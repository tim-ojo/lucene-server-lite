package com.timojo.luceneserverlite.exception;

public class UnknownMergePolicyException extends Exception {
    public UnknownMergePolicyException(String message) {
        super(message);
    }

    public UnknownMergePolicyException(String message, Exception cause) {
        super(message, cause);
    }
}
