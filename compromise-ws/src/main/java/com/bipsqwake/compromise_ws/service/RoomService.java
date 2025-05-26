package com.bipsqwake.compromise_ws.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.stereotype.Service;

import com.bipsqwake.compromise_ws.room.Card;
import com.bipsqwake.compromise_ws.room.Room;

@Service
public class RoomService {

    private Map<String, Room> rooms = new ConcurrentSkipListMap<>();

    public String createRoom(List<Card> cards) {
        Room room = new Room(cards);
        rooms.put(room.getId(), room);
        return room.getId();
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Set<String> getRoomsIds() {
        return rooms.keySet();
    }
}
