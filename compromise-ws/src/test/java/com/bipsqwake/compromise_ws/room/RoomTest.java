package com.bipsqwake.compromise_ws.room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.bipsqwake.compromise_ws.room.exception.RoomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoomTest {

    @Test
    public void positive() throws RoomException {
        Room room = new Room("Test", getCardList());
        Assertions.assertNotNull(room.getId(), "Room should have not null id");
        Assertions.assertEquals(GameState.PREPARE, room.getState(), "Expected room to init in PREPARE state");

        PlayerMock player1 = new PlayerMock("player1", "6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc90");
        PlayerMock player2 = new PlayerMock("player2", "6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc90");
        PlayerMock player3 = new PlayerMock("player3", "6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc90");

        player1.setId(room.addPlayer(player1.getName(), UUID.randomUUID().toString()));
        player2.setId(room.addPlayer(player2.getName(), UUID.randomUUID().toString()));
        player3.setId(room.addPlayer(player3.getName(), UUID.randomUUID().toString()));

        List<PlayerMock> playerMocks = Arrays.asList(player1, player2, player3);

        Map<String, List<Card>> startCards = room.start();
        playerMocks.forEach(pm -> startCards.get(pm.getId()).forEach(card -> pm.addToQueue(card.id())));

        int i = 0;
        int maxTurns = 20;
        while (room.getState() != GameState.FINISHED && i < maxTurns) {
            PlayerMock current = playerMocks.get((i++) % playerMocks.size());
            log.info("Client: Player {} makes his turn", current.getName());
            String[] decision = current.decision();
            if (decision == null) {
                log.info("Player {}:{} finished cards", current.getId(), current.getName());
                continue;
            }
            log.info("Client: Player {} decided that {} is {}", current.getName(), decision[0], decision[1]);
            room.processDecision(decision[0], current.getId(), Decision.valueOf(decision[1]));
            Card nextCard = room.getNextCardForPlayer(current.getId());
            if (nextCard == null) {
                log.info("Client: next card for player is not available");
            } else {
                current.addToQueue(nextCard.id());
            }
            room.finishCheck();
        }
        Assertions.assertTrue(i < maxTurns);
        Card resultCard = room.getSelectedCard();
        log.info("Game finished in {} turns; Selected id: {}", i, resultCard.id());
        Assertions.assertEquals("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc90", resultCard.id(), "Wrong card picked");
    }

    private List<Card> getCardList() {
        List<Card> result = new ArrayList<>();
        result.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc90", "0", "Zero", "https://img.pixers.pics/pho_wat(s3:700/FO/57/91/04/67/700_FO57910467_c3eb65d3d570cac084541479f0dea2c7.jpg,700,700,cms:2018/10/5bd1b6b8d04b8_220x50-watermark.png,over,480,650,jpg)/stickers-fire-alphabet-number-0-zero.jpg.jpg"));
        result.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc91", "1", "One", "https://image.spreadshirtmedia.net/image-server/v1/products/T1459A839PA4459PT28D195809042W8333H10000/views/1,width=550,height=550,appearanceId=839,backgroundColor=F2F2F2/number-1-number-sticker.jpg"));
        result.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc92", "2", "Two", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd8OENc93qPZc5n1TnO_OLh5-XxTpoto-sgw&s"));
        result.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc93", "3", "Fo.. Just kidding. Three", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQs18Alp46dvHqWIpIvj7tjqhovdcdeoTN-1w&s"));
        result.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc94", "4", "Five", "https://img.uline.com/is/image/uline/S-25441-4?$Mobile_Zoom$"));
        return result;
    }

    class PlayerMock {
        final List<String> wanted;

        private String id;
        private String name;
        private Queue<String> cardQueue = new LinkedList<>();

        PlayerMock(String name, String... wanted) {
            this.name = name;
            this.wanted = Arrays.asList(wanted);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void addToQueue(String cardId) {
            cardQueue.add(cardId);
        }

        public String[] decision() {
            if (cardQueue.isEmpty()) {
                return null;
            }
            String cardId = cardQueue.poll();
            return new String[] { cardId, wanted.contains(cardId) ? "OK" : "NOT_OK" };
        }
    }
}
