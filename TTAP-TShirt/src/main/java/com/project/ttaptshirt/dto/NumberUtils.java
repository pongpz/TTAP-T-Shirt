package com.project.ttaptshirt.dto;

import java.text.DecimalFormat;

public class NumberUtils {
    public static String formatCurrency(double value) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(value) + " đ";
    }
}