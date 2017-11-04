package com.tesis.yudith.showmethepast.domain.collections.childs;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class GPSPosition implements Serializable {
    private double latitude;
    private double longitude;
    private String regionKey;

    public GPSPosition() {

    }

    public boolean isZero() {
        return this.latitude == 0 && this.longitude == 0;
    }

    public GPSPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getRegionKey() {
        return regionKey;
    }

    public void setRegionKey(String regionKey) {
        this.regionKey = regionKey;
    }
}
