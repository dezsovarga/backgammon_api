package com.dezso.varga.backgammon.exeptions;

/**
 * Created by dezso on 28.11.2017.
 */
public class UserAlreadyExistsException extends BgException{

    int statusCode;

    public UserAlreadyExistsException(String message, int statusCode) {
        super(message, statusCode);
        this.statusCode = statusCode;
    }
}
