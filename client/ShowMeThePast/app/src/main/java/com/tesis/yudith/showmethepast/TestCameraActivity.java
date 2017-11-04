package com.tesis.yudith.showmethepast;

import com.tesis.yudith.showmethepast.configuration.LanguageManager;
import com.tesis.yudith.showmethepast.configuration.LoginUserManager;
import com.tesis.yudith.showmethepast.domain.collections.OldPicture;
import com.tesis.yudith.showmethepast.domain.collections.TouristicPlace;
import com.tesis.yudith.showmethepast.domain.collections.childs.ARPosition;
import com.tesis.yudith.showmethepast.domain.collections.childs.ImageSizeInformation;
import com.tesis.yudith.showmethepast.domain.collections.childs.MultiLanguageString;
import com.tesis.yudith.showmethepast.helpers.BitmapTools;
import com.tesis.yudith.showmethepast.helpers.JsonTools;
import com.tesis.yudith.showmethepast.rotation.IRotationManager;
import com.tesis.yudith.showmethepast.rotation.RotationManager;
import com.tesis.yudith.showmethepast.rotation.RotationManagerNative;
import com.tesis.yudith.showmethepast.view.ar.ArTestCanvas;
import com.tesis.yudith.showmethepast.view.ar.DialogArInstructionsFragment;
import com.tesis.yudith.showmethepast.view.camera.TestCameraFragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.SeekBar;

public class TestCameraActivity extends AppCompatActivity implements SensorEventListener, Runnable, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final String ARG_TARGET_SERIALIZED_IMAGE = "ArActivity.ARG_TARGET_SERIALIZED_IMAGE";
    public static final String ARG_TARGET_POSITION_OBJECT_KEY = "ArActivity.TARGET_POSITION_OBJECT_KEY";
    public static final String ARG_TARGET_OLD_PICTURE_DOCUMENT = "ArActivity.ARG_TARGET_OLD_PICTURE_DOCUMENT";

    public static final String ARG_TEST_MODE = "ArActivity.TEST_MODE";
    public static final int RETURN_CODE = 1234567;

    private final float SCALE_K_ALTERATION = 2;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;
    private Sensor mRotation;

    private IRotationManager rotationManager;
    private FrameLayout layoutCameraContainer;
    private FrameLayout layoutMainCameraContainer;

    private ArTestCanvas arTestCanvas;

    private Thread thread;

    private boolean finishThread = false;

    private int previousOrientation  = -1;

    TestCameraFragment currentCameraFragment = null;

    FloatingActionButton btnConfirm;
    FloatingActionButton btnCalibrate;
    FloatingActionButton btnZoom;
    FloatingActionButton btnReset;
    FloatingActionButton btnInformation;
    SeekBar seekImageRelation;

    float opacity = 1;

    private ARPosition originalArPosition;
    private ARPosition workingArPosition;

    private OldPicture targetOldPicture;
    private TouristicPlace targetTouristicPlace;

    private Bitmap targetBitmap;

    private boolean isTestMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle options = this.getIntent().getExtras();

        if (options != null) {
            this.originalArPosition = (ARPosition) options.getSerializable(ARG_TARGET_POSITION_OBJECT_KEY);
            this.targetBitmap = BitmapTools.decodeFromBase64(options.getString(ARG_TARGET_SERIALIZED_IMAGE));

            this.targetOldPicture = (OldPicture) options.getSerializable(ARG_TARGET_OLD_PICTURE_DOCUMENT);
            this.targetTouristicPlace = MyApp.getCurrent().getAppDaos().getCommonsDao().findOne(targetOldPicture.getTouristicPlace(), TouristicPlace.class);

            this.isTestMode = false;
        } else {
            this.isTestMode = true;
        }

        boolean useGyroscope = MyApp.getCurrent().getConfiguration().readUseGyroscope();

        if (useGyroscope) {
            this.rotationManager = new RotationManagerNative();
        } else {
            this.rotationManager = new RotationManager();
        }

        //this.rotationManager = new RotationManager();
        this.startSensors();
        this.makeFullScreen();

        setContentView(R.layout.activity_test_camera);
        this.startInformation();
        this.linkControls(savedInstanceState);
        this.resetAll();
    }

    private void resetAll() {
        this.workingArPosition = JsonTools.cloneByJson(this.originalArPosition, ARPosition.class);
        this.arTestCanvas.onHoldMode();

        try {
            Thread.sleep(20);
        } catch (Exception err) {

        }

        this.setSeekAlterationHeight((float)this.workingArPosition.getImageSizeInformation().getAlterationHeight());
        this.arTestCanvas.setArPosition(this.workingArPosition);

        this.changeToNormalMode();
    }

    private void changeToNormalMode() {
        if (this.isTestMode) {
            this.btnConfirm.setVisibility(View.INVISIBLE);
            this.btnInformation.setVisibility(View.INVISIBLE);
        } else {
            if (LoginUserManager.getCurrent().isCurrentUserAnEditor()) {
                this.btnConfirm.setImageResource(R.drawable.ic_save_black_24dp);
                this.btnConfirm.setVisibility(View.VISIBLE);
            } else {
                this.btnConfirm.setVisibility(View.INVISIBLE);
            }
        }

        this.seekImageRelation.setVisibility(View.INVISIBLE);
        this.btnZoom.setVisibility(View.VISIBLE);
        this.btnCalibrate.setVisibility(View.VISIBLE);

        if (this.workingArPosition.isArInformationEmpty()) {
            this.arTestCanvas.onHoldMode();
        } else {
            this.arTestCanvas.augmentedRealityMode();
        }
    }

    private void changeToZoomMode() {
        this.btnConfirm.setVisibility(View.VISIBLE);
        this.btnConfirm.setImageResource(R.drawable.ic_done_black_24dp);

        this.seekImageRelation.setVisibility(View.VISIBLE);
        this.btnZoom.setVisibility(View.INVISIBLE);
        this.btnCalibrate.setVisibility(View.INVISIBLE);
    }

    private void changeToCalibrateMode() {
        this.btnConfirm.setVisibility(View.VISIBLE);
        this.btnConfirm.setImageResource(R.drawable.ic_done_black_24dp);

        this.seekImageRelation.setVisibility(View.INVISIBLE);
        this.btnZoom.setVisibility(View.INVISIBLE);
        this.btnCalibrate.setVisibility(View.INVISIBLE);
    }


    private void startInformation() {
        if (this.targetBitmap == null) {
            this.targetBitmap = BitmapTools.fromAssets(this.getApplicationContext(), "ghost.png");
        }

        if (this.originalArPosition == null || this.originalArPosition.isArInformationEmpty()) {
            this.originalArPosition = this.createInitialArPosition(this.originalArPosition);
        }

        this.workingArPosition = JsonTools.cloneByJson(this.originalArPosition, ARPosition.class);
    }

    private DisplayMetrics getMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    private ARPosition createInitialArPosition(ARPosition positionBase) {
        ARPosition newOne;

        if (positionBase != null) {
            newOne = JsonTools.cloneByJson(positionBase, ARPosition.class);
        } else {
            newOne = new ARPosition();
        }

        ImageSizeInformation sizeInfo = newOne.getImageSizeInformation();
        DisplayMetrics metrics = this.getMetrics();

        sizeInfo.setScale(1);
        sizeInfo.setAlterationHeight(1);
        sizeInfo.setAlterationWidth(1);
        sizeInfo.setScreenWidth(metrics.widthPixels);
        sizeInfo.setScreenHeight(metrics.heightPixels);
        sizeInfo.setScreenDensityWidth(metrics.xdpi);
        sizeInfo.setScreenDensityHeight(metrics.ydpi);

        return newOne;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (this.previousOrientation == -1 || newConfig.orientation != this.previousOrientation ) {
            //Toast.makeText(this.getBaseContext(), "Rotated!!!", Toast.LENGTH_LONG).show();
            this.previousOrientation = newConfig.orientation;
            this.currentCameraFragment.onResume();
        }
    }

    void makeFullScreen() {
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.


        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |  View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        //int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOptions);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void linkControls(Bundle savedInstanceState) {
        if (null == savedInstanceState) {

            this.currentCameraFragment = TestCameraFragment.newInstance();

            getFragmentManager().beginTransaction()
                    .replace(R.id.testCameraContainer, currentCameraFragment)
                    .commit();
        }

        this.layoutMainCameraContainer = (FrameLayout) this.findViewById(R.id.testCameraContainerControls);
        this.btnConfirm = (FloatingActionButton) this.findViewById(R.id.btn_testCameraActivity_confirm);
        this.btnReset = (FloatingActionButton) this.findViewById(R.id.btn_arActivity_reset);
        this.btnCalibrate = (FloatingActionButton) this.findViewById(R.id.btn_arActivity_calibrate);
        this.btnZoom = (FloatingActionButton) this.findViewById(R.id.btn_arActivity_zoom);
        this.seekImageRelation = (SeekBar) this.findViewById(R.id.seek_arActivity_imageRelation);
        this.btnInformation = (FloatingActionButton) this.findViewById(R.id.btn_arActivity_information);

        this.btnConfirm.setOnClickListener(this);
        this.btnReset.setOnClickListener(this);
        this.btnCalibrate.setOnClickListener(this);
        this.btnZoom.setOnClickListener(this);
        this.seekImageRelation.setOnSeekBarChangeListener(this);
        this.btnInformation.setOnClickListener(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        this.opacity = 0.5f;
        this.arTestCanvas = new ArTestCanvas(this, this.targetBitmap, this.opacity, this.rotationManager, metrics);
        this.layoutMainCameraContainer.addView(arTestCanvas, 0);

        thread = new Thread(this);
        thread.start();

        /*
        String title = this.getResources().getString(R.string.label_instructions);
        String instructions = this.getResources().getString(R.string.message_instructions_zoom);
        this.displayInstructions(title, instructions);
        */
    }

    public void displayInstructions(String title, String instructions) {
        FragmentManager fm = this.getSupportFragmentManager();
        DialogArInstructionsFragment instructionsFragment = DialogArInstructionsFragment.newInstance(title, instructions);
        instructionsFragment.show(fm, "fragment_dialog_ar_instructions");
    }

    private void startSensors() {
        this.mSensorManager = (SensorManager) this.getSystemService(Activity.SENSOR_SERVICE);
        this.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void onResume() {
        super.onResume();

        this.mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        this.mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        this.mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_GAME);
        this.mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int currentSensorType = event.sensor.getType();
        int currentRotation = this.getWindow().getWindowManager().getDefaultDisplay().getRotation();
        this.rotationManager.setInformation(currentSensorType, currentRotation, event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.finishThread = true;
        thread.interrupt();
    }

    @Override
    public void run() {

        while (!this.finishThread) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    arTestCanvas.invalidate();
                }
            });

            try {
                Thread.sleep(10);
            } catch (Exception error) {

            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    this.opacity+=0.1f;
                    if (this.opacity > 1) {
                        this.opacity = 1;
                    }
                    this.updateOpacity();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    this.opacity-=0.1;
                    if (this.opacity <= 0) {
                        this.opacity = 0.1f;
                    }
                    this.updateOpacity();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private void updateOpacity() {
        this.arTestCanvas.setImageOpacity(this.opacity);
    }

    @Override
    public void onClick(View v) {
        String title = this.getResources().getString(R.string.label_instructions);
        String instructions;

        switch (v.getId()) {
            case R.id.btn_testCameraActivity_confirm:
                switch (this.arTestCanvas.getCurrentMode()) {
                    case CALIBRATING:
                        if (this.arTestCanvas.calibrateEvent()) {
                            this.changeToNormalMode();
                        }
                        break;
                    case ADJUST_ZOOM:
                        this.changeToNormalMode();
                        break;
                    case AUGMENTED_REALITY:
                        this.actionSaveAll();
                        break;
                    case ON_HOLD:
                        this.actionSaveAll();
                        break;
                }
                break;
            case R.id.btn_arActivity_calibrate:
                instructions = this.getResources().getString(R.string.message_instructions_calibrate);
                this.displayInstructions(title, instructions);
                this.changeToCalibrateMode();
                this.arTestCanvas.calibrationMode();
                break;
            case R.id.btn_arActivity_zoom:
                instructions = this.getResources().getString(R.string.message_instructions_zoom);
                this.displayInstructions(title, instructions);
                this.changeToZoomMode();
                this.arTestCanvas.zoomMode();
                break;
            case R.id.btn_arActivity_reset:
                this.resetAll();
                break;
            case R.id.btn_arActivity_information:
                this.displayInformation();
                break;
        }
    }

    private void displayInformation() {
        int size = this.targetTouristicPlace.getInformationList().size();
        if (size == 0) {
            return;
        }

        int randomItem = (int)(Math.random() * size);
        MultiLanguageString multiLanguageString = this.targetTouristicPlace.getInformationList().get(randomItem);
        String targetInformation = LanguageManager.translate(multiLanguageString);

        this.displayInstructions(this.getString(R.string.title_information), targetInformation);
    }

    private void actionSaveAll() {
        Intent extra = new Intent();

        this.stopAll();

        extra.putExtra(ARG_TARGET_POSITION_OBJECT_KEY, this.workingArPosition);
        this.setResult(RETURN_CODE, extra);
        this.finish();
    }

    private void stopAll() {
        this.arTestCanvas.onHoldMode();
        this.finishThread = true;

        try {
            Thread.sleep(20);
        } catch (Exception err) {

        }
    }

    @Override
    public void onBackPressed() {
        this.stopAll();
        super.onBackPressed();
    }

    /*
        * Inverse formula for onProgressChanged
        * */
    private void setSeekAlterationHeight(float alteration) {
        if (alteration <= 0) {
            alteration = 1;
        }

        int max = this.seekImageRelation.getMax();
        float half = max/2.0f;
        float k = SCALE_K_ALTERATION;
        double progress = (half * Math.log(alteration)) / (Math.log(2) * k) + half;

        this.seekImageRelation.setProgress((int)progress);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int max = seekBar.getMax();
        int half = max / 2;
        float seed = (float)(progress - half) / half;
        float alteration = (float)Math.pow(2, 2*seed);
        //this.arTestCanvas.setZoomAlteration(new SizeF(1, alteration));
        this.workingArPosition.getImageSizeInformation().setAlterationHeight(alteration);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
