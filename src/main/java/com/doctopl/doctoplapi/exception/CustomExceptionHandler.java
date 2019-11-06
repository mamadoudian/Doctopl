package com.doctopl.doctoplapi.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import com.doctopl.doctoplapi.response.ErrorMessage;

import java.util.Date;


@ControllerAdvice
public class CustomExceptionHandler {
  @ExceptionHandler(Exception.class)
  public final ResponseEntity<ErrorMessage> handleAllExceptions(Exception ex, WebRequest request) {
    
    ErrorMessage errorObj = new ErrorMessage(new Date(), ex.getMessage(),
        request.getDescription(false));
    return new ResponseEntity<>(errorObj, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
