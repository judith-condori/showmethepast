package com.tesis.yudith.showmethepast.domain.serializables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializableMapScenario implements Serializable {
    private List<SerializableMapMarker> staticMarkers;
    private List<SerializableMapMarker> draggableMarkers;

    private double centerLatitude;
    private double centerLongitude;

    public SerializableMapScenario() {
        this.staticMarkers = new ArrayList<>();
        this.draggableMarkers = new ArrayList<>();
    }

    public double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public double getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public List<SerializableMapMarker> getStaticMarkers() {
        return staticMarkers;
    }

    public void setStaticMarkers(List<SerializableMapMarker> staticMarkers) {
        this.staticMarkers = staticMarkers;
    }

    public List<SerializableMapMarker> getDraggableMarkers() {
        return draggableMarkers;
    }

    public void setDraggableMarkers(List<SerializableMapMarker> draggableMarkers) {
        this.draggableMarkers = draggableMarkers;
    }
}
