package com.bipsqwake.compromise_ws.service.teseraservice;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class TeseraGame {
    @JsonProperty
    private String id;
    @JsonProperty
    private String teseraId;
    @JsonProperty
    private String title;
    @JsonProperty 
    private String title2;
    @JsonProperty
    private String descriptionShort;
    @JsonProperty
    private String photoUrl;

}
