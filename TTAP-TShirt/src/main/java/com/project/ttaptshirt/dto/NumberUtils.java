package com.project.ttaptshirt.dto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumberUtils {
    public static String formatCurrency(double value) {
        // Tạo DecimalFormatSymbols tùy chỉnh để định nghĩa ký tự phân cách
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(','); // Dấu ',' làm ký tự phân cách nhóm
        symbols.setDecimalSeparator('.'); // Dấu '.' làm ký tự phân cách thập phân (nếu cần)

        // Cấu hình DecimalFormat với mẫu định dạng và các ký hiệu
        DecimalFormat df = new DecimalFormat("#,###", symbols); // Hiển thị 2 chữ số thập phân
        return df.format(value) + " đ"; // Thêm ký tự tiền tệ
    }

    public static String formatCurrency2(double value) {
        // Create custom DecimalFormatSymbols for consistent grouping symbol
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(','); // Use ',' as the grouping separator
        symbols.setDecimalSeparator('.'); // Use '.' as the decimal separator (if needed)

        // Apply the custom symbols to the DecimalFormat instance
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(value);
    }

    public static String formatCurrency3(double value) {
        // Create custom DecimalFormatSymbols for consistent grouping symbol
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Use ',' as the grouping separator
        symbols.setDecimalSeparator('.'); // Use '.' as the decimal separator (if needed)

        // Apply the custom symbols to the DecimalFormat instance
        DecimalFormat df = new DecimalFormat("###.#", symbols);
        return df.format(value);
    }

    public static void main(String[] args) {
        System.out.println(formatCurrency2(1000000));
    }
}
