package com.bipsqwake.compromise_ws.room;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoomStatus(@JsonProperty String id,
        @JsonProperty GameState state,
        @JsonProperty String name,
        @JsonProperty LocalDateTime lastUsed,
        @JsonProperty List<Player> players) { }
