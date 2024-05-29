package me.dev.onlinestoreapi.exception;

public class PermissionDenyException extends RuntimeException {
    public PermissionDenyException(String msg) {
        super(msg);
    }
}
