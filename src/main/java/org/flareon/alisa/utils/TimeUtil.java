package org.flareon.alisa.utils;

import org.flareon.alisa.FLAlisa;

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

    public String getLog() {
        final StringBuilder sb = new StringBuilder("");
        if(days > 0) {
            sb.append(String.format(" %s дней", days));
        }
        if(hours > 0) {
            sb.append(String.format(" %s час(а)", hours));
        }
        if(minutes > 0) {
            sb.append(String.format(" %s минут", minutes));
        }
        if(seconds > 0) {
            sb.append(String.format(" %s секунд", seconds));
        }
        return sb.toString();
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

    @Deprecated
    public static int getDayOfWeek() {
        final Date date = new Date(System.currentTimeMillis());
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }
}
