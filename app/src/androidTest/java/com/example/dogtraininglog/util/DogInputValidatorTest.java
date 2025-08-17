package com.example.dogtraininglog.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class DogInputValidatorTest {

    @Test public void valid_simple() {
        assertNull(DogInputValidator.validateName("Buddy"));
    }

    @Test public void empty_returnsMessage() {
        assertEquals("Name required", DogInputValidator.validateName("   "));
    }

    @Test public void tooLong_returnsMessage() {
        assertEquals("Name must be ≤ 30 chars",
                DogInputValidator.validateName("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
    }

}
