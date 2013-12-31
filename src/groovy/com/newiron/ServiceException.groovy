package com.newiron

class ServiceException extends RuntimeException {

    String message
    Map<String, List<String>> errors

    ServiceException(String message, Map<String, List<String>> errors) {
        this.message = message
        this.errors = errors
    }

}