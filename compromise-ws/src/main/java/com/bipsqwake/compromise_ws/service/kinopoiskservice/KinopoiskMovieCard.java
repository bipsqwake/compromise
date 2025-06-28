package com.bipsqwake.compromise_ws.service.kinopoiskservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class KinopoiskMovieCard {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String alternativeName;
    @JsonProperty
    private String description;
    @JsonProperty
    private ImageHolder poster;

    public String getImgUrl() {
        if (poster == null) {
            return null;
        }
        return poster.url;
    }

    public String getName() {
        return name != null ? name : alternativeName;
    }

    public String getDescription() {
        if (description == null) {
            return "";
        }
        return description.substring(0, Math.min(description.length(), 200));
    }
    

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class ImageHolder {
        @JsonProperty
        private String url;
        @JsonProperty
        private String previewUrl;
    }
}
