package com.bipsqwake.compromise_ws.service.kinopoiskservice;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.bipsqwake.compromise_ws.exception.WebException;
import com.bipsqwake.compromise_ws.room.Card;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KinopoiskService {

    private static final int BATCH_SIZE = 50;

    @Autowired
    private KinopoiskExtractor kinopoiskExtractor;

    public List<Card> getMovies(KinopoiskQuery query) throws WebException {
        query.setWithPosterOnly(true);
        query.setWithDescriptionOnly(true);
        List<KinopoiskMovieCard> result;
        try {
            result = kinopoiskExtractor.extractMovies(query, 1, BATCH_SIZE);
        } catch (RestClientException e) {
            log.warn("Failed to extract movies: " + e.getMessage());
            throw WebException.extractionFailed(String.format("Failed to extract movies"));
        }
        return result.stream().map(KinopoiskService::kinopoiskCardToCard).toList();
    }

    private static Card kinopoiskCardToCard(KinopoiskMovieCard kinopoiskCard) {
        return new Card(UUID.randomUUID().toString(),
                kinopoiskCard.getName(),
                kinopoiskCard.getDescription(),
                kinopoiskCard.getImgUrl());
    }
}
