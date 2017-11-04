package com.tesis.yudith.showmethepast.tools;

public class StringTools {
    public static String formatNumber(double number, int decimalPositions) {
        return String.format("%." + decimalPositions + "f", number);
    }
}
