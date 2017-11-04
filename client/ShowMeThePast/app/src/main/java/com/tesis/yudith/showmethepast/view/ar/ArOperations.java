package com.tesis.yudith.showmethepast.view.ar;

public class ArOperations {

    public static double calculateArDistance(double x, double angle) {
        return x / ( 2 * Math.sin(angle / 2));
    }

    public static double calculateArX(double distance, double angle) {
        return 2 * distance * Math.sin(angle / 2);
    }

    public static double normalizeAngle(double angle) {
        while (angle < 0) {
             angle+=2 * Math.PI;
        }

        while (angle > 2 * Math.PI) {
            angle-=2 * Math.PI;
        }

        return angle;
    }

    public static double angleDistance(double startAngle, double newAngle) {
        startAngle = normalizeAngle(startAngle);
        newAngle = normalizeAngle(newAngle);

        double result = newAngle - startAngle;
        if (Math.abs(result) > Math.PI) {
            if (result < 0) {
                result = result + 2 * Math.PI;
            } else {
                result = result - 2 * Math.PI;
            }
        }
        return result;
    }
}
