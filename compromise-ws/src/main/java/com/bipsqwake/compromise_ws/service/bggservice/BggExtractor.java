package com.bipsqwake.compromise_ws.service.bggservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.bipsqwake.compromise_ws.exception.InvalidInputException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BggExtractor {

    private final RestClient restClient;

    BggExtractor(RestClient.Builder restClientBuilder, @Value("${external.bgg}") String bggUrl) {
        this.restClient = restClientBuilder
                .baseUrl(bggUrl)
                .messageConverters(converters -> {
                    Jaxb2RootElementHttpMessageConverter converter = new Jaxb2RootElementHttpMessageConverter();
                    converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_XML, MediaType.TEXT_XML));
                    converters.add(converter);
                })
                .build();
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 300))
    public BggBoardGameList extractList(String username) throws InvalidInputException {
        return restClient
                .get()
                .uri("/collection?own=1&stats=1&excludesubtype=boardgameexpansion&username=" + username)
                .retrieve()
                .onStatus((status) -> status == HttpStatus.ACCEPTED, (request, response) -> {
                    log.info("Accepted on {}", request.getURI());
                    throw new RestClientException("Retry");
                })
                .body(BggBoardGameList.class);
    }
}
