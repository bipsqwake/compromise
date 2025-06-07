package com.bipsqwake.compromise_ws.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bipsqwake.compromise_ws.exception.WebException;
import com.bipsqwake.compromise_ws.message.ErrorResponse;

@ControllerAdvice
@RestController
public class ExceptionHandlerController {
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebException.class)
    public ErrorResponse handleWebException(WebException exception) {
        return exception.getErrorMessage();
    }
}
