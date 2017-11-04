package com.tesis.yudith.showmethepast;


import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.google.android.gms.maps.MapsInitializer;
import com.tesis.yudith.showmethepast.aservices.AlertsByPositionService;
import com.tesis.yudith.showmethepast.configuration.AppConfiguration;
import com.tesis.yudith.showmethepast.configuration.LoginProcessor;
import com.tesis.yudith.showmethepast.configuration.NitriteManager;
import com.tesis.yudith.showmethepast.configuration.ObjectsCache;
import com.tesis.yudith.showmethepast.controller.AppControllers;
import com.tesis.yudith.showmethepast.dao.AppDaos;
import com.tesis.yudith.showmethepast.helpers.GeocoderTools;
import com.tesis.yudith.showmethepast.requests.tools.ApplicationRequests;
import com.tesis.yudith.showmethepast.tools.AndroidServiceTools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyApp extends Application {

    private static MyApp currentApp;
    private LoginProcessor currentLoginProcessor;

    private ApplicationRequests applicationRequests;
    private AppConfiguration configuration;
    private NitriteManager nitriteManager;
    private AppDaos appDaos;
    private AppControllers appControllers;
    private ObjectsCache objectsCache;
    private GeocoderTools geocoderTools;
    private String lastModification;

    public MyApp() {
        super();
        MyApp.currentApp = this;
        this.objectsCache = new ObjectsCache();
        this.updateLastModification();
    }

    public boolean isReleaseMode() {
        boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
        return !isDebuggable;
    }

    public String getLastModification() {
        return this.lastModification;
    }

    public void updateLastModification() {
        this.lastModification = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public ObjectsCache getObjectsCache() {
        return this.objectsCache;
    }

    public AppConfiguration getConfiguration() {
        if (this.configuration == null) {
            String appConfigurationKey = this.getResources().getString(R.string.configuration_application_key);
            this.configuration = new AppConfiguration(this.getSharedPreferences(appConfigurationKey, Context.MODE_PRIVATE), this.getResources());
        }
        return this.configuration;
    }

    public ApplicationRequests getAppRequests() {
        if (this.applicationRequests == null) {
            String serverUrl = this.getConfiguration().readServerAddress();
            this.applicationRequests = new ApplicationRequests(this.getApplicationContext(), serverUrl);
        }
        return this.applicationRequests;
    }

    public AppDaos getAppDaos() {
        return this.appDaos;
    }

    public AppControllers getAppControllers() {
        return this.appControllers;
    }

    public static MyApp getCurrent() {
        return MyApp.currentApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MapsInitializer.initialize(this.getApplicationContext());

        String dbPath = this.getApplicationContext().getFilesDir().getAbsolutePath();
        this.nitriteManager = new NitriteManager(dbPath + "/smtp.db");
        this.appDaos = new AppDaos(this.nitriteManager);
        this.appControllers = new AppControllers(this.appDaos);
        this.geocoderTools = new GeocoderTools(this.getApplicationContext());

        if (AndroidServiceTools.isMyServiceRunning(AlertsByPositionService.class)) {
            AlertsByPositionService.stopService();
        }

        if (this.getConfiguration().readEnableAlertsByPosition()) {
            AlertsByPositionService.startService();
        }
    }

    public LoginProcessor getCurrentLoginProcessor() {
        return currentLoginProcessor;
    }

    public void setCurrentLoginProcessor(LoginProcessor currentLoginProcessor) {
        this.currentLoginProcessor = currentLoginProcessor;
    }

    public GeocoderTools getGeocoderTools() {
        return geocoderTools;
    }
}
