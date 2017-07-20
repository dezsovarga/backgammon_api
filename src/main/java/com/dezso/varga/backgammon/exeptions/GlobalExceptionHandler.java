package com.dezso.varga.backgammon.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * Created by dezso on 25.06.2017.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(value=HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public Map<String, String> handleMissingFieldsException(BgException ex) {
        return ex.getErrorBody();
    }

}

