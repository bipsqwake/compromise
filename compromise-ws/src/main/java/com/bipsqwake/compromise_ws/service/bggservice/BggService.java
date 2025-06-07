package com.bipsqwake.compromise_ws.service.bggservice;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.bipsqwake.compromise_ws.exception.InvalidInputException;
import com.bipsqwake.compromise_ws.exception.WebException;
import com.bipsqwake.compromise_ws.room.Card;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BggService {

    @Autowired
    BggExtractor bggExtractor;

    public List<Card> getCardsFromCollection(String username, int playersNum) throws InvalidInputException, WebException {
        BggBoardGameList bgList;
        try {
            bgList = bggExtractor.extractList(username);
        } catch (RestClientException e) {
            log.warn("Failed to extract bgg list: {}", e.getMessage());
            throw WebException.extractionFailed(String.format("Failed to extract collection of %s", username));
        }
        return bggListToCards(bgList, playersNum);
    }

    private List<Card> bggListToCards(BggBoardGameList list, int playersNum) {
        return list.getGames().stream()
                .filter(game -> game.isSuitableForPlayersNum(playersNum))
                .map(BggService::bggGameToCard)
                .collect(Collectors.toList());
    }

    static private Card bggGameToCard(BggBoardGame game) {
        return new Card(
                UUID.randomUUID().toString(),
                game.getName(),
                game.getRankDescription(),
                game.getImage());
    }
}
