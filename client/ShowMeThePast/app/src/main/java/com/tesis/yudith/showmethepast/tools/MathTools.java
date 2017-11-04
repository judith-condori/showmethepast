package com.tesis.yudith.showmethepast.tools;

public class MathTools {
    public static double radToDeg(double rad) {
        return rad * 180 / Math.PI;
    }

    public static double atanDegrees(double yComponent, double xComponent) {
        return Math.atan2(yComponent, xComponent) * 180 / Math.PI;
    }
}
