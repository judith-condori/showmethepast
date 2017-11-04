package com.tesis.yudith.showmethepast.rotation;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.Surface;

public class RotationManagerNative implements IRotationManager {

    private YawPitchRollVector rotationYPR;

    public RotationManagerNative() {
        this.rotationYPR = new YawPitchRollVector();
    }

    @Override
    public UVWVector getRotation() {
        return new UVWVector(this.getYPRRotation());
    }

    @Override
    public YawPitchRollVector getYPRRotation() {
        return this.rotationYPR;
    }

    @Override
    public void setInformation(int sensorType, int surfaceRotation, float[] sensorValues) {
        if (sensorType == Sensor.TYPE_ROTATION_VECTOR) {
            this.updateInformation(surfaceRotation, sensorValues);
        }
    }

    private void updateInformation(int surfaceRotation, float[] sensorValues) {
        float[] rotationMatrix = new float[9];
        float[] rotationVector = sensorValues;

        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);

        final int worldAxisForDeviceAxisX;
        final int worldAxisForDeviceAxisY;

        // Remap the axes as if the device screen was the instrument panel,
        // and adjust the rotation matrix for the device orientation.
        switch (surfaceRotation) {
            case Surface.ROTATION_0:
            default:
                worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
                break;
            case Surface.ROTATION_90:
                worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
                break;
            case Surface.ROTATION_180:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
                break;
            case Surface.ROTATION_270:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_X;
                break;
        }

        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
                worldAxisForDeviceAxisY, adjustedRotationMatrix);

        // Transform rotation matrix into azimuth/pitch/roll
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        // Convert radians to degrees
        float azimuth = orientation[0];
        float pitch = orientation[1];
        float roll = orientation[2];

        this.rotationYPR = new YawPitchRollVector(azimuth, pitch, roll);
    }
}
