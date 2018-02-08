package com.timojo.luceneserverlite.exception;

public class UnknownAnalyzerException extends Exception {
    public UnknownAnalyzerException(String message) {
        super(message);
    }

    public UnknownAnalyzerException(String message, Exception cause) {
        super(message, cause);
    }
}
