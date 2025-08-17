package com.example.dogtraininglog.util;

public final class DogInputValidator {
    private DogInputValidator() {}

    /** Returns null if valid */
    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) return "Name required";
        String n = name.trim();
        if (n.length() > 30) return "Name must be ≤ 30 chars";
        if (!n.matches("[A-Za-z0-9 '\\-]+")) return "Invalid characters in name";
        return null;
    }
}
