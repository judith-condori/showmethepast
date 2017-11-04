package com.tesis.yudith.showmethepast.aservices;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.tesis.yudith.showmethepast.MainMenuActivity;
import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.configuration.LanguageManager;
import com.tesis.yudith.showmethepast.dao.CommonsDao;
import com.tesis.yudith.showmethepast.domain.CommonConstants;
import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.domain.position.PositionForEntity;
import com.tesis.yudith.showmethepast.helpers.GeocoderTools;
import com.tesis.yudith.showmethepast.tools.AndroidServiceTools;

import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertsByPositionService extends Service {

    public static void startService() {
        Intent service = new Intent(MyApp.getCurrent().getBaseContext(), AlertsByPositionService.class);
        MyApp.getCurrent().startService(service);
    }

    public static void stopService() {
        int maxIterations = 5;
        int counter = 0;

        while (counter++ < maxIterations && AndroidServiceTools.isMyServiceRunning(AlertsByPositionService.class)) {
            Intent service = new Intent(MyApp.getCurrent().getBaseContext(), AlertsByPositionService.class);
            MyApp.getCurrent().stopService(service);
        }
    }

    public static final String NOTIFICATION_DOCUMENT_ID = "NOTIFICATION_DOCUMENT_ID";
    public static final String NOTIFICATION_DOCUMENT_TYPE = "NOTIFICATION_DOCUMENT_TYPE";
    public static final String NOTIFICATION_DOCUMENT_MODE = "NOTIFICATION_DOCUMENT_MODE";

    public static final String MODE_OPEN_IN_MAP = "MODE_OPEN_IN_MAP";
    public static final String MODE_OPEN_INFORMATION = "MODE_OPEN_INFORMATION";

    public static final long UPDATE_REGION_MIN_MILISECONDS = 20 * 60 * 1000;

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 2f;

    private GeocoderTools geocoderTools;
    private List<PositionForEntity> positionsForCurrentRegion;
    private String currentRegion;
    private String lastModificationDate;
    private Date lastRegionUpdate;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Date currentDate = new Date();
            long offset = currentDate.getTime() - lastRegionUpdate.getTime();

            if (offset >= UPDATE_REGION_MIN_MILISECONDS) {
                this.verifyRegionChanges(location);
            }

            PositionForEntity nearest = this.findNearestEntity(location);

            if (nearest == null) {
                Log.e(TAG, "onLocationChanged: " + location + " " + currentRegion);
            } else {
                Intent intentOpenInMap = new Intent(MyApp.getCurrent(), MainMenuActivity.class);
                intentOpenInMap.putExtra(NOTIFICATION_DOCUMENT_ID, nearest.getId());
                intentOpenInMap.putExtra(NOTIFICATION_DOCUMENT_TYPE, MongoCollection.getServerCollectionName(nearest.getEntityClass()));
                intentOpenInMap.putExtra(NOTIFICATION_DOCUMENT_MODE, MODE_OPEN_IN_MAP);

                PendingIntent pendingIntentOpenInMap = PendingIntent.getActivity(MyApp.getCurrent(), 0, intentOpenInMap,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Intent intentOpenInformation = new Intent(MyApp.getCurrent(), MainMenuActivity.class);
                intentOpenInformation.putExtra(NOTIFICATION_DOCUMENT_ID, nearest.getId());
                intentOpenInformation.putExtra(NOTIFICATION_DOCUMENT_TYPE, MongoCollection.getServerCollectionName(nearest.getEntityClass()));
                intentOpenInformation.putExtra(NOTIFICATION_DOCUMENT_MODE, MODE_OPEN_INFORMATION);

                PendingIntent pendingIntentOpenInformation = PendingIntent.getActivity(MyApp.getCurrent(), 1, intentOpenInformation,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MyApp.getCurrent().getApplicationContext())
                                .setSmallIcon(R.drawable.common_full_open_on_phone)
                                .setContentTitle(MyApp.getCurrent().getResources().getString(R.string.title_notification_you_are_close_to))
                                .setContentText(LanguageManager.translate(nearest.getTitle()))
                                .setVibrate(new long[] { 250, 250 })
                                .addAction(R.mipmap.ic_launcher_smtp, getResources().getString(R.string.label_notification_open_in_map), pendingIntentOpenInMap)
                                .addAction(R.drawable.ic_menu_camera, getResources().getString(R.string.label_notification_open_information), pendingIntentOpenInformation)
                                //.setSound(Uri.parse("file:///android_asset/notification_ddm.mp3"))
                                //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_ddm))
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                                .setAutoCancel(true);

                NotificationManager nm = (NotificationManager) MyApp.getCurrent().getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(1, mBuilder.build());

                Log.e(TAG, "onLocationChanged: " + location + " " + currentRegion + " " + nearest.getId());
            }
            mLastLocation.set(location);
        }

        private PositionForEntity findNearestEntity(Location location) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            float minimunDistance = Float.MAX_VALUE;
            PositionForEntity nearestEntity = null;

            int alertMinimumDistance = MyApp.getCurrent().getConfiguration().readAlertsMinimunRadius();

            for(PositionForEntity entity : positionsForCurrentRegion) {
                LatLng current = entity.getPosition();
                float[] results = new float[1];

                Location.distanceBetween(position.latitude, position.longitude, current.latitude, current.longitude, results);

                if (results[0] < minimunDistance && results[0] <= alertMinimumDistance) {
                    minimunDistance = results[0];
                    nearestEntity = entity;
                }
            }

            return nearestEntity;
        }

        private void verifyRegionChanges(Location location) {
            String regionKey;
            try {
                regionKey = geocoderTools.getRegionKey(location);
            } catch (Exception err) {
                regionKey = CommonConstants.DEFAULT_REGION_KEY;
            }

            if (!regionKey.equals(currentRegion) || !MyApp.getCurrent().getLastModification().equals(lastModificationDate)) {
                lastModificationDate = MyApp.getCurrent().getLastModification();
                currentRegion = regionKey;
                this.reloadRegion();
            }
        }

        private void reloadRegion() {
            List<TouristicPlace> touristicPlaces = this.getTouristicPlacesByRegionKey(currentRegion);
            List<OldPicture> oldPictures = this.getOldPicturesByRegionKey(currentRegion);

            positionsForCurrentRegion = new ArrayList<>();

            for(TouristicPlace item : touristicPlaces) {
                PositionForEntity newEntity = new PositionForEntity();
                newEntity.setId(item.getId());
                newEntity.setEntityClass(TouristicPlace.class);
                newEntity.setPosition(item.getPosition().toLatLng());
                newEntity.setTitle(item.getName());

                positionsForCurrentRegion.add(newEntity);
            }

            for(OldPicture item : oldPictures) {
                PositionForEntity newEntity = new PositionForEntity();
                newEntity.setId(item.getId());
                newEntity.setEntityClass(OldPicture.class);
                newEntity.setPosition(item.getPosition().getStartPosition().toLatLng());
                newEntity.setTitle(item.getName());

                positionsForCurrentRegion.add(newEntity);
            }
        }

        List<TouristicPlace> getTouristicPlacesByRegionKey(String regionKey) {
            CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();
            return commonsDao.find(ObjectFilters.eq("position.regionKey", regionKey), TouristicPlace.class).toList();
        }

        List<OldPicture> getOldPicturesByRegionKey(String regionKey) {
            CommonsDao commonsDao = MyApp.getCurrent().getAppDaos().getCommonsDao();
            return commonsDao.find(ObjectFilters.eq("position.startPosition.regionKey", regionKey), OldPicture.class).toList();
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
        new LocationListener(LocationManager.GPS_PROVIDER),
        new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        if (this.geocoderTools == null) {
            this.geocoderTools = new GeocoderTools(this);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        this.lastRegionUpdate = new Date(0);
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
