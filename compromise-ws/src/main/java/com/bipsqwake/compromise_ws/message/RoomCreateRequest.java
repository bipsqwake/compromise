package com.bipsqwake.compromise_ws.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoomCreateRequest(@JsonProperty String name) {
    
}
