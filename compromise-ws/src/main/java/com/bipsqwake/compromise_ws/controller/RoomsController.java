package com.bipsqwake.compromise_ws.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bipsqwake.compromise_ws.message.RoomCreateRequest;
import com.bipsqwake.compromise_ws.response.RoomResponse;
import com.bipsqwake.compromise_ws.room.Card;
import com.bipsqwake.compromise_ws.room.Room;
import com.bipsqwake.compromise_ws.service.RoomService;

@RestController
@RequestMapping("/rooms")
public class RoomsController {
    
    @Autowired
    RoomService roomService;

    @PostMapping("/create")
    public RoomResponse createRoom(RoomCreateRequest request) {
        List<Card> tmpList = new ArrayList<>();
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc90", "0", "Zero"));
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc91", "1", "One"));
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc92", "2", "Two"));
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc93", "3", "Fo.. Just kidding. Three"));
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc94", "4", "Five"));

        return new RoomResponse(roomService.createRoom(request.name(), tmpList));
    }

    @GetMapping
    public Set<String> getRoomsIds() {
        return roomService.getRoomsIds();
    }

    @GetMapping("{id}")
    public Set<String> getRoomPlayers(@PathVariable("id") String id) {
        Room room = roomService.getRoom(id);
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No room with id %s", id));
        }
        return room.getPlayerIds();
    }

}
