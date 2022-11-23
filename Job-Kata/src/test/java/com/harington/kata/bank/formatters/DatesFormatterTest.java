package com.harington.kata.bank.formatters;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DatesFormatterTest {

    @Test
    public void should_return_empty_when_null_given(){
        assertEquals("", DatesFormatter.format(null));
        assertEquals("", DatesFormatter.formatOnlyDate(null));
    }

    @Test
    public void should_return_correct_format(){
        LocalDateTime date = LocalDateTime.of(2022, 11, 12, 4, 20, 20);
        assertEquals("12/11/2022 04:20:20", DatesFormatter.format(date));
        assertEquals("12/11/2022", DatesFormatter.formatOnlyDate(date));

        LocalDateTime date2 = LocalDateTime.of(2022, 11, 1, 4, 20, 20);
        assertEquals("01/11/2022 04:20:20", DatesFormatter.format(date2));
        assertEquals("01/11/2022", DatesFormatter.formatOnlyDate(date2));

        LocalDateTime date3 = LocalDateTime.of(2022, 11, 1, 12, 5, 20);
        assertEquals("01/11/2022 12:05:20", DatesFormatter.format(date3));
    }
}