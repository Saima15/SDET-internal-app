package com.scalesec.vulnado.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FOUND)
public class Found extends RuntimeException {
    public Found(String exception) {
        super(exception);
    }
}