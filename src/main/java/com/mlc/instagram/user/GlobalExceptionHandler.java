package com.mlc.instagram.user;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleRestException(Exception e) throws IOException {
        log.warn("Not a mapped exception. Please contact development team", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>("Server failed to process the request " + e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @ExceptionHandler({ IllegalArgumentException.class, HttpRequestMethodNotSupportedException.class, HttpMediaTypeNotSupportedException.class, HttpMessageNotReadableException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object badRequest(HttpServletRequest request, Exception e) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object badRequest(HttpServletRequest request, MethodArgumentNotValidException e) throws IOException {
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder sb = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            sb.append(error.getField() + " " + error.getDefaultMessage() + "\n");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler({ ResourceAccessException.class })
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Object badRequest(HttpServletRequest request, ResourceAccessException e) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.BAD_GATEWAY);
    }

    @ResponseBody
    @ExceptionHandler({ RestClientResponseException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object badRequest(HttpServletRequest request, RestClientResponseException e) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
    }
}
