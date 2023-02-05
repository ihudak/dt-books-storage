package com.dynatrace.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.LOCKED)
public class InsufficientResourcesException extends ResponseStatusException {
    private static final long serialVersionUID = 1L;

    public InsufficientResourcesException(String message) {
        super(HttpStatus.LOCKED, message);
    }
}
