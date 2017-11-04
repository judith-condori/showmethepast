package com.tesis.yudith.showmethepast.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocoderTools {

    private Geocoder geocoder;
    public GeocoderTools(Context context) {
        this.geocoder = new Geocoder(context, Locale.ENGLISH);
    }

    public String getRegionKey(LatLng position) throws IOException {
        return this.getRegionKey(position.latitude, position.longitude);
    }

    public String getRegionKey(Location location) throws IOException {
        return this.getRegionKey(location.getLatitude(), location.getLongitude());
    }

    public String getRegionKey(double latitude, double longitude) throws IOException {
        StringBuilder strAddress = new StringBuilder();

        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if(addresses != null) {

            Address fetchedAddress = addresses.get(0);

            strAddress
                    .append(fetchedAddress.getCountryName())
                    .append(".")
                    .append(fetchedAddress.getLocality());

        }

        return strAddress.toString();
    }
}
