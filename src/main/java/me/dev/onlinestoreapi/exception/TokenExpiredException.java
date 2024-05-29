package me.dev.onlinestoreapi.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String msg) {
        super(msg);
    }
}
