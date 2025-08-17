package com.example.dogtraininglog.util;

import java.time.LocalDate;
import java.util.Comparator;

public final class LogSort {
    private LogSort() {}

    public static final class SimpleLog {
        public final String activity;
        public final LocalDate date;
        public SimpleLog(String activity, LocalDate date) {
            this.activity = activity;
            this.date = date;
        }
    }

    /*Sort with newest on top*/
    public static final Comparator<SimpleLog> BY_DATE_DESC = (a, b) -> {
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;
        if (a.date == null && b.date == null) return 0;
        if (a.date == null) return 1;
        if (b.date == null) return -1;
        return b.date.compareTo(a.date);
    };
}
