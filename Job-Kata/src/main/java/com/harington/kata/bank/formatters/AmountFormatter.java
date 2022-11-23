package com.harington.kata.bank.formatters;

public final class AmountFormatter {
    private final static String FORMAT = "%s%d.%02d€";

    /**
     * @param amountInCents
     * @return
     */
    public static String formatCents(int amountInCents) {
        String sign = amountInCents < 0 ? "-" : "";
        amountInCents = amountInCents < 0 ? (amountInCents * -1) : amountInCents;
        if (amountInCents < 100) return String.format(FORMAT, sign, 0, amountInCents);
        return String.format(FORMAT, sign, amountInCents / 100, amountInCents % 100);
    }
}
