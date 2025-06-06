package com.bipsqwake.compromise_ws.service.teseraservice;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.bipsqwake.compromise_ws.exception.InvalidInputException;
import com.bipsqwake.compromise_ws.room.Card;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TeseraService {

    @Autowired
    TeseraExtractor teseraExtractor;

    public List<Card> getCardsFromCollection(String username, int playersNum) throws InvalidInputException {
        List<TeseraGame> result = new LinkedList<>();
        List<TeseraGameCard> tmp;
        int page = 0;
        try {
            do {
                tmp = teseraExtractor.extractGames(username, page++);
                result.addAll(tmp.stream().map(TeseraGameCard::getGame).toList());
            } while (!tmp.isEmpty());
        } catch (RestClientException e) {
            throw new InvalidInputException("Failed to extract Tesera list");
        }
        return teseraListToCardList(result.stream()
                .map(game -> teseraExtractor.getFullCard(game).getGame())
                .toList(), playersNum);
    }

    private static List<Card> teseraListToCardList(List<TeseraGame> list, int playersNum) {
        return list.stream()
                .filter(game -> game.isSuitableForPlayersNum(playersNum))
                .map(TeseraService::teseraGameToCard)
                .collect(Collectors.toList());
    }

    private static Card teseraGameToCard(TeseraGame game) {
        return new Card(
                UUID.randomUUID().toString(),
                game.getTitle(),
                game.getDescriptionShort(),
                game.getPhotoUrl());
    }
}
