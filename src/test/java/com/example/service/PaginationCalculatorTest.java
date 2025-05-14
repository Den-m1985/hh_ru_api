package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationCalculatorTest {
    PaginationCalculator paginationCalculator;

    @BeforeEach
    void setUp() {
        paginationCalculator = new PaginationCalculator();
    }

    @Test
    void testExactPages() {
        Map<Integer, Integer> result = paginationCalculator.calculatePages(200, 100);
        assertEquals(Map.of(0, 100, 1, 100), result);
    }

    @Test
    void testWithRemainder() {
        Map<Integer, Integer> result = paginationCalculator.calculatePages(201, 100);
        assertEquals(Map.of(0, 100, 1, 100, 2, 1), result);
    }

    @Test
    void testSinglePage() {
        Map<Integer, Integer> result = paginationCalculator.calculatePages(50, 100);
        assertEquals(Map.of(0, 50), result);
    }

    @Test
    void testZeroItems() {
        Map<Integer, Integer> result = paginationCalculator.calculatePages(0, 100);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> paginationCalculator.calculatePages(-1, 100));
        assertThrows(IllegalArgumentException.class, () -> paginationCalculator.calculatePages(10, 0));
    }
}
