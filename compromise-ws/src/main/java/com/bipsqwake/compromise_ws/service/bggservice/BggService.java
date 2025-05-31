package com.bipsqwake.compromise_ws.service.bggservice;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.bipsqwake.compromise_ws.exception.InvalidInputException;
import com.bipsqwake.compromise_ws.room.Card;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BggService {

    private final RestClient restClient;

    public BggService(RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder
                .baseUrl("https://boardgamegeek.com/xmlapi2")
                .messageConverters(converters -> {
                    Jaxb2RootElementHttpMessageConverter converter = new Jaxb2RootElementHttpMessageConverter();
                    converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_XML, MediaType.TEXT_XML));
                    converters.add(converter);
                })
                .build();
    }

    public List<Card> getCardsFromCollection(String username, int playersNum) throws InvalidInputException {
        log.info(username);
        BggBoardGameList bgList;
        try {
            bgList = restClient
                .get()
                .uri("/collection?username=" + username)
                .retrieve()
                .body(BggBoardGameList.class);
        } catch (RestClientException e) {
            log.info("Failed to extract list of games from user " + username);
            throw new InvalidInputException("Failed to get list");
        }
        
        return bggListToCards(bgList);
    }

    private List<Card> bggListToCards(BggBoardGameList list) {
        return list.getGames().stream().map(BggService::bggGameToCard).collect(Collectors.toList());
    }

    static private Card bggGameToCard(BggBoardGame game) {
        return new Card(
            UUID.randomUUID().toString(),
            game.getName(),
            game.getOriginalName(),
            game.getImage()
        );
    }
}
