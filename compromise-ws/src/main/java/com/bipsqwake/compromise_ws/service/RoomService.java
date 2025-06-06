package com.bipsqwake.compromise_ws.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bipsqwake.compromise_ws.message.CloseRoomMesssage;
import com.bipsqwake.compromise_ws.message.PlayersResponse;
import com.bipsqwake.compromise_ws.message.PlayersResponse.Action;
import com.bipsqwake.compromise_ws.message.status.FinishMessage;
import com.bipsqwake.compromise_ws.message.status.StatusMessage;
import com.bipsqwake.compromise_ws.message.status.StatusMessage.Status;
import com.bipsqwake.compromise_ws.room.Card;
import com.bipsqwake.compromise_ws.room.Decision;
import com.bipsqwake.compromise_ws.room.GameState;
import com.bipsqwake.compromise_ws.room.Player;
import com.bipsqwake.compromise_ws.room.Room;
import com.bipsqwake.compromise_ws.room.RoomStatus;
import com.bipsqwake.compromise_ws.room.exception.RoomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoomService {

    private static final String PLAYERS_DESTINATION = "/topic/room/%s/players";
    private static final String STATUS_DESTINATION = "/topic/room/%s/status";
    private static final String CARDS_USER_DESTINATION = "/queue/cards";
    private static final String ADMIN_NOTIFICATION_USER_DESTINATION = "/queue/admin";
    private static final String CLOSE_CONNECTION_DESTINATION = "topic/room/%s/close";

    private static final long ALIVE_ROOM_TIMEOUT = 5;
    private static final long DEAD_ROOM_TIMEOUT = 1;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Map<String, Room> rooms = new ConcurrentSkipListMap<>();

    public Room createRoom(String name, List<Card> cards) {
        Room room = new Room(name, cards);
        rooms.put(room.getId(), room);
        return room;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Set<String> getRoomsIds() {
        return rooms.keySet();
    }

    public List<RoomStatus> getRoomsStatus() {
        return rooms.values().stream().map(Room::getStatus).toList();
    }

    public String addPlayerToRoom(String roomId, String playerName, String playerSession) throws RoomException {
        if (roomId == null) {
            throw new RoomException("Invalid room id format");
        }
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RoomException(String.format("No room with id %s", roomId));
        }
        String id;
        synchronized (room) {
            id = room.addPlayer(playerName, playerSession);
            List<String> playersNames = room.getPlayerNames();
            if (room.getPlayerIds().size() == 1) {
                room.setAdminId(id);
                messagingTemplate.convertAndSendToUser(playerSession, ADMIN_NOTIFICATION_USER_DESTINATION,
                        "{\"admin\": true}",
                        createHeaders(playerSession));
            }
            messagingTemplate.convertAndSend(String.format(PLAYERS_DESTINATION, roomId),
                    new PlayersResponse(Action.CONNECTED, playerName, playersNames));
        }
        return id;
    }

    public void removePlayerFromRoomsBySession(String sessionId) throws RoomException {
        List<Room> roomsToRemoveUser;
        synchronized (rooms) {
            roomsToRemoveUser = rooms.values().stream()
                    .filter(room -> room.getPlayerSessions().contains(sessionId))
                    .toList();
        }
        for (Room room : roomsToRemoveUser) {
            removePlayerFromRoomBySession(sessionId, room.getId());
        }
    }

    public void removePlayerFromRoomBySession(String sessionId, String roomId) throws RoomException {
        if (sessionId == null) {
            throw new RoomException("Invalid session ID");
        }
        if (roomId == null) {
            throw new RoomException("Invalid room id format");
        }
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RoomException(String.format("No room with id %s", roomId));
        }
        removePlayerFromRoom(room.getPlayerIdBySession(sessionId), roomId);
    }

    public void removePlayerFromRoom(String playerId, String roomId) throws RoomException {
        if (playerId == null) {
            throw new RoomException("Invalid player ID");
        }
        if (roomId == null) {
            throw new RoomException("Invalid room id format");
        }
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RoomException(String.format("No room with id %s", roomId));
        }
        synchronized (room) {
            // check if user is admin
            boolean needToChangeAdmin = playerId.equals(room.getAdminId());

            // remove player from room
            Player playerToRemove = room.getPlayer(playerId);
            if (playerToRemove == null) {
                log.info("No player with id %s to remove", playerId);
                return;
            }
            room.removePlayer(playerId);

            // notify about disconnect
            List<String> playersNames = room.getPlayerNames();
            messagingTemplate.convertAndSend(String.format(PLAYERS_DESTINATION, roomId),
                    new PlayersResponse(Action.DISCONNECTED, playerToRemove.getName(), playersNames));

            // setNewAdmin
            if (needToChangeAdmin) {
                String nextAdminId = room.getPlayerIds().stream().findFirst().orElse(null);
                if (nextAdminId != null) {
                    Player nextAdmin = room.getPlayer(nextAdminId);
                    room.setAdminId(nextAdminId);
                    messagingTemplate.convertAndSendToUser(nextAdmin.getSession(), ADMIN_NOTIFICATION_USER_DESTINATION,
                            "{\"admin\": true}",
                            createHeaders(nextAdmin.getSession()));
                }
            }
        }
    }

    public void startRoom(String roomId) throws RoomException {
        if (roomId == null) {
            throw new RoomException("Invalid room id format");
        }
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RoomException(String.format("No room with id %s", roomId));
        }
        if (room.getState() != GameState.PREPARE) {
            return;
        }
        synchronized (room) {
            if (room.getState() != GameState.PREPARE) {
                return;
            }
            Map<String, List<Card>> startCards = room.start();
            messagingTemplate.convertAndSend(String.format(STATUS_DESTINATION, room.getId()),
                    new StatusMessage(Status.STARTED));
            for (String playerSession : startCards.keySet()) {
                for (Card card : startCards.get(playerSession)) {
                    log.info("Sending card {} to session {}", card.id(), playerSession);
                    messagingTemplate.convertAndSendToUser(playerSession, CARDS_USER_DESTINATION, card,
                            createHeaders(playerSession));
                }
            }
        }
    }

    public void processDecision(String roomId, String playerId, String cardId, Decision decision) throws RoomException {
        if (roomId == null) {
            throw new RoomException("Invalid room id format");
        }
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RoomException(String.format("No room with id %s", roomId));
        }
        if (room.getState() != GameState.IN_PROGRESS) {
            return;
        }
        synchronized (room) {
            if (room.getState() != GameState.IN_PROGRESS) {
                return;
            }
            room.processDecision(cardId, playerId, decision);
            boolean finished = room.finishCheck();
            if (!finished) {
                Card nextCard = room.getNextCardForPlayer(playerId);
                if (nextCard == null) {
                    if (room.finishCheck()) {
                        processFinish(room);
                    }
                    return;
                }
                String playerSession = room.getPlayer(playerId).getSession();
                log.info("Sending card {} to session {}", nextCard.id(), playerSession);
                messagingTemplate.convertAndSendToUser(playerSession, CARDS_USER_DESTINATION, nextCard,
                        createHeaders(playerSession));
            } else {
                processFinish(room);
            }
        }
    }

    private void processFinish(Room room) throws RoomException {
        if (room == null) {
            return;
        }
        Card selectedCard = room.getSelectedCard();

        FinishMessage finishMessage = selectedCard != null ? new FinishMessage(selectedCard) : new FinishMessage();
        messagingTemplate.convertAndSend(String.format(STATUS_DESTINATION, room.getId()), finishMessage);
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    // rooms clean out
    @Scheduled(fixedDelayString = "${appconfig.clean-period}")
    public void clean() {
        List<Room> toRemove = rooms.values().stream()
                .filter(RoomService::needToRemove)
                .toList();
        toRemove.forEach(room -> {
            notifyRoomClosed(room, "Timeout");
            rooms.remove(room.getId());
        });
    }

    private void notifyRoomClosed(Room room, String reason) {
        if (room == null) {
            return;
        }
        CloseRoomMesssage message = new CloseRoomMesssage(reason);
        messagingTemplate.convertAndSend(String.format(CLOSE_CONNECTION_DESTINATION, room.getId()), message);
    }

    private static boolean needToRemove(Room room) {
        long diff = room.getLastUsed().until(LocalDateTime.now(), ChronoUnit.MINUTES);
        log.info("Room {}:{} is stale for {} minutes", room.getId(), room.getName(), diff);
        return (diff > ALIVE_ROOM_TIMEOUT)
                || (diff > DEAD_ROOM_TIMEOUT
                        && (room.getState() == GameState.FINISHED || room.getPlayerIds().isEmpty()));
    }
}
