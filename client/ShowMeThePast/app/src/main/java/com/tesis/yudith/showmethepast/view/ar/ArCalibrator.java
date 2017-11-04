package com.tesis.yudith.showmethepast.view.ar;

import android.graphics.PointF;

import com.tesis.yudith.showmethepast.rotation.UVVector;
import com.tesis.yudith.showmethepast.rotation.UVWVector;

import java.util.ArrayList;
import java.util.List;

public class ArCalibrator {

    private List<ArCalibratorInformation> syncDots;

    public ArCalibrator() {
        this.syncDots = new ArrayList<ArCalibratorInformation>();
    }

    public void reset(){
        this.syncDots.clear();
    }

    public void addPoint(UVWVector vector, int x, int y) {
        this.syncDots.add(new ArCalibratorInformation(vector, x, y));
    }

    public ArPin calibrate() {
        double avgCalibratedDistanceU = 0;
        double avgCalibratedDistanceV = 0;

        int n = this.syncDots.size();

        if (n <= 1) {
            return null;
        }

        for(int i = 1; i < n; i++) {
            ArCalibratorInformation current = this.syncDots.get(i);
            ArCalibratorInformation initial = this.syncDots.get(0);

            double deltaDistanceU = Math.abs(current.getScreenPoint().x - initial.getScreenPoint().x);
            double deltaDistanceV = Math.abs(current.getScreenPoint().y - initial.getScreenPoint().y);

            double deltaAngleU = Math.abs(ArOperations.angleDistance(initial.getRotation().u, current.getRotation().u));
            double deltaAngleV = Math.abs(ArOperations.angleDistance(initial.getRotation().v, current.getRotation().v));

            double calibratedDistanceU = ArOperations.calculateArDistance(deltaDistanceU, deltaAngleV);
            double calibratedDistanceV = ArOperations.calculateArDistance(deltaDistanceV, deltaAngleU);

            avgCalibratedDistanceU += calibratedDistanceU;
            avgCalibratedDistanceV += calibratedDistanceV;
        }

        avgCalibratedDistanceU/=n;
        avgCalibratedDistanceV/=n;

        ArCalibratorInformation firstPoint = this.syncDots.get(0);

        ArCalibrationResult calibration = new ArCalibrationResult(avgCalibratedDistanceU, avgCalibratedDistanceV);
        UVWVector position = firstPoint.getRotation();

        return new ArPin(position, calibration);
    }
}
