package com.requests.itoup.exception;

public class RequestNotFoundException extends RuntimeException {

    public RequestNotFoundException(Long id) {
        super("Заявка с id " + id + " не найдена");
    }
}