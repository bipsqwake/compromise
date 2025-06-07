package com.bipsqwake.compromise_ws.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(@JsonProperty String error, @JsonProperty String errorDescription) {

}
