package com.campusfind.util;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ItemCodeGenerator {

    private final AtomicInteger counter = new AtomicInteger(200);

    public String generate() {
        int year = Year.now().getValue();
        int seq = counter.incrementAndGet();
        return String.format("LF-%d-%06d", year, seq);
    }
}