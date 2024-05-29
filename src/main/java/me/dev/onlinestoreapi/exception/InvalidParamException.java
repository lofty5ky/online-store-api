package me.dev.onlinestoreapi.exception;

public class InvalidParamException extends RuntimeException {
    public InvalidParamException(String msg) {
        super(msg);
    }
}
