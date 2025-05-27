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

import com.bipsqwake.compromise_ws.message.HelloMessage;
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
    public void hello(@DestinationVariable("roomId") String roomId,
            @Payload HelloMessage message,
            SimpMessageHeaderAccessor header) throws RoomException {
        String playerId = roomService.addPlayerToRoom(roomId, message.name(), header.getSessionId());
        if (header.getSessionAttributes() != null) {
            header.getSessionAttributes().put(PLAYER_ID_SESSION_ATTR, playerId);
        } else {
            log.warn("Session attributes for session {} is null", header.getSessionId());
        }
    }

    @MessageMapping("/room/{roomId}/start")
    public void start(@DestinationVariable("roomId") String roomId, SimpMessageHeaderAccessor header) throws RoomException {
        roomService.startRoom(roomId);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        exception.printStackTrace();
        return exception.getMessage();
    }
}
