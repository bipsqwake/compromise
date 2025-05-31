package com.bipsqwake.compromise_ws.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BgRoomCreateRequest(@JsonProperty String name, @JsonProperty String username, @JsonProperty int playersNum, @JsonProperty SourceType source) {
    
    public enum SourceType {
        BGG,
        TESERA
    }
}
