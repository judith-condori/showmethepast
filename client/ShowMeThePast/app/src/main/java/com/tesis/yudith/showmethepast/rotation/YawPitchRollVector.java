package com.tesis.yudith.showmethepast.rotation;

public class YawPitchRollVector {
    // Yaw or Azimuth
    public double yaw;
    public double pitch;
    public double roll;

    public YawPitchRollVector() {
        this.yaw = this.pitch = this.roll = 0;
    }

    public YawPitchRollVector(double yaw, double pitch, double roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public YawPitchRollVector(UVWVector fromVector) {
        this.yaw = fromVector.v;

        this.pitch = -fromVector.u;
        this.roll = -fromVector.w;
    }
}
