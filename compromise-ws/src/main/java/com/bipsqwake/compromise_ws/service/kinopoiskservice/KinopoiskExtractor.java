package com.bipsqwake.compromise_ws.service.kinopoiskservice;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KinopoiskExtractor {

    private final RestClient restClient;

    @Value("${external.kinopoiskKey}")
    String apiKey;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String MOVIE_PATH = "movie";

    KinopoiskExtractor(RestClient.Builder restClientBuilder, @Value("${external.kinopoisk}") String kinopoiskApiUrl) {
        this.restClient = restClientBuilder
                .baseUrl(kinopoiskApiUrl)
                .build();
    }

    @RateLimiter(name = "teseraLimiter")
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 300))
    List<KinopoiskMovieCard> extractMovies(KinopoiskQuery query, int page, int limit) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(MOVIE_PATH);
        uriBuilder.queryParams(query.getPaginatedMapQuery(page, limit));

        URI uri = uriBuilder.encode(Charset.defaultCharset()).build().toUri();

        String strictlyEscapedQuery = uri.getRawQuery()
                .replaceAll("\\+", "%2B")
                .replaceAll("!", "%21");
        URI uriFixed = UriComponentsBuilder.fromUri(uri)
                .replaceQuery(strictlyEscapedQuery)
                .build(true).toUri();
                
        log.info(uriFixed.toString());

        KinopoiskPage resultPage = restClient
                .get()
                .uri(uriFixed)
                .header(API_KEY_HEADER, apiKey)
                .retrieve()
                .body(KinopoiskPage.class);

        return resultPage == null ? null : resultPage.docs;
    }
}
