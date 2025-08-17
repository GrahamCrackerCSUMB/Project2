package com.example.dogtraininglog.util;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.*;

public class LogSortTest {


    @Test public void sortsNewestFirst() {
        LogSort.SimpleLog a = new LogSort.SimpleLog("Heel", LocalDate.of(2024, 1, 2));
        LogSort.SimpleLog b = new LogSort.SimpleLog("Stay", LocalDate.of(2025, 8, 16));
        LogSort.SimpleLog c = new LogSort.SimpleLog("Sit",  LocalDate.of(2025, 8, 17));

        /*Sort so newest are on top*/
        List<LogSort.SimpleLog> list = new ArrayList<>(Arrays.asList(a, b, c));
        list.sort(LogSort.BY_DATE_DESC);

        /*Check*/
        assertEquals("Sit",  list.get(0).activity); // 2025-08-17
        assertEquals("Stay", list.get(1).activity); // 2025-08-16
        assertEquals("Heel", list.get(2).activity); // 2024-01-02
    }


    @Test public void nullDates_goLast() {
        LogSort.SimpleLog withDate = new LogSort.SimpleLog("With", LocalDate.of(2025, 1, 1));
        LogSort.SimpleLog noDate   = new LogSort.SimpleLog("No", null);

        /*Put null dates on bottom*/
        List<LogSort.SimpleLog> list = new ArrayList<>(Arrays.asList(noDate, withDate));
        list.sort(LogSort.BY_DATE_DESC);

        /*Check they are on bottom*/
        assertEquals("With", list.get(0).activity);
        assertEquals("No",   list.get(1).activity);
    }
}
