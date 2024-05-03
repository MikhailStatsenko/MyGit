package com.vcs.vitalitygit.git.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    private static final ThreadLocal<SimpleDateFormat> dateFormatter =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH));
    public static String format(Date date) {
        return dateFormatter.get().format(date);
    }
}
