package com.scalesec.vulnado.dto;

import java.io.Serializable;


public class LoginResponse implements Serializable {
    public String token;

    public LoginResponse(String msg) {
        this.token = msg;
    }
}