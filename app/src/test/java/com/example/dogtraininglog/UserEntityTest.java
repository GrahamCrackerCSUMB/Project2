package com.example.dogtraininglog;

import com.example.dogtraininglog.database.entities.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserEntityTest {

    @Test
    public void defaultIsAdminIsFalse_andToggleWorks() {
        // Your User has a (String, String) ctor
        User user = new User("trainer1", "pass123");

        // default should be false
        assertFalse(user.isAdmin());

        // promote
        user.setAdmin(true);
        assertTrue(user.isAdmin());

        // demote
        user.setAdmin(false);
        assertFalse(user.isAdmin());
    }

    @Test
    public void usernameAndPasswordSettersWork() {
        User user = new User("alice", "pw");
        assertEquals("alice", user.getUsername());
        assertEquals("pw", user.getPassword());

        user.setUsername("bob");
        user.setPassword("secret");

        assertEquals("bob", user.getUsername());
        assertEquals("secret", user.getPassword());
    }
}