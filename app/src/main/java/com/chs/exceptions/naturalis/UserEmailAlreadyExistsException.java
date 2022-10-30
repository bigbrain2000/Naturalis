package com.chs.exceptions.naturalis;

public class UserEmailAlreadyExistsException extends Exception {
    public UserEmailAlreadyExistsException(String message) {
        super(message);
    }
}
