package com.bipsqwake.compromise_ws.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.bipsqwake.compromise_ws.message.PlayersResponse;
import com.bipsqwake.compromise_ws.message.PlayersResponse.Action;
import com.bipsqwake.compromise_ws.room.Card;
import com.bipsqwake.compromise_ws.room.GameState;
import com.bipsqwake.compromise_ws.room.Room;
import com.bipsqwake.compromise_ws.room.exception.RoomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoomService {

    private static final String PLAYERS_DESTINATION = "/topic/room/%s/players";
    private static final String CARDS_DESTINATION = "/queue/cards";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Map<String, Room> rooms = new ConcurrentSkipListMap<>();

    public String createRoom(List<Card> cards) {
        Room room = new Room(cards);
        rooms.put(room.getId(), room);
        return room.getId();
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Set<String> getRoomsIds() {
        return rooms.keySet();
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
            messagingTemplate.convertAndSend(String.format(PLAYERS_DESTINATION, roomId),
                    new PlayersResponse(Action.CONNECTED, playerName, playersNames));
        }
        return id;
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
            for (String playerSession : startCards.keySet()) {
                for (Card card: startCards.get(playerSession)) {
                    log.info("Sending card {} to session {}", card.id(), playerSession);
                    messagingTemplate.convertAndSendToUser(playerSession, CARDS_DESTINATION, card, createHeaders(playerSession));
                }
            }
        }
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}
