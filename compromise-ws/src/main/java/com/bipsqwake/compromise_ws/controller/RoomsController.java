package com.bipsqwake.compromise_ws.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bipsqwake.compromise_ws.exception.InvalidInputException;
import com.bipsqwake.compromise_ws.exception.WebException;
import com.bipsqwake.compromise_ws.message.RoomCreateRequest;
import com.bipsqwake.compromise_ws.message.RoomResponse;
import com.bipsqwake.compromise_ws.message.roomcreate.BgRoomCreateRequest;
import com.bipsqwake.compromise_ws.message.roomcreate.MovieRoomRequest;
import com.bipsqwake.compromise_ws.room.Card;
import com.bipsqwake.compromise_ws.room.Room;
import com.bipsqwake.compromise_ws.room.RoomStatus;
import com.bipsqwake.compromise_ws.service.RoomService;
import com.bipsqwake.compromise_ws.service.bggservice.BggService;
import com.bipsqwake.compromise_ws.service.kinopoiskservice.KinopoiskQuery;
import com.bipsqwake.compromise_ws.service.kinopoiskservice.KinopoiskService;
import com.bipsqwake.compromise_ws.service.teseraservice.TeseraService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rooms")
@Slf4j
public class RoomsController {
    
    @Autowired
    private RoomService roomService;

    @Autowired
    private BggService bggService;

    @Autowired
    private TeseraService teseraService;

    @Autowired
    private KinopoiskService kinopoiskService;

    @Value("classpath:/data/food_options.json")
    private Resource restsInput;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping("/create/food")
    public RoomResponse createRoom(@RequestBody RoomCreateRequest request) throws IOException, InvalidInputException {
        List<Card> list = getCardsFromResource(restsInput);
        Room room = roomService.createRoom(request.name(), list);
        return new RoomResponse(room.getId(), room.getName(), room.getState());
    }

    @PostMapping("/create/boardgames")
    public RoomResponse createBggRoom(@RequestBody BgRoomCreateRequest request) throws InvalidInputException, WebException {
        if (!request.isValid()) {
            throw WebException.invalidIput("Invalid bg room create request");
        }
        List<Card> cards;
        switch (request.source()) {
            case BGG:
                cards = bggService.getCardsFromCollection(request.username(), request.playersNum());
                break;
            default:
                cards = teseraService.getCardsFromCollection(request.username(), request.playersNum());
                break;
        }
        if (cards.size() <= 2) {
            throw WebException.collectionTooSmall("Collection is too small to start voting");
        }
        Room room = roomService.createRoom(request.name(), cards);
        return new RoomResponse(room.getId(), room.getName(), room.getState());
    }

    @PostMapping("/create/movies")
    public RoomResponse createMovieRoom(@RequestBody MovieRoomRequest movieRoomRequest) throws WebException {
        log.info(movieRoomRequest.toString());
        KinopoiskQuery query = new KinopoiskQuery();
        if (movieRoomRequest.type() != null) {
            query.mapType(movieRoomRequest.type());
        }
        if (movieRoomRequest.genres() != null) {
            query.mapGenres(movieRoomRequest.genres(), movieRoomRequest.genresExclude());
        }
        if (movieRoomRequest.countries() != null) {
            query.mapCountries(movieRoomRequest.countries(), movieRoomRequest.countriesExclude());
        }
        if (movieRoomRequest.services() != null) {
            query.mapServices(movieRoomRequest.services());
        } 
        if (movieRoomRequest.minRating() != null) {
            query.setMinRating(movieRoomRequest.minRating());
        }
        if (movieRoomRequest.yearFrom() != null) {
            query.setYearFrom(movieRoomRequest.yearFrom());
        }
        if (movieRoomRequest.yearTo() != null) {
            query.setYearTo(movieRoomRequest.yearTo());
        }
        List<Card> cards = kinopoiskService.getMovies(query);
        log.info("Extracted {} cards", cards.size());
        if (cards.size() <= 2) {
            throw WebException.collectionTooSmall("Collection is too small to start voting");
        }
        Room room = roomService.createRoom(movieRoomRequest.name(), cards);
        return new RoomResponse(room.getId(), room.getName(), room.getState());
    }

    @GetMapping
    public List<RoomStatus> getRooms() {
        return roomService.getRoomsStatus();
    }

    @GetMapping("{id}")
    public RoomResponse getRoomName(@PathVariable("id") String id) {
        Room room = roomService.getRoom(id);
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No room with id %s", id));
        }
        return new RoomResponse(room.getId(), room.getName(), room.getState());
    }

    private List<Card> getCardsFromResource(Resource resource) throws IOException {
        return objectMapper.readValue(resource.getContentAsByteArray(), new TypeReference<List<Card>>() {});
    }

}
