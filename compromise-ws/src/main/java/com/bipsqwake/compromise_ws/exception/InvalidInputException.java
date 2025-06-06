package com.bipsqwake.compromise_ws.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="No such Order")
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
