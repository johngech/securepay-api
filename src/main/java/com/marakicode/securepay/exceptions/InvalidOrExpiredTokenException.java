package com.marakicode.securepay.exceptions;

public class InvalidOrExpiredTokenException extends RuntimeException {
    public InvalidOrExpiredTokenException(){
        super("Invalid or expired token!!");
    }
}
