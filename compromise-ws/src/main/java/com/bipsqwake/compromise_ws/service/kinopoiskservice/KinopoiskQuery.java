package com.bipsqwake.compromise_ws.service.kinopoiskservice;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.bipsqwake.compromise_ws.message.roomcreate.MovieRoomRequest;


public class KinopoiskQuery {
    private String type;
    private Set<String> genres;
    private Set<String> countries;
    private Set<String> services;
    private Integer yearFrom;
    private Integer yearTo;
    private Integer minRating;
    private boolean onlyWithPoster;
    private boolean onlyWithDescription;

    private static final Map<MovieRoomRequest.Type, String> TYPE_MAP = Map.of(
        MovieRoomRequest.Type.MOVIE, "movie",
        MovieRoomRequest.Type.TV_SHOW, "tv-series");

    private static final Map<MovieRoomRequest.Genre, String> GENRE_MAP = Map.of(
        MovieRoomRequest.Genre.THRILLER, "триллер",
        MovieRoomRequest.Genre.HORROR, "ужасы",
        MovieRoomRequest.Genre.ACTION, "боевик",
        MovieRoomRequest.Genre.COMEDY, "комедия",
        MovieRoomRequest.Genre.DETECTIVE, "детектив",
        MovieRoomRequest.Genre.DRAMA, "драма",
        MovieRoomRequest.Genre.FANTASTIC, "фантастика",
        MovieRoomRequest.Genre.FANTASY, "фентези"
    );

    private static final Map<MovieRoomRequest.Countries, String> COUNTIRES_MAP = Map.of(
        MovieRoomRequest.Countries.RUSSIA, "Россия",
        MovieRoomRequest.Countries.USA, "США",
        MovieRoomRequest.Countries.FRANCE, "Франция",
        MovieRoomRequest.Countries.SPAIN, "Испания"
    );

    private static final Map<MovieRoomRequest.Service, String> SERVICES_MAP = Map.of(
        MovieRoomRequest.Service.KINOPOISK, "kinopoisk",
        MovieRoomRequest.Service.IVI, "ivi",
        MovieRoomRequest.Service.KION, "kion",
        MovieRoomRequest.Service.OKKO, "okko",
        MovieRoomRequest.Service.START, "start",
        MovieRoomRequest.Service.PREMIERE, "premiere"
    );

    public void mapType(MovieRoomRequest.Type type) {
        if (!TYPE_MAP.containsKey(type)) {
            return;
        }
        this.type = TYPE_MAP.get(type);
    }

    public void mapGenres(Set<MovieRoomRequest.Genre> genres, boolean exclude) {
        this.genres = genres.stream()
        .map(genre -> GENRE_MAP.getOrDefault(genre, null))
        .filter(Objects::nonNull)
        .map(genre -> (exclude ? "!" : "+") + genre)
        .distinct()
        .collect(Collectors.toSet());
    }

    public void mapCountries(Set<MovieRoomRequest.Countries> countries, boolean exclude) {
        this.countries = countries.stream()
        .map(country -> COUNTIRES_MAP.getOrDefault(country, null))
        .filter(Objects::nonNull)
        .map(country -> (exclude ? "!" : "+") + country)
        .distinct()
        .collect(Collectors.toSet());
    }

    public void mapServices(Set<MovieRoomRequest.Service> services) {
        this.services = services.stream()
        .map(service -> SERVICES_MAP.getOrDefault(service, null))
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toSet());
    }

    public void setMinRating(int minRating) {
        if (minRating > 10 || minRating < 0) {
            this.minRating = null;
        }
        this.minRating = minRating;
    }

    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public void setWithPosterOnly(boolean withPoster) {
        this.onlyWithPoster = withPoster;
    }

    public void setWithDescriptionOnly(boolean withDescription) {
        this.onlyWithDescription = withDescription;
    }

    public String getQuery() {
        List<String> parameters = new LinkedList<>();
        if (type != null) {
            parameters.add("type=" + type);
        }
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> parameters.add("genres.name=" + genre));
        }
        if (countries != null && !countries.isEmpty()) {
            countries.forEach(country -> parameters.add("countries.name=" + country));
        }
        if (services != null && !services.isEmpty()) {
            services.forEach(service -> parameters.add("watchability.items.name=" + service));
        }
        String years = Stream.of(yearFrom, yearTo)
            .filter(Objects::nonNull)
            .map(i -> Integer.toString(i))
            .collect(Collectors.joining("-"));
        if (years != null && !years.isBlank()) {
            parameters.add("year=" + years);
        }
        if (minRating != null) {
            parameters.add("rating.kp=" + minRating + "-10");
        }
        if (parameters.isEmpty()) {
            return "";
        }
        return parameters.stream().collect(Collectors.joining("&"));
    }

    public MultiValueMap<String, String> getMapQuery() {
        MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();
        if (type != null) {
            result.add("type", type);
        }
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> result.add("genres.name", genre));
        }
        if (countries != null && !countries.isEmpty()) {
            countries.forEach(country -> result.add("countries.name", country));
        }
        if (services != null && !services.isEmpty()) {
            services.forEach(service -> result.add("watchability.items.name", service));
        }
        String years = Stream.of(yearFrom, yearTo)
            .filter(Objects::nonNull)
            .map(i -> Integer.toString(i))
            .collect(Collectors.joining("-"));
        if (years != null && !years.isBlank()) {
            result.add("year", years);
        }
        if (minRating != null) {
            result.add("rating.kp",  minRating + "-10");
        }
        if (onlyWithPoster) {
            result.add("notNullFields", "poster.url");
        }
        if (onlyWithDescription) {
            result.add("notNullFields", "description");
        }
        return result;
    }

    public String getPaginatedQuery(int page, int limit) {
        String query = getQuery();
        return query + (query.isBlank() ? "" : "&") + "page=" + page + "&limit=" + limit;
    }

    public MultiValueMap<String, String> getPaginatedMapQuery(int page, int limit) {
        MultiValueMap<String, String> result = getMapQuery();
        result.add("page", Integer.toString(page));
        result.add("limit", Integer.toString(limit));
        return result;
    }
}
