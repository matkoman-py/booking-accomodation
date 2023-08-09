package com.bookingaccomodation.exception;

import com.bookingaccomodation.model.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(BadRequestException exception) {
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                "Bad request exception!",
                exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
