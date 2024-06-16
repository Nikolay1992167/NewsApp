package com.solbeg.newsservice.util.testdata;

import com.solbeg.newsservice.dto.request.TimePeriod;

import java.time.LocalDateTime;

public class TimePeriodTestData {

    public static TimePeriod getTimePeriod() {
        return TimePeriod.builder()
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now())
                .build();
    }

    public static TimePeriod getTimePeriodIncorrect() {
        return TimePeriod.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().minusDays(5))
                .build();
    }
}