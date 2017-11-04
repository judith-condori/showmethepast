package com.tesis.yudith.showmethepast.view;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.tesis.yudith.showmethepast.MyApp;
import com.tesis.yudith.showmethepast.R;

public class SensorsCalibrationDialogFragment extends DialogFragment implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;

    TextView txtAccuracy;

    int lowerAccuracy = -1;
    int accelerometerAccuracy;
    int magnetometerAccuracy;
    int gyroscopeAccuracy;

    public SensorsCalibrationDialogFragment() {

    }

    public static SensorsCalibrationDialogFragment newInstance() {
        SensorsCalibrationDialogFragment fragment = new SensorsCalibrationDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onResume() {
        super.onResume();

        this.mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        this.mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        this.mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void startSensors() {
        this.mSensorManager = (SensorManager)this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensors_calibration_dialog, container, false);

        this.linkControls(view);
        this.startSensors();

        return view;
    }

    void linkControls(View view) {
        WebView webView = (WebView) view.findViewById(R.id.webViewCalibrationInstructions);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadUrl("file:///android_asset/calibrate.html");

        this.txtAccuracy = (TextView) view.findViewById(R.id.txt_sensorsCalibration_accuracy);
    }

    private String getStringResource(int resourceCode) {
        return MyApp.getCurrent().getResources().getString(resourceCode);
    }

    private String formatAccuracy(int accuracy) {

        // 0 SENSOR_STATUS_UNRELIABLE
        // 1 SENSOR_STATUS_ACCURACY_LOW
        // 2 SENSOR_STATUS_ACCURACY_MEDIUM
        // 3 SENSOR_STATUS_ACCURACY_HIGH

        String[] meanings = new String[]{
                this.getStringResource(R.string.label_unreilable),
                this.getStringResource(R.string.label_low),
                this.getStringResource(R.string.label_medium),
                this.getStringResource(R.string.label_high)
        };

        if (accuracy < 0 || accuracy >= meanings.length) {
            return this.getStringResource(R.string.label_unknown);
        }

        return meanings[accuracy];
    }

    private int accuracyToColor(int accuracy) {
        // 0 SENSOR_STATUS_UNRELIABLE
        // 1 SENSOR_STATUS_ACCURACY_LOW
        // 2 SENSOR_STATUS_ACCURACY_MEDIUM
        // 3 SENSOR_STATUS_ACCURACY_HIGH

        int[] colors = new int[]{ Color.BLACK, Color.RED, Color.rgb(186, 130, 0), Color.rgb(0, 86, 24)};

        if (accuracy < 0 || accuracy >= colors.length) {
            return Color.GRAY;
        }

        return colors[accuracy];
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        int currentSensorType = sensor.getType();

        switch (currentSensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                this.accelerometerAccuracy = accuracy;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.magnetometerAccuracy = accuracy;
                break;
            case Sensor.TYPE_GYROSCOPE:
                this.gyroscopeAccuracy = accuracy;
                break;
        }

        if (this.accelerometerAccuracy <= this.magnetometerAccuracy && this.accelerometerAccuracy <= this.gyroscopeAccuracy) {
            this.lowerAccuracy = this.accelerometerAccuracy;
        }

        if (this.magnetometerAccuracy <= this.accelerometerAccuracy && this.magnetometerAccuracy <= this.gyroscopeAccuracy) {
            this.lowerAccuracy = this.magnetometerAccuracy;
        }

        if (this.gyroscopeAccuracy <= this.magnetometerAccuracy && this.gyroscopeAccuracy <= this.accelerometerAccuracy) {
            this.lowerAccuracy = this.gyroscopeAccuracy;
        }

        if (this.lowerAccuracy > -1) {
            this.txtAccuracy.setText(this.formatAccuracy(this.lowerAccuracy));
            this.txtAccuracy.setTextColor(this.accuracyToColor(this.lowerAccuracy));
        }
    }

}
