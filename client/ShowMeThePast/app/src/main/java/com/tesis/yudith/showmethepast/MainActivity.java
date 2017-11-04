package com.tesis.yudith.showmethepast;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.tesis.yudith.showmethepast.rotation.RotationManager;
import com.tesis.yudith.showmethepast.rotation.UVWVector;
import com.tesis.yudith.showmethepast.tools.MathTools;
import com.tesis.yudith.showmethepast.tools.StringTools;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {

    RotationManager rotationManager;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;

    TextView txtValueX;
    TextView txtValueY;
    TextView txtValueZ;

    TextView txtAngleXZ;
    TextView txtAngleXY;
    TextView txtAngleYZ;

    TextView txtAngleUV;
    TextView txtAngleWV;
    TextView txtAngleUW;

    Button btnMode1;
    Button btnMode2;
    Button btnMode3;

    ConstraintLayout layoutSensors;

    int selectedMode = -1;

    MainActivity self;

    String formatNumber(double number) {
        return StringTools.formatNumber(number, 2);
    }

    void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.rotationManager = new RotationManager();

        self = this;

        this.makeFullScreen();

        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        this.layoutSensors = (ConstraintLayout) this.findViewById(R.id.layoutSensors);

        this.linkControls();

        btnMode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMode = Sensor.TYPE_ACCELEROMETER;
            }
        });

        btnMode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMode = Sensor.TYPE_MAGNETIC_FIELD;
            }
        });

        btnMode3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMode = Sensor.TYPE_GYROSCOPE;
                self.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        });

        self.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    void linkControls() {
        this.txtValueX = (TextView) this.findViewById(R.id.txtValueX);
        this.txtValueY = (TextView) this.findViewById(R.id.txtValueY);
        this.txtValueZ = (TextView) this.findViewById(R.id.txtValueZ);

        this.btnMode1 = (Button) this.findViewById(R.id.btnMode1);
        this.btnMode2 = (Button) this.findViewById(R.id.btnMode2);
        this.btnMode3 = (Button) this.findViewById(R.id.btnMode3);

        this.txtAngleXZ = (TextView) this.findViewById(R.id.txtAngleXZ);
        this.txtAngleXY = (TextView) this.findViewById(R.id.txtAngleXY);
        this.txtAngleYZ = (TextView) this.findViewById(R.id.txtAngleYZ);

        this.txtAngleUV = (TextView) this.findViewById(R.id.txtAngleUV);
        this.txtAngleWV = (TextView) this.findViewById(R.id.textAngleWV);
        this.txtAngleUW = (TextView) this.findViewById(R.id.textAngleUW);
    }

    protected void onResume() {
        super.onResume();

        this.mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        this.mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        this.mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x,y,z;
        double oppositeSide;
        double adjacentSide;
        double xzAngle;
        double xyAngle;
        double yzAngle;

        int currentSensorType = event.sensor.getType();

        if (currentSensorType == Sensor.TYPE_ACCELEROMETER || currentSensorType == Sensor.TYPE_MAGNETIC_FIELD || currentSensorType == Sensor.TYPE_GYROSCOPE) {

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            switch (currentSensorType) {
                case Sensor.TYPE_ACCELEROMETER:
                    this.rotationManager.setAccelerometerInformation(x, y, z);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    this.rotationManager.setMagnetometerInformation(x, y, z);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    this.rotationManager.setGyroscopeInformation(x, y, z);
                    break;
            }

            if (selectedMode == currentSensorType) {

                this.txtValueX.setText("X: " + this.formatNumber(x));
                this.txtValueY.setText("Y: " + this.formatNumber(y));
                this.txtValueZ.setText("Z: " + this.formatNumber(z));

                oppositeSide = z;
                adjacentSide = x;
                xzAngle = MathTools.atanDegrees(oppositeSide, adjacentSide);
                txtAngleXZ.setText("XZ:" + this.formatNumber(xzAngle));

                oppositeSide = y;
                adjacentSide = x;
                xyAngle = MathTools.atanDegrees(oppositeSide, adjacentSide);
                txtAngleXY.setText("XY:" + this.formatNumber(xyAngle));

                oppositeSide = z;
                adjacentSide = y;
                yzAngle = MathTools.atanDegrees(oppositeSide, adjacentSide);
                txtAngleYZ.setText("YZ:" + this.formatNumber(yzAngle));

            }

            if (currentSensorType == Sensor.TYPE_ACCELEROMETER) {
                this.showRotationResults();
            }
        }
    }

    void showRotationResults() {
        UVWVector aInformation = this.rotationManager.getAccelerometerInformation();
        UVWVector mInformation = this.rotationManager.getMagnetometerInformation();

        RotationManager.ERotation currentRotation = this.rotationManager.getCurrentRotation();

        this.rotateScreen(currentRotation);

        double angleUV = MathTools.atanDegrees(aInformation.v, aInformation.u);
        double angleVW = -MathTools.atanDegrees(aInformation.w, aInformation.u);
        double angleUW = MathTools.atanDegrees(mInformation.u, mInformation.w);

        this.txtAngleUV.setText("UV:" + this.formatNumber(angleUV));
        this.txtAngleWV.setText("VW:" + this.formatNumber(angleVW));
        this.txtAngleUW.setText("UW:" + this.formatNumber(angleUW));
    }

    void rotateScreen(RotationManager.ERotation targetRotation) {
        boolean invalidMode = false;

        switch (targetRotation) {
            case X_NEGATIVE:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case X_POSITIVE:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Y_NEGATIVE:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                invalidMode = true;
                break;
            case Y_POSITIVE:
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }

        if (invalidMode) {
            this.layoutSensors.setBackgroundColor(Color.RED);
        } else {
            this.layoutSensors.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

