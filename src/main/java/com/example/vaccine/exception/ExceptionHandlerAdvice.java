package com.example.vaccine.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;

@ControllerAdvice("com.example.vaccine")
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ServletRequestBindingException.class
            , HttpMessageConversionException.class, MethodArgumentTypeMismatchException.class
            , ConstraintViolationException.class, HttpMessageNotReadableException.class , ValidationException.class})
    public ResponseEntity<String> invalidRequestExceptionHandler(HttpServletRequest request, Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(HttpServletRequest request, Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
