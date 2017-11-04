package com.tesis.yudith.showmethepast.domain.serializables;

import com.google.android.gms.maps.model.LatLng;
import com.tesis.yudith.showmethepast.configuration.MarkersManager;
import com.tesis.yudith.showmethepast.domain.collections.childs.GPSPosition;

import java.io.Serializable;

public class SerializableMapMarker implements Serializable {
    private double latitude;
    private double longitude;
    private String title;
    private MarkersManager.EMarker marker;

    public SerializableMapMarker() {

    }

    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }

    public GPSPosition getGPSPosition() {
        return new GPSPosition(this.latitude, this.longitude);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MarkersManager.EMarker getMarker() {
        return marker;
    }

    public void setMarker(MarkersManager.EMarker marker) {
        this.marker = marker;
    }
}
