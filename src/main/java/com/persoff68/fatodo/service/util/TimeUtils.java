package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.service.exception.OldRequestException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TimeUtils {

    private TimeUtils() {
    }

    public static void checkIfOldRequest(Date date) {
        Instant oldestInstant = Instant.now().minus(24, ChronoUnit.HOURS);
        Date oldestDate = Date.from(oldestInstant);
        if (date.before(oldestDate)) {
            throw new OldRequestException();
        }
    }

}
