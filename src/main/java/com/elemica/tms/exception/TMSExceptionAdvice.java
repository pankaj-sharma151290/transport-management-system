package com.elemica.tms.exception;

import com.elemica.tms.model.resourceobject.TMSExceptionResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class TMSExceptionAdvice extends ResponseEntityExceptionHandler {

    /**
     * This advice will redirect all the Exception occurred in the application to this method
     *
     * @param exception
     * @return TMSExceptionResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<TMSExceptionResponse> resolveAndWriteException(Exception exception) {

        TMSExceptionResponse response = new TMSExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                                                                 HttpStatus.BAD_REQUEST.name(), exception.getMessage());
        logger.error("Error caught in TMSExceptionAdvice", exception);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * This advice will redirect all the TMSException occurred in the application to this method
     *
     * @param tmsException
     * @return TMSExceptionResponse
     */
    @ExceptionHandler(TMSException.class)
    public ResponseEntity<TMSExceptionResponse> resolveAndWriteException(TMSException tmsException) {

        TMSExceptionResponse response = new TMSExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                                                                 HttpStatus.BAD_REQUEST.name(), tmsException.getMessage());
        logger.error("Error caught in TMSExceptionAdvice", tmsException);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}