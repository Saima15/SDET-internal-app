package com.scalesec.vulnado.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class Unauthorized extends RuntimeException {
    public Unauthorized(String exception) {
        super(exception);
    }
}
