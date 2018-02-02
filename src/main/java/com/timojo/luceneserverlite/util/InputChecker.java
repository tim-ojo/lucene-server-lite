package com.timojo.luceneserverlite.util;

public class InputChecker {
    public static void isNotNullOrEmpty(String input, String message) {
        if (input == null || input.length() == 0)
            throw new IllegalArgumentException(message);
    }
}
