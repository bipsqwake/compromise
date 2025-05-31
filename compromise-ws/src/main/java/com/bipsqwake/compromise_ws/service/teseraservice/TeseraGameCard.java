package com.bipsqwake.compromise_ws.service.teseraservice;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class TeseraGameCard {
    @JsonProperty
    private TeseraGame game;
}
