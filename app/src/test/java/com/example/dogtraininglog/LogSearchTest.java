package com.example.dogtraininglog;

import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class LogSearchTest {

    private List<String> filterLogs(List<String> logs, String query) {
        if (query == null || query.isEmpty()) return logs;
        List<String> result = new ArrayList<>();
        for (String log : logs) {
            if (log.toLowerCase().contains(query.toLowerCase())) {
                result.add(log);
            }
        }
        return result;
    }

    @Test
    public void searchFindsMatchingLogs() {
        List<String> logs = Arrays.asList("Sit 5 reps", "Stay 3 reps", "Down 2 reps");
        List<String> result = filterLogs(logs, "sit");

        assertEquals(1, result.size());
        assertEquals("Sit 5 reps", result.get(0));
    }

    @Test
    public void searchEmptyReturnsAllLogs() {
        List<String> logs = Arrays.asList("Sit", "Stay", "Down");
        List<String> result = filterLogs(logs, "");

        assertEquals(3, result.size());
    }
}