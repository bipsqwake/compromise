package com.bipsqwake.compromise_ws.service.teseraservice;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.bipsqwake.compromise_ws.exception.InvalidInputException;
import com.bipsqwake.compromise_ws.room.Card;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TeseraService {
    private static final int BATCH_SIZE = 100;

    private final RestClient restClient;

    public TeseraService(RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder
                .baseUrl("https://api.tesera.ru/v1/")
                .build();
    }

    public List<Card> getCardsFromCollection(String username, int playersNum) throws InvalidInputException {
        List<TeseraGame> bgList = getTeseraGames(username, playersNum);
        return teseraListToCardList(bgList);
    }

    private List<TeseraGame> getTeseraGames(String username, int playersNum) {
        List<TeseraGame> result = new LinkedList<>();
        int offset = 0;
        while (true) {
            List<TeseraGame> tmp = restClient
                    .get()
                    .uri("/collections/base/own/" + username + "?GamesType=SelfGame&Limit=" + BATCH_SIZE + "&Offset=" + offset)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TeseraGameCard>>() {
                    }).stream()
                    .map(card -> card.getGame()).collect(Collectors.toList());
            if (tmp.isEmpty()) {
                break;
            } else {
                result.addAll(tmp);
                offset += BATCH_SIZE;
            }
        }
        return result;
    }

    private static List<Card> teseraListToCardList(List<TeseraGame> list) {
        return list.stream().map(TeseraService::teseraGameToCard).collect(Collectors.toList());
    }

    private static Card teseraGameToCard(TeseraGame game) {
        return new Card(
            UUID.randomUUID().toString(),
            game.getTitle(),
            game.getDescriptionShort(),
            game.getPhotoUrl()
        );
    }
}
