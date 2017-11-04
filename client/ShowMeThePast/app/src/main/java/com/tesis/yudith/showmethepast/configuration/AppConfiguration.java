package com.tesis.yudith.showmethepast.configuration;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.tesis.yudith.showmethepast.R;
import java.util.Date;

public class AppConfiguration {

    private SharedPreferences sharedPreferences;
    private Resources resources;

    public AppConfiguration(SharedPreferences sharedPreferences, Resources resources) {
        this.sharedPreferences = sharedPreferences;
        this.resources = resources;
    }

    private SharedPreferences.Editor getEditor() {
        return this.sharedPreferences.edit();
    }

    public String readServerAddress() {
        return this.sharedPreferences.getString(
                resources.getString(R.string.configuration_server_address),
                resources.getString(R.string.configuration_server_address_default));
    }

    public void writeServerAddress(String value) {
        SharedPreferences.Editor editor = this.getEditor();
        editor.putString(resources.getString(R.string.configuration_server_address), value);
        editor.commit();
    }

    public boolean readUseGyroscope() {
        return this.sharedPreferences.getBoolean(
                resources.getString(R.string.configuration_use_gyroscope),
                resources.getBoolean(R.bool.configuration_use_gyroscope_default));
    }

    public void writeUseGyroscope(boolean value) {
        SharedPreferences.Editor editor = this.getEditor();
        editor.putBoolean(resources.getString(R.string.configuration_use_gyroscope), value);
        editor.commit();
    }
/*
    public void writeLastSyncDate(Date date) {
        SharedPreferences.Editor editor = this.getEditor();
        editor.putLong(resources.getString(R.string.configuration_last_synchronization), date.getTime());
    }

    public Date readLastSyncDate() {
        long dateLong = this.sharedPreferences.getLong(resources.getString(R.string.configuration_last_synchronization), 0);
        return new Date(dateLong);
    }
*/
    public boolean readEnableAlertsByPosition() {
        return this.sharedPreferences.getBoolean(
                resources.getString(R.string.configuration_alerts_by_position),
                resources.getBoolean(R.bool.configuration_alerts_by_position_default));
    }

    public void writeEnableAlertsByPosition(boolean value) {
        SharedPreferences.Editor editor = this.getEditor();
        editor.putBoolean(resources.getString(R.string.configuration_alerts_by_position), value);
        editor.commit();
    }

    public int readAlertsMinimunRadius() {
        return this.sharedPreferences.getInt(
                resources.getString(R.string.configuration_alerts_minimum_radius),
                resources.getInteger(R.integer.configuration_alerts_minimum_radius_default));
    }

    public void writeAlertsMinimumRadius(int value) {
        SharedPreferences.Editor editor = this.getEditor();
        editor.putInt(resources.getString(R.string.configuration_alerts_minimum_radius), value);
        editor.commit();
    }
}
