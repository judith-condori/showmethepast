package com.tesis.yudith.showmethepast.rotation;

public interface IRotationManager {
    UVWVector getRotation();
    YawPitchRollVector getYPRRotation();
    void setInformation(int sensorType, int surfaceRotation, float[] sensorValues);
}
