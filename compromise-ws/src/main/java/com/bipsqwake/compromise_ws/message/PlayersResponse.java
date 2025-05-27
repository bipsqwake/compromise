package com.bipsqwake.compromise_ws.message;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayersResponse {

    @JsonProperty
    private Action action;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<String> playerNames;

    public enum Action {
        CONNECTED,
        DISCONNECTED
    }
}
