package com.tesis.yudith.showmethepast.view.ar;

import com.tesis.yudith.showmethepast.rotation.UVWVector;

public class ArPin {

    private UVWVector position;
    private ArCalibrationResult calibration;

    public ArPin() {
        this.setPosition(new UVWVector());
        this.setCalibration(new ArCalibrationResult());
    }

    public ArPin(UVWVector position, ArCalibrationResult calibration) {
        this.setPosition(position);
        this.setCalibration(calibration);
    }

    public UVWVector getPosition() {
        return position;
    }

    public void setPosition(UVWVector position) {
        this.position = position;
    }

    public ArCalibrationResult getCalibration() {
        return calibration;
    }

    public void setCalibration(ArCalibrationResult calibration) {
        this.calibration = calibration;
    }
}
