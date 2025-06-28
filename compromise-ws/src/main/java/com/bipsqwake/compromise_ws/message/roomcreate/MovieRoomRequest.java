package com.bipsqwake.compromise_ws.message.roomcreate;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MovieRoomRequest(
    @JsonProperty
    @NotBlank
    String name,

    @JsonProperty 
    @NotBlank
    Type type,

    @JsonProperty
    boolean genresExclude,

    @JsonProperty
    Set<Genre> genres,

    @JsonProperty
    boolean countriesExclude,

    @JsonProperty
    Set<Countries> countries,

    @JsonProperty
    Set<Service> services,

    @JsonProperty()
    @Min(value = 0, message = "minimum rating is 0")
    @Max(value = 10, message = "maximum rating is 10")
    Integer minRating,

    @JsonProperty()
    Integer yearFrom,

    @JsonProperty
    Integer yearTo
    
    
    
) {
    public enum Type {
        MOVIE,
        TV_SHOW
    }

    public enum Genre {
        THRILLER,
        HORROR,
        ACTION,
        COMEDY,
        FANTASTIC,
        FANTASY,
        DETECTIVE,
        DRAMA
    }

    public enum Countries {
        RUSSIA,
        USA,
        SPAIN,
        FRANCE
    }

    public enum Service {
        IVI,
        KION,
        KINOPOISK,
        START,
        OKKO,
        PREMIERE
    }
}
