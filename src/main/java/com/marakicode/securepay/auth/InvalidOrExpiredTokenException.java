package com.marakicode.securepay.auth;

public class InvalidOrExpiredTokenException extends RuntimeException {
    public InvalidOrExpiredTokenException(){
        super("Invalid or expired token!!");
    }
}
