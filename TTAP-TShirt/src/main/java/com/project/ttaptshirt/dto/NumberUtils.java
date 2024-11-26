package com.project.ttaptshirt.dto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumberUtils {
    public static String formatCurrency(double value) {
        // Create custom DecimalFormatSymbols for consistent grouping symbol
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(','); // Use ',' as the grouping separator
        symbols.setDecimalSeparator('.'); // Use '.' as the decimal separator (if needed)

        // Apply the custom symbols to the DecimalFormat instance
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(value) + " đ";
    }
}