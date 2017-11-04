package com.tesis.yudith.showmethepast.rotation;

public class UVWVector {
    public double u, v ,w;

    public UVWVector(YawPitchRollVector fromVector) {
        this.v = fromVector.yaw;

        this.u = -fromVector.pitch;
        this.w = -fromVector.roll;
    }

    public UVWVector(double u, double v, double w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public UVWVector() {
        this.u = this.v = this.w;
    }
}
