package com.example.dogtraininglog;

import com.example.dogtraininglog.database.entities.User;
import org.junit.Test;
import static org.junit.Assert.*;

public class AdminToggleTest {

    @Test
    public void testPromoteToAdmin() {
        User user = new User("john", "password123");

        // user starts as non-admin
        assertFalse(user.isAdmin());

        // promote
        user.setAdmin(true);
        assertTrue("User should be admin after promotion", user.isAdmin());
    }

    @Test
    public void testDemoteFromAdmin() {
        User user = new User("jane", "secret123");
        user.setAdmin(true); // start as admin

        assertTrue(user.isAdmin());

        // demote
        user.setAdmin(false);
        assertFalse("User should not be admin after demotion", user.isAdmin());
    }
}