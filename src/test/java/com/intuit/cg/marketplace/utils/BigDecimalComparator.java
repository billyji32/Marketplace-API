package com.intuit.cg.marketplace.utils;

import org.hamcrest.Matcher;
import org.hamcrest.number.BigDecimalCloseTo;

import java.math.BigDecimal;

public class BigDecimalComparator {
    private static final BigDecimal PRECISION = new BigDecimal(0.00001);

    public static Matcher<BigDecimal> closeTo(double value) {
        return BigDecimalCloseTo.closeTo(new BigDecimal(value), PRECISION);
    }
}
