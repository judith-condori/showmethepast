package com.tesis.yudith.showmethepast.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import com.tesis.yudith.showmethepast.R;
import com.tesis.yudith.showmethepast.tools.StringTools;
import com.tesis.yudith.showmethepast.TestCameraActivity;
import com.tesis.yudith.showmethepast.view.navigation.INavigationChild;

public class SensorsStatusFragment extends Fragment implements SensorEventListener, INavigationChild {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;

    TextView txtStatusAccelerometer;
    TextView txtStatusMagnetometer;
    TextView txtStatusGyroscope;

    TextView txtAccelerometerX;
    TextView txtAccelerometerY;
    TextView txtAccelerometerZ;

    TextView txtMagnetometerX;
    TextView txtMagnetometerY;
    TextView txtMagnetometerZ;

    TextView txtGyroscopeX;
    TextView txtGyroscopeY;
    TextView txtGyroscopeZ;

    Button btnTestCamera;
    Button btnCalibrate;

    public SensorsStatusFragment() {
        // Required empty public constructor
    }

    public static SensorsStatusFragment newInstance() {
        SensorsStatusFragment fragment = new SensorsStatusFragment();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensors_status, container, false);

        this.linkControls(view);
        this.startSensors();

        return view;
    }

    private void startSensors() {
        this.mSensorManager = (SensorManager)this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void linkControls(View view) {
        this.txtStatusAccelerometer = (TextView) view.findViewById(R.id.txt_sensorStatus_aStatus);
        this.txtStatusMagnetometer = (TextView) view.findViewById(R.id.txt_sensorStatus_mStatus);
        this.txtStatusGyroscope = (TextView) view.findViewById(R.id.txt_sensorStatus_gStatus);

        this.txtAccelerometerX = (TextView) view.findViewById(R.id.txt_sensorStatus_ax);
        this.txtAccelerometerY = (TextView) view.findViewById(R.id.txt_sensorStatus_ay);
        this.txtAccelerometerZ = (TextView) view.findViewById(R.id.txt_sensorStatus_az);

        this.txtMagnetometerX = (TextView) view.findViewById(R.id.txt_sensorStatus_mx);
        this.txtMagnetometerY = (TextView) view.findViewById(R.id.txt_sensorStatus_my);
        this.txtMagnetometerZ = (TextView) view.findViewById(R.id.txt_sensorStatus_mz);

        this.txtGyroscopeX = (TextView) view.findViewById(R.id.txt_sensorStatus_gx);
        this.txtGyroscopeY = (TextView) view.findViewById(R.id.txt_sensorStatus_gy);
        this.txtGyroscopeZ = (TextView) view.findViewById(R.id.txt_sensorStatus_gz);

        this.btnTestCamera = (Button) view.findViewById(R.id.btn_sensorStatus_testCamera);
        this.btnCalibrate = (Button) view.findViewById(R.id.btn_sensorStatus_calibrate);

        final FragmentActivity currentActivity = this.getActivity();

        this.btnTestCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(currentActivity, TestCameraActivity.class);
                currentActivity.startActivity(myIntent);
            }
        });

        this.btnCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = currentActivity.getSupportFragmentManager();
                SensorsCalibrationDialogFragment editor = SensorsCalibrationDialogFragment.newInstance();
                editor.show(fm, "fragment_sensors_calibration_dialog");
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (this.isDetached() || this.getActivity() == null) {
            return;
        }

        float x,y,z;
        String strX, strY, strZ;
        int decimalPlaces = 2;

        int currentSensorType = event.sensor.getType();

        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        strX = this.getResources().getString(R.string.label_x_value) + StringTools.formatNumber(x, decimalPlaces);
        strY = this.getResources().getString(R.string.label_y_value) + StringTools.formatNumber(y, decimalPlaces);
        strZ = this.getResources().getString(R.string.label_z_value) + StringTools.formatNumber(z, decimalPlaces);

        switch (currentSensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                this.txtAccelerometerX.setText(strX);
                this.txtAccelerometerY.setText(strY);
                this.txtAccelerometerZ.setText(strZ);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.txtMagnetometerX.setText(strX);
                this.txtMagnetometerY.setText(strY);
                this.txtMagnetometerZ.setText(strZ);
                break;
            case Sensor.TYPE_GYROSCOPE:
                this.txtGyroscopeX.setText(strX);
                this.txtGyroscopeY.setText(strY);
                this.txtGyroscopeZ.setText(strZ);
                break;
        }
    }

    private String formatAccuracy(int accuracy) {

        // 0 SENSOR_STATUS_UNRELIABLE
        // 1 SENSOR_STATUS_ACCURACY_LOW
        // 2 SENSOR_STATUS_ACCURACY_MEDIUM
        // 3 SENSOR_STATUS_ACCURACY_HIGH

        String[] meanings = new String[]{
            getResources().getString(R.string.label_unreilable),
            getResources().getString(R.string.label_low),
            getResources().getString(R.string.label_medium),
            getResources().getString(R.string.label_high)
        };

        if (accuracy < 0 || accuracy > meanings.length) {
            return getResources().getString(R.string.label_unknown);
        }

        return meanings[accuracy];
    }

    private int accuracyToColor(int accuracy) {
        // 0 SENSOR_STATUS_UNRELIABLE
        // 1 SENSOR_STATUS_ACCURACY_LOW
        // 2 SENSOR_STATUS_ACCURACY_MEDIUM
        // 3 SENSOR_STATUS_ACCURACY_HIGH

        int[] colors = new int[]{ Color.BLACK, Color.RED, Color.rgb(186, 130, 0), Color.rgb(0, 86, 24)};

        if (accuracy < 0 || accuracy > colors.length) {
            return Color.GRAY;
        }

        return colors[accuracy];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        int currentSensorType = sensor.getType();

        switch (currentSensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                this.txtStatusAccelerometer.setText(this.formatAccuracy(accuracy));
                this.txtStatusAccelerometer.setTextColor(this.accuracyToColor(accuracy));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.txtStatusMagnetometer.setText(this.formatAccuracy(accuracy));
                this.txtStatusMagnetometer.setTextColor(this.accuracyToColor(accuracy));
                break;
            case Sensor.TYPE_GYROSCOPE:
                this.txtStatusGyroscope.setText(this.formatAccuracy(accuracy));
                this.txtStatusGyroscope.setTextColor(this.accuracyToColor(accuracy));
                break;
        }
    }

    @Override
    public String getNavigationTitle(Resources resources) {
        return resources.getString(R.string.title_fragment_sensors);
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void onChildrenClosed(INavigationChild origin, boolean needReload) {

    }
}
