package com.dynatrace.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
public class TimeOutException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TimeOutException(String message) {
        super(message);
    }
}
