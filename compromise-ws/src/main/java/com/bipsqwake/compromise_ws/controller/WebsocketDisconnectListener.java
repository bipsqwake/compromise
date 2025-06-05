package com.bipsqwake.compromise_ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.bipsqwake.compromise_ws.room.exception.RoomException;
import com.bipsqwake.compromise_ws.service.RoomService;

import io.micrometer.common.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebsocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    RoomService roomService;

    @Override
    public void onApplicationEvent(@NonNull SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("User with session {} disconnected", sessionId);
        try {
            roomService.removePlayerFromRoomsBySession(sessionId);
        } catch (RoomException e) {
            log.error("Failed to remove player with session {} from rooms: {}", e.getMessage());
        }
    }
}
