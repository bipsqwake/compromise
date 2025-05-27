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

import com.bipsqwake.compromise_ws.exception.InvalidInputException;
import com.bipsqwake.compromise_ws.message.DecisionMessage;
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

    @MessageMapping("/room/{roomId}/hello")
    public void hello(@DestinationVariable("roomId") String roomId,
            @Payload HelloMessage message,
            SimpMessageHeaderAccessor header) throws RoomException {
        String playerId = roomService.addPlayerToRoom(roomId, message.name(), header.getSessionId());
        if (header.getSessionAttributes() != null) {
            header.getSessionAttributes().put(PLAYER_ID_SESSION_ATTR, playerId);
        } else {
            log.warn("Session attributes for session {} is null", header.getSessionId());
            return;
        }
    }

    @MessageMapping("/room/{roomId}/start")
    public void start(@DestinationVariable("roomId") String roomId, SimpMessageHeaderAccessor header) throws RoomException {
        roomService.startRoom(roomId);
    }

    @MessageMapping("/room/{roomId}/decision")
    public void decision(@DestinationVariable("roomId") String roomId, 
                        @Payload DecisionMessage message,
                        SimpMessageHeaderAccessor header) throws RoomException, InvalidInputException {
        if (header.getSessionAttributes() == null) {
            log.warn("No session attributes in session {}", header.getSessionId());
            return;
        }
        if (message.cardId() == null || message.decision() == null) {
            throw new InvalidInputException("Invalid decision message");
        }
        String playerId = (String) header.getSessionAttributes().get(PLAYER_ID_SESSION_ATTR);
        roomService.processDecision(roomId, playerId, message.cardId(), message.decision());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        exception.printStackTrace();
        return exception.getMessage();
    }
}
