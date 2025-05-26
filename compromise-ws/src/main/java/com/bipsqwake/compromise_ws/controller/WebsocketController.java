package com.bipsqwake.compromise_ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.bipsqwake.compromise_ws.exception.WsException;
import com.bipsqwake.compromise_ws.message.HelloMessage;
import com.bipsqwake.compromise_ws.message.HelloResponse;
import com.bipsqwake.compromise_ws.room.Room;
import com.bipsqwake.compromise_ws.room.exception.RoomException;
import com.bipsqwake.compromise_ws.service.RoomService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WebsocketController {

    private static final String PLAYER_ID_SESSION_ATTR = "playerId";
    
    @Autowired
    RoomService roomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room/{roomId}/hello")
    @SendToUser("/queue/reply")
    public HelloResponse hello(@DestinationVariable(value = "roomId") String roomId,
                            @Payload HelloMessage message,
                            SimpMessageHeaderAccessor header) throws RoomException, WsException {
        Room room = roomService.getRoom(roomId);
        if (room == null) {
            log.info("Hello request to unexisting room {}", roomId);
            throw new WsException(String.format("Can't find room with id %s", roomId));
        }
        String name = message.name();
        String playerId;
        playerId = room.addPlayer(name);
        //TODO nullpointer check
        header.getSessionAttributes().put(PLAYER_ID_SESSION_ATTR, playerId);
        return new HelloResponse(playerId);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
