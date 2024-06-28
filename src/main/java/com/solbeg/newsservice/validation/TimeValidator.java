package com.solbeg.newsservice.validation;

import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.exception.TimePeriodException;

import java.time.LocalDateTime;

public class TimeValidator {

    public static void validateTimePeriod(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new TimePeriodException(ErrorMessage.ERROR_TIME_MESSAGE.getMessage());
        }
    }
}