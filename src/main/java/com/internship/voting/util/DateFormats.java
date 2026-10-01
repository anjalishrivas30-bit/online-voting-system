package com.internship.voting.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** JSTL's fmt:formatDate does not understand java.time, so entities expose pre-formatted strings. */
public final class DateFormats {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private DateFormats() { }

    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? "" : DISPLAY.format(dateTime);
    }
}
