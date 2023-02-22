package com.seitov.news.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
