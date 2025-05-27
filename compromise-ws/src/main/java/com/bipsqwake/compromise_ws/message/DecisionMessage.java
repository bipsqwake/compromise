package com.bipsqwake.compromise_ws.message;

import com.bipsqwake.compromise_ws.room.Decision;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DecisionMessage(@JsonProperty String cardId, @JsonProperty Decision decision) {
    
}
