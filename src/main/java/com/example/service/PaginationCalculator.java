package com.example.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PaginationCalculator {

    public Map<Integer, Integer> calculatePages(int totalItems, int maxPerPage) {
        if (totalItems < 0 || maxPerPage <= 0) {
            throw new IllegalArgumentException("totalItems must be >= 0 and maxPerPage must be > 0");
        }
        Map<Integer, Integer> pages = new LinkedHashMap<>();
        int fullPages = totalItems / maxPerPage;
        int remaining = totalItems % maxPerPage;
        for (int i = 0; i < fullPages; i++) {
            pages.put(i, maxPerPage);
        }
        if (remaining > 0) {
            pages.put(fullPages, remaining);
        }
        return pages;
    }
}
