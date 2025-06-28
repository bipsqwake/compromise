package com.bipsqwake.compromise_ws.service.teseraservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TeseraExtractor {
    private static final int BATCH_SIZE = 100;

    private final RestClient restClient;

    public TeseraExtractor(RestClient.Builder restClientBuilder, @Value("${external.tesera}") String teseraUrl) {
        this.restClient = restClientBuilder
                .baseUrl(teseraUrl)
                .build();
    }

    @RateLimiter(name = "teseraLimiter")
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 300))
    List<TeseraGameCard> extractGames(String username, int page) {
        return restClient
                .get()
                .uri("/collections/base/own/" + username + "?GamesType=SelfGame&Limit=" + BATCH_SIZE + "&Offset="
                        + page * BATCH_SIZE)
                .retrieve()
                .body(new ParameterizedTypeReference<List<TeseraGameCard>>() {
                });
    }

    @RateLimiter(name = "teseraLimiter")
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 300))
    TeseraGameCard getFullCard(TeseraGame game) {
        return restClient
                .get()
                .uri("/games/" + game.getAlias())
                .retrieve()
                .body(new ParameterizedTypeReference<TeseraGameCard>() {
                });
    }


}
