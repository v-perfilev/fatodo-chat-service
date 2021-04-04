package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.service.exception.OldRequestException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    private TimeUtils() {
    }

    public static Date getDateWithShift(Date date, int msShift) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MILLISECOND, msShift);
        return cal.getTime();
    }

}
