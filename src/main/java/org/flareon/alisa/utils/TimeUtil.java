package org.flareon.alisa.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private final long days;
    private final long hours;
    private final long minutes;
    private final long seconds;

    public TimeUtil(final long time) {
        this.days = time / 86400000L;
        this.hours = time / 3600000L % 24L;
        this.minutes = time / 60000L % 60L;
        this.seconds = time / 1000L % 60L;
    }

    @Deprecated
    public static int getDayOfWeek() {
        final Date date = new Date(System.currentTimeMillis());
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public String formatLogBuilder(final String day, final String hour, final String minute, final String second) {
        final StringBuilder sb = new StringBuilder("");
        if (days > 0) {
            sb.append(String.format(" %s%s", days, day));
        }
        if (hours > 0) {
            sb.append(String.format("%s%s", hours, hour));
        }
        if (minutes > 0) {
            sb.append(String.format("%s%s", minutes, minute));
        }
        if (seconds > 0) {
            sb.append(String.format("%s%s", seconds, second));
        }
        return sb.toString();
    }

    public String getLog() {
        return formatLogBuilder(" дней ", " час(а) ", " минут ", " секунд");
    }

    public String getShortLog() {
        return formatLogBuilder("д. ", "ч. ", "м. ", "с. ");
    }

    public long getDays() {
        return days;
    }

    public long getHours() {
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getSeconds() {
        return seconds;
    }
}
