package com.requests.itoup.exception;

public class BusinessAccessDeniedException extends RuntimeException {
    public BusinessAccessDeniedException(String error) {
        super(error);
    }
}