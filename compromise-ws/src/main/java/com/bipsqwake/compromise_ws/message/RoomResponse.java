package com.bipsqwake.compromise_ws.message;

import com.bipsqwake.compromise_ws.room.GameState;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RoomResponse(@JsonProperty String id,
                        @JsonProperty String name,
                        @JsonProperty GameState state) {
    
}
