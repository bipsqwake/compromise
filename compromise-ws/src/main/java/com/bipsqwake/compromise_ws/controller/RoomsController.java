package com.bipsqwake.compromise_ws.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bipsqwake.compromise_ws.message.RoomCreateRequest;
import com.bipsqwake.compromise_ws.message.RoomResponse;
import com.bipsqwake.compromise_ws.room.Card;
import com.bipsqwake.compromise_ws.room.Room;
import com.bipsqwake.compromise_ws.service.RoomService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rooms")
@Slf4j
public class RoomsController {
    
    @Autowired
    RoomService roomService;

    @PostMapping("/create")
    public RoomResponse createRoom(@RequestBody RoomCreateRequest request) {
        List<Card> tmpList = new ArrayList<>();
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc90", "0", "Zero", "https://img.pixers.pics/pho_wat(s3:700/FO/57/91/04/67/700_FO57910467_c3eb65d3d570cac084541479f0dea2c7.jpg,700,700,cms:2018/10/5bd1b6b8d04b8_220x50-watermark.png,over,480,650,jpg)/stickers-fire-alphabet-number-0-zero.jpg.jpg"));
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc91", "1", "One", "https://image.spreadshirtmedia.net/image-server/v1/products/T1459A839PA4459PT28D195809042W8333H10000/views/1,width=550,height=550,appearanceId=839,backgroundColor=F2F2F2/number-1-number-sticker.jpg"));
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc92", "2", "Two", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd8OENc93qPZc5n1TnO_OLh5-XxTpoto-sgw&s"));
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc93", "3", "Fo.. Just kidding. Three", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQs18Alp46dvHqWIpIvj7tjqhovdcdeoTN-1w&s"));
        tmpList.add(new Card("6a9dcaa2-4c68-40cc-a4f5-d85d8c51bc94", "4", "Five", "https://img.uline.com/is/image/uline/S-25441-4?$Mobile_Zoom$"));

        log.info(request.name());

        Room room = roomService.createRoom(request.name(), tmpList);
        return new RoomResponse(room.getId(), room.getName());
    }

    @GetMapping
    public Set<String> getRoomsIds() {
        return roomService.getRoomsIds();
    }

    @GetMapping("{id}")
    public RoomResponse getRoomName(@PathVariable("id") String id) {
        Room room = roomService.getRoom(id);
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No room with id %s", id));
        }
        return new RoomResponse(room.getId(), room.getName());
    }

}
