package com.tesis.yudith.showmethepast.rotation;

import android.hardware.Sensor;

public class RotationManager implements IRotationManager {

    public UVWVector getRotation() {

        // Angle UV
        double wAngle = Math.atan2(aInformation.v, aInformation.u);
        // Angle VW
        double uAngle = -Math.atan2(aInformation.w, aInformation.u);
        // Angle UW
        double vAngle = Math.atan2(mInformation.u, mInformation.w);

        return new UVWVector(uAngle, vAngle, wAngle);
    }

    @Override
    public YawPitchRollVector getYPRRotation() {
        return new YawPitchRollVector(this.getRotation());
    }

    @Override
    public void setInformation(int sensorType, int surfaceRotation, float[] sensorValues) {
        switch (sensorType) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.setMagnetometerInformation(sensorValues[0], sensorValues[1], sensorValues[2]);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                this.setAccelerometerInformation(sensorValues[0], sensorValues[1], sensorValues[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                this.setGyroscopeInformation(sensorValues[0], sensorValues[1], sensorValues[2]);
                break;
        }
    }

    public UVWVector getAccelerometerInformation() {
        return aInformation;
    }

    public UVWVector getMagnetometerInformation() {
        return mInformation;
    }

    public UVWVector getGyroscopeInformation() {
        return gInformation;
    }

    public ERotation getCurrentRotation() {
        return currentRotation;
    }

    public enum ERotation {
        NONE,
        X_POSITIVE,
        X_NEGATIVE,
        Y_POSITIVE,
        Y_NEGATIVE
    }
    
    /*
        Accelerometer Information
     */
    private UVWVector aInformation;

    /*
        Magnetometer Information
     */
    private UVWVector mInformation;

    /*
        Gyroscope Information
     */
    private UVWVector gInformation;
    
    private ERotation currentRotation;

    /*
        Rotation manager, constructor
     */
    public RotationManager() {
        this.aInformation = new UVWVector();
        this.mInformation = new UVWVector();
        this.gInformation = new UVWVector();
        this.currentRotation = ERotation.NONE;
    }

    public void setAccelerometerInformation(float x, float y, float z) {
        float absX = Math.abs(x);
        float absY = Math.abs(y);

        if (absX > absY) {
            // Landscape
            if (x < 0) {
                // cambiar bien uv
                this.aInformation.u = -x;
                this.aInformation.v = y;
                this.aInformation.w = z;
                this.currentRotation = ERotation.X_NEGATIVE;
            } else {
                // cambiar bien uv
                this.aInformation.u = x;
                this.aInformation.v = -y;
                this.aInformation.w = z;
                this.currentRotation = ERotation.X_POSITIVE;
            }
        } else {
            if (y < 0) {
                // cambiar bien uv
                this.aInformation.u = -y;
                this.aInformation.v = -x;
                this.aInformation.w = z;
                this.currentRotation = ERotation.Y_NEGATIVE;
            } else {
                // este uv esta correcto
                this.aInformation.u = y;
                this.aInformation.v = x;
                this.aInformation.w = z;
                this.currentRotation = ERotation.Y_POSITIVE;
            }
        }
    }

    public void setMagnetometerInformation(float x, float y, float z) {
        if (this.currentRotation == ERotation.NONE) {
            return;
        }

        switch (this.getCurrentRotation()) {
            case X_NEGATIVE:
                this.mInformation.u = y;
                this.mInformation.w = -z;
                break;
            case X_POSITIVE:
                this.mInformation.u = -y;
                this.mInformation.w = -z;
                break;
            case Y_NEGATIVE:
                this.mInformation.u = -x;
                this.mInformation.w = -z;
                break;
            case Y_POSITIVE:
                this.mInformation.u = x;
                this.mInformation.w = -z;
                break;
        }
    }

    public void setGyroscopeInformation(float x, float y, float z) {

    }
}
