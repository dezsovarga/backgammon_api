package com.dezso.varga.backgammon.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by dezso on 18.11.2017.
 */
public class AuthExeption extends BgException{

    int statusCode;

    public AuthExeption(String message, int statusCode) {
        super(message, statusCode);
        this.statusCode = statusCode;
    }
}
