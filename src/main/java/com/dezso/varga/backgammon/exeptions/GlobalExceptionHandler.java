package com.dezso.varga.backgammon.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by dezso on 25.06.2017.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingFieldsException.class)
    @ResponseStatus(value=HttpStatus.PRECONDITION_FAILED, reason = "Missing or invalid mandatory fields")
    @ResponseBody
    public void handleMissingFieldsException(final MissingFieldsException ex) {
        ex.printStackTrace();
    }

}

