package com.harington.kata.bank.formatters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmountFormatterTest {

    @Test
    public void should_return_correct_format(){
        assertEquals("-0.01€", AmountFormatter.formatCents(-1));
        assertEquals("0.00€", AmountFormatter.formatCents(0));
        assertEquals("0.01€", AmountFormatter.formatCents(1));
        assertEquals("0.10€", AmountFormatter.formatCents(10));
        assertEquals("1.00€", AmountFormatter.formatCents(100));
        assertEquals("10.00€", AmountFormatter.formatCents(10_00));
    }
}