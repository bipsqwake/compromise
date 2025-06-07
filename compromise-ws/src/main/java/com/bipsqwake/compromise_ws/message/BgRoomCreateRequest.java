package com.bipsqwake.compromise_ws.message;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BgRoomCreateRequest(@JsonProperty String name, @JsonProperty String username, @JsonProperty int playersNum, @JsonProperty SourceType source) {
    
    public enum SourceType {
        BGG,
        TESERA
    }

    public boolean isValid() {
        return Arrays.asList(name, username).stream().allMatch(e -> e != null && !e.isBlank()) && playersNum != 0;
    }
}
