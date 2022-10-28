package com.chs.exceptions.naturalis;

public class InvalidEmailException extends Exception{

    public InvalidEmailException(String message) {
        super(message);
    }
}