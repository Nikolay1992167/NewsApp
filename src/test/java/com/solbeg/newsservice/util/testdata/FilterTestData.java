package com.solbeg.newsservice.util.testdata;

import com.solbeg.newsservice.dto.request.Filter;

public class FilterTestData {

    public static Filter getFilter() {
        return Filter.builder()
                .part("hello")
                .build();
    }
}
