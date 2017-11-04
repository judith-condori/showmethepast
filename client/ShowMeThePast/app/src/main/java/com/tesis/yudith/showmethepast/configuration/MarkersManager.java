package com.tesis.yudith.showmethepast.configuration;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.helpers.BitmapTools;

import java.util.HashMap;
import java.util.Map;

public class MarkersManager {

    private static MarkersManager currentMarkersManager;

    public static synchronized MarkersManager getCurrent() {
        if (currentMarkersManager == null) {
            currentMarkersManager = new MarkersManager();
        }
        return currentMarkersManager;
    }

    public enum EMarker {
        MARKER_TOURISTIC_PLACE,
        MARKER_OLD_PICTURE,
        MARKER_PICK_START,
        MARKER_PICK_TARGET
    }

    private Map<EMarker, BitmapDescriptor> markersMap;

    public BitmapDescriptor get(EMarker marker) {
        return this.markersMap.get(marker);
    }

    private MarkersManager() {
        this.markersMap = new HashMap<>();
        this.markersMap.put(EMarker.MARKER_TOURISTIC_PLACE, this.createTouristicPlaceMarker());
        this.markersMap.put(EMarker.MARKER_PICK_START, this.createPickStartPlaceMarker());
        this.markersMap.put(EMarker.MARKER_PICK_TARGET, this.createPickTargetPlaceMarker());
    }

    private BitmapDescriptor createPickTargetPlaceMarker() {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
    }

    private BitmapDescriptor createPickStartPlaceMarker() {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
    }

    private BitmapDescriptor createTouristicPlaceMarker() {
        BitmapDescriptor bitmapDescriptor;
        Bitmap museumBitmap = BitmapTools.fromAssets(MyApp.getCurrent().getApplicationContext(), "museum_marker.png");
        museumBitmap = BitmapTools.scale(museumBitmap , 0.25f);
        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(museumBitmap);

        return bitmapDescriptor;
    }
}
