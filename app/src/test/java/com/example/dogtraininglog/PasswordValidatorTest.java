package com.example.dogtraininglog;

import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordValidatorTest {

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    @Test
    public void passwordTooShort_isInvalid() {
        assertFalse(isValidPassword("abc"));   // only 3 chars
    }

    @Test
    public void passwordLongEnough_isValid() {
        assertTrue(isValidPassword("secret1")); // 7 chars
    }
}