package com.tesis.yudith.showmethepast.tools;

import android.app.ActivityManager;
import android.content.Context;

import com.tesis.yudith.showmethepast.MyApp;

public class AndroidServiceTools {
    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MyApp.getCurrent().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
