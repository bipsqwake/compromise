package com.bipsqwake.compromise_ws.room.exception;

import lombok.Getter;

@Getter
public class RoomException extends Exception {
    private final String reason;
    private final String userError;

    public RoomException(String reason) {
        super(reason);
        this.reason = reason;
        this.userError = reason;
    }

    public RoomException(String reason, String userError) {
        super(reason);
        this.reason = reason;
        this.userError = userError;
    }
}
