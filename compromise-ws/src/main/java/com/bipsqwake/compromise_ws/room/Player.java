package com.bipsqwake.compromise_ws.room;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Player {
    @JsonProperty
    private String id;
    @JsonProperty
    private String session;
    @JsonProperty
    private String name;
}
