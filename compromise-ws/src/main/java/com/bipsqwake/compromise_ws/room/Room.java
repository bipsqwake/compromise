package com.bipsqwake.compromise_ws.room;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import com.bipsqwake.compromise_ws.room.exception.RoomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Room {

    // constants
    private static final int MAX_PLAYERS = 10;
    // initials
    private final String id;
    private final List<Card> cards;
    private final ConcurrentSkipListMap<String, Map<String, Decision>> decisions;
    // state
    private String name;
    private Map<String, Player> players = new ConcurrentSkipListMap<>();
    private Set<String> priorityCards = new ConcurrentSkipListSet<>();
    private GameState state = GameState.PREPARE;
    private Card selectedCard = null;
    private String adminId;
    private volatile LocalDateTime lastUsed;
    // tools
    private static final Random random = new Random();

    public Room(String name, List<Card> cards) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.cards = cards;
        this.decisions = new ConcurrentSkipListMap<>();
        this.lastUsed = LocalDateTime.now();
        cards.forEach(card -> decisions.put(card.id(), new HashMap<>()));
    }

    // last used

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    // basic getters

    public GameState getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RoomStatus getStatus() {
        return new RoomStatus(id, state, name, lastUsed, players.values().stream().toList());
    }

    // player adders/removers
    public String addPlayer(String name, String session) throws RoomException {
        if (state != GameState.PREPARE) {
            log.error("Failed to add player $s: invalid game state", name);
            throw new RoomException("Game should be in PREPARE state to add player");
        }
        if (players.size() >= MAX_PLAYERS) {
            log.error("Failed to add player $s: Exceeded max players", name);
            throw new RoomException("Already have max players");
        }
        updateLastUsed();

        String playerId = UUID.randomUUID().toString();
        players.put(playerId, new Player(playerId, session, name));
        log.info("Added player {} with id {}", name, playerId);
        return playerId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void removePlayer(String id) {
        // need to check if player is in?
        if (!isPlayerPresent(id)) {
            return;
        }
        updateLastUsed();
        players.remove(id);
        if (state == GameState.IN_PROGRESS) {
            synchronized (decisions) {
                decisions.forEach((cardId, decisionsMap) -> decisionsMap.remove(id));
            }
        }
    }

    public Set<String> getPlayerIds() {
        return players.keySet();
    }

    public List<String> getPlayerNames() {
        return players.values().stream().map(Player::getName).collect(Collectors.toList());
    }

    public List<String> getPlayerSessions() {
        return players.values().stream().map(Player::getSession).collect(Collectors.toList());
    }

    public String getPlayerIdBySession(String session) {
        Player player = players.values().stream()
                .filter(p -> p.getSession().equals(session))
                .findAny()
                .orElse(null);
        return player == null ? null : player.getId();
    }

    public Player getPlayer(String id) {
        return players.get(id);
    }

    // game methods

    public Map<String, List<Card>> start() throws RoomException {
        if (state != GameState.PREPARE) {
            throw new RoomException("Game should be in PREPARE state to start");
        }
        updateLastUsed();
        state = GameState.IN_PROGRESS;
        Map<String, List<Card>> result = new HashMap<>();
        for (String playerId : players.keySet()) {
            List<Card> cards = getInitialCards(playerId);
            if (cards == null) {
                throw new RoomException("Initial null draw. Can't start game");
            }
            result.put(players.get(playerId).getSession(), cards);
        }
        log.info("Game {} started; Initial cards: {}", id, result);
        return result;
    }

    public void processDecision(String cardId, String playerId, Decision decision) throws RoomException {
        if (state != GameState.IN_PROGRESS) {
            throw new RoomException("Game should be in IN_PROGRESS state to process decision");
        }
        if (!isPlayerPresent(playerId)) {
            throw new RoomException(String.format("Player %s is not present in game", playerId));
        }
        updateLastUsed();
        synchronized (decisions) {
            Map<String, Decision> cardDecisions = decisions.get(cardId);
            cardDecisions.put(playerId, decision);
            // priority update
            if (decision == Decision.NOT_OK) {
                priorityCards.remove(cardId);
            }
            if (decision == Decision.OK && !cardDecisions.values().contains(Decision.NOT_OK)) {
                priorityCards.add(cardId);
            }
            log.info("Player {}:{} decided that {}:{} is {}", playerId, players.get(playerId).getName(), cardId,
                    getCardById(cardId).name(), decision);
            log.info("Decision map {}", decisions);
        }
    }

    public synchronized boolean finishCheck() {
        if (state == GameState.FINISHED) {
            return true;
        }
        // if (finishedPlayersIds.size() == players.size()) {
        // state = GameState.FINISHED;
        // return true;
        // }
        updateLastUsed();
        synchronized (decisions) {
            boolean cardsLeft = decisions.entrySet().stream()
                    .anyMatch(entry -> !entry.getValue().containsValue(Decision.NOT_OK));
            if (!cardsLeft) {
                state = GameState.FINISHED;
                return true;
            }
            String selectedCardId = decisions.entrySet().stream()
                    .filter(entry -> entry.getValue().size() >= players.size())
                    .filter(entry -> entry.getValue().values().stream().allMatch(val -> val == Decision.OK))
                    .map(entry -> entry.getKey())
                    .findAny().orElse(null);
            if (selectedCardId != null) {
                selectedCard = getCardById(selectedCardId);
                state = GameState.FINISHED;
                return true;
            }
        }
        return false;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public Card getNextCardForPlayer(String playerId) throws RoomException {
        if (!isPlayerPresent(playerId)) {
            throw new RoomException(String.format("Player %s is not present in game", playerId));
        }
        updateLastUsed();
        
        synchronized (decisions) {
            Card nextCard = getNextPriorityCard(playerId);
            if (nextCard == null) {
                nextCard = getNextDefaultCard(playerId);
            }
            if (nextCard == null) {
                return null;
            }
            Map<String, Decision> cardDecisions = decisions.get(nextCard.id());
            cardDecisions.put(playerId, Decision.PENDING);
            return nextCard;
        }
    }

    private Card getNextPriorityCard(String playerId) {
        if (!priorityCards.isEmpty()) {
            String priorityCardId = priorityCards.stream()
                    .filter(id -> !decisions.get(id).containsKey(playerId))
                    .findFirst().orElse(null);
            if (priorityCardId != null) {
                return getCardById(priorityCardId);
            }
        }
        return null;
    }

    private Card getNextDefaultCard(String playerId) {
        List<Card> deck;
        deck = cards.stream()
                .filter(card -> canShowCardToPlayer(playerId, decisions.get(card.id())))
                .filter(Objects::nonNull)
                .toList();
        if (deck == null || deck.isEmpty()) {
            return null;
        }
        Card result = deck.get(random.nextInt(deck.size()));
        return result;
    }

    private List<Card> getInitialCards(String playerId) throws RoomException {
        if (!isPlayerPresent(playerId)) {
            throw new RoomException(String.format("Player %s not requested in game", playerId));
        }
        List<Card> deck;
        updateLastUsed();
        synchronized (decisions) {
            deck = cards.stream()
                    .filter(Objects::nonNull)
                    .toList();
        }
        if (deck.size() < 2) {
            throw new RoomException("Can't start game with less than 2 cards");
        }
        int first = random.nextInt(deck.size());
        int second = random.nextInt(deck.size());
        while (first == second) {
            second = random.nextInt(deck.size());
        }
        return Arrays.asList(deck.get(first), deck.get(second));
    }

    protected boolean canShowCardToPlayer(String playerId, Map<String, Decision> decisions) {
        if (decisions.containsValue(Decision.NOT_OK)) {
            return false;
        }
        if (decisions.containsKey(playerId)) {
            return false;
        }
        return true;
    }

    // private

    private Card getCardById(String id) {
        return cards.stream().filter(card -> card.id().equals(id)).findFirst().orElse(null);
    }

    private boolean isPlayerPresent(String playerId) {
        return players.containsKey(playerId);
    }

    private void updateLastUsed() {
        this.lastUsed = LocalDateTime.now();
    }
}
