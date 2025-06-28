package com.bipsqwake.compromise_ws.service.kinopoiskservice;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class KinopoiskPage {
    @JsonProperty
    List<KinopoiskMovieCard> docs;
}
