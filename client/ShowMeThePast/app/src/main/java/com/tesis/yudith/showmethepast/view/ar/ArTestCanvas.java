package com.tesis.yudith.showmethepast.view.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.tesis.yudith.showmethepast.domain.collections.childs.ARPosition;
import com.tesis.yudith.showmethepast.tools.MathTools;
import com.tesis.yudith.showmethepast.helpers.BitmapTools;
import com.tesis.yudith.showmethepast.rotation.IRotationManager;
import com.tesis.yudith.showmethepast.tools.StringTools;
import com.tesis.yudith.showmethepast.rotation.UVWVector;
import com.tesis.yudith.showmethepast.tools.ZoomTools;
import com.tesis.yudith.showmethepast.view.helpers.ScaleListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArTestCanvas extends View implements ScaleListener.ScaleExtraListener {
    Context context;
    IRotationManager rotationManager;
    Paint currentPainter;
    Paint currentGhostPainter;

    Paint painterUSolid;
    Paint painterUGuide;

    Paint painterVSolid;
    Paint painterVGuide;

    Paint painterWSolid;
    Paint painterWGuide;

    private Bitmap bmpGhost;
    private Rect rectGhost;
    //private String currentInstructions;
    private ArCalibrator arCalibrator;
    private ArrayList<Point> arCalibrationPoints;
    private int calibrationIndex;
    //private ArPin arGhostPin;
    private DisplayMetrics metrics;

    private ScaleGestureDetector scaleDetector;
    private ScaleListener scaleListener;

    private float imageOpacity;

    public enum EMode {
        ON_HOLD,
        AUGMENTED_REALITY,
        ADJUST_ZOOM,
        CALIBRATING
    }

    ARPosition targetPosition;
    Bitmap targetImage;

    private EMode currentMode = EMode.ON_HOLD;

    public void setArPosition(ARPosition arPosition) {
        this.targetPosition = arPosition;
    }


    public EMode getCurrentMode() {
        return this.currentMode;
    }

    private void startVectorPainters() {
        this.painterUSolid = new Paint();
        this.painterUGuide = new Paint();

        this.painterVSolid = new Paint();
        this.painterVGuide = new Paint();

        this.painterWSolid = new Paint();
        this.painterWGuide = new Paint();

        this.painterUSolid.setStyle(Paint.Style.FILL);
        this.painterUSolid.setColor(Color.BLUE);
        this.painterUSolid.setStrokeWidth(2);

        this.painterVSolid.setStyle(Paint.Style.FILL);
        this.painterVSolid.setColor(Color.GREEN);
        this.painterVSolid.setStrokeWidth(2);

        this.painterWSolid.setStyle(Paint.Style.FILL);
        this.painterWSolid.setColor(Color.RED);
        this.painterWSolid.setStrokeWidth(2);
        this.painterUGuide.setAlpha(150);

        this.painterUGuide.setStyle(Paint.Style.FILL);
        this.painterUGuide.setColor(Color.BLUE);
        this.painterUGuide.setStrokeWidth(1);
        //this.painterUGuide.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));
        this.painterUGuide.setAlpha(150);

        this.painterVSolid.setStyle(Paint.Style.FILL);
        this.painterVGuide.setColor(Color.GREEN);
        this.painterVGuide.setStrokeWidth(1);
        //this.painterVGuide.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));
        this.painterVGuide.setAlpha(150);

        this.painterWGuide.setStyle(Paint.Style.FILL);
        this.painterWGuide.setColor(Color.RED);
        this.painterWGuide.setStrokeWidth(1);
        //this.painterWGuide.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));
        this.painterWGuide.setAlpha(150);
    }

    public ArTestCanvas(Context context, Bitmap targetImage, float imageOpacity, IRotationManager rotationManager, DisplayMetrics metrics) {
        super(context);

        this.currentMode = EMode.ON_HOLD;
        this.targetImage = targetImage;
        this.imageOpacity = imageOpacity;
        this.context = context;
        this.rotationManager = rotationManager;
        this.metrics = metrics;

        this.currentPainter = new Paint();
        this.currentGhostPainter = new Paint();
        this.arCalibrator = new ArCalibrator();
        this.arCalibrationPoints = new ArrayList<>();
        //this.currentInstructions = "Touch when ghost is centered. " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.setupGhost();
        this.createArCalibrationPoints();
        this.startVectorPainters();

        this.scaleListener = new ScaleListener(0.01f, 16f, 1, this);
        this.scaleDetector = new ScaleGestureDetector(this.getContext(), this.scaleListener);
    }

    public void setImageOpacity(float imageOpacity) {
        this.imageOpacity = imageOpacity;
        this.currentGhostPainter.setAlpha((int)(255*imageOpacity));
    }

    @Override
    public void onScaleChanged(float newScale) {
        this.targetPosition.getImageSizeInformation().setScale(newScale);
    }

    private void createArCalibrationPoints() {
        this.calibrationIndex = 0;
        this.arCalibrationPoints.clear();

        int w = this.getWidth();
        int h = this.getHeight();

        this.arCalibrationPoints.add(new Point(w/2, h/2));
        this.arCalibrationPoints.add(new Point(w/4, h/4));
        this.arCalibrationPoints.add(new Point(3*w/4, 3*h/4));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (this.currentMode) {
            case ADJUST_ZOOM:
                this.scaleDetector.onTouchEvent(event);
                break;
        }

        return true;
    }

    public boolean isCalibrationCompleted() {
        return this.calibrationIndex >= this.arCalibrationPoints.size();
    }

    /*
        Returns true if the calibration finished
     */
    public boolean calibrateEvent() {
        if (this.calibrationIndex < this.arCalibrationPoints.size()) {
            Point targetPoint = this.arCalibrationPoints.get(this.calibrationIndex);
            this.arCalibrator.addPoint(this.rotationManager.getRotation(), targetPoint.x, targetPoint.y);
            this.calibrationIndex++;

            if (this.calibrationIndex == this.arCalibrationPoints.size()) {
                ArPin arGhostPin = this.arCalibrator.calibrate();
                this.transferArPinInformation(arGhostPin, this.targetPosition);
                //Toast.makeText(this.context, "Calibration Finished! : " + this.arGhostPin.getCalibration().distanceU + " " + this.arGhostPin.getCalibration().distanceV, Toast.LENGTH_LONG).show();
                Toast.makeText(this.context, "Calibration Finished!!!", Toast.LENGTH_LONG).show();
                return true;
            } else {
                Toast.makeText(this.context, "Saved Point!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void transferArPinInformation(ArPin arGhostPin, ARPosition targetPosition) {
        targetPosition.setuAngle(arGhostPin.getPosition().u);
        targetPosition.setvAngle(arGhostPin.getPosition().v);
        targetPosition.setwAngle(arGhostPin.getPosition().w);
        targetPosition.setuDistanceCalibration(arGhostPin.getCalibration().distanceU);
        targetPosition.setvDistanceCalibration(arGhostPin.getCalibration().distanceV);
    }

    void setupGhost() {
        this.setImageOpacity(this.imageOpacity);
        this.bmpGhost = this.targetImage;
        this.rectGhost = new Rect(0, 0, this.bmpGhost.getWidth(), this.bmpGhost.getHeight());
    }
/*
    private void updateRectGhost() {

        int w = this.bmpGhost.getWidth();
        //int h = this.bmpGhost.getHeight();

        float multiplier = this.getWidth()/2.0f/w;

        this.rectGhost = new Rect(0, 0, (int)(this.bmpGhost.getWidth()*multiplier), (int)(this.bmpGhost.getHeight()*multiplier));
    }
*/
    int getDelta(int w, int h) {
        int minimum = (w < h)? w: h;
        return minimum / 2;
    }

    private void drawCustomPointer(CanvasInformation ci, int centerX, int centerY, int color, int strokeWidth) {
        this.currentPainter.setStyle(Paint.Style.STROKE);
        this.currentPainter.setColor(color);
        this.currentPainter.setStrokeWidth(strokeWidth);

        ci.canvas.drawLine(centerX - ci.delta / 8, centerY, centerX + ci.delta / 8, centerY, this.currentPainter);
        ci.canvas.drawLine(centerX, centerY  - ci.delta / 8, centerX, centerY  + ci.delta / 8, this.currentPainter);
    }

    private void drawPointer(CanvasInformation ci) {
        int centerX = ci.w / 2;
        int centerY = ci.h / 2;
        int strokeWidth = 1;

        this.drawCustomPointer(ci, centerX, centerY, Color.WHITE, strokeWidth);
    }

    private void drawCalibrationPointer(CanvasInformation ci) {
        if (this.calibrationIndex >= this.arCalibrationPoints.size()) {
            return;
        }

        Point targetPoint = this.arCalibrationPoints.get(this.calibrationIndex);
        int centerX = targetPoint.x;
        int centerY = targetPoint.y;

        this.drawCustomPointer(ci, centerX, centerY, Color.GREEN, 2);

        //int bmpWidth = (int)(ci.delta * this.scaleListener.getScaleFactor());
        //int bmpHeight = (int)((float)bmpWidth / rectGhost.width() * rectGhost.height());
        //float scaleFactor = this.scaleListener.getScaleFactor();

        //int bmpWidth = (int)(ci.delta * 2 * this.zoomProportions.getWidth() * scaleFactor * this.zoomAlterations.getWidth());
        //int bmpHeight = (int)(ci.delta * 2 * this.zoomProportions.getHeight() * scaleFactor * this.zoomAlterations.getHeight());\\

        SizeF imageSize = ZoomTools.getImageSize(this.bmpGhost, this.targetPosition.getImageSizeInformation());
        int bmpWidth = (int)imageSize.getWidth();
        int bmpHeight = (int)imageSize.getHeight();


        Rect targetRect = new Rect(centerX - bmpWidth/2, centerY - bmpHeight / 2, centerX + bmpWidth/2, centerY + bmpHeight / 2);
        ci.canvas.drawBitmap(this.bmpGhost, rectGhost, targetRect, this.currentGhostPainter);
    }

    private void drawGhostPin(CanvasInformation ci) {
        //int bmpWidth = (int)(ci.delta * this.scaleListener.getScaleFactor());
        //int bmpHeight = (int)((float)bmpWidth / rectGhost.width() * rectGhost.height());

        //float scaleFactor = this.scaleListener.getScaleFactor();
        //int bmpWidth = (int)(ci.delta * 2 * this.zoomProportions.getWidth() * scaleFactor * this.zoomAlterations.getWidth());
        //int bmpHeight = (int)(ci.delta * 2 * this.zoomProportions.getHeight() * scaleFactor * this.zoomAlterations.getHeight());

        SizeF imageSize = ZoomTools.getImageSize(this.bmpGhost, this.targetPosition.getImageSizeInformation());
        int bmpWidth = (int)imageSize.getWidth();
        int bmpHeight = (int)imageSize.getHeight();



        //double deltaU = ArOperations.angleDistance(this.rotationManager.getRotation().u, this.arGhostPin.getPosition().u);
        //double deltaV = ArOperations.angleDistance(this.rotationManager.getRotation().v, this.arGhostPin.getPosition().v);
        //double distanceV = ArOperations.calculateArX(this.arGhostPin.getCalibration().distanceV, deltaU);
        //double distanceU = ArOperations.calculateArX(this.arGhostPin.getCalibration().distanceU, deltaV);

        double deltaU = ArOperations.angleDistance(this.rotationManager.getRotation().u, this.targetPosition.getuAngle());
        double deltaV = ArOperations.angleDistance(this.rotationManager.getRotation().v, this.targetPosition.getvAngle());
        double distanceV = ArOperations.calculateArX(this.targetPosition.getvDistanceCalibration(), deltaU);
        double distanceU = ArOperations.calculateArX(this.targetPosition.getuDistanceCalibration(), deltaV);

        int centerX = (int)(ci.w / 2 + distanceU);
        int centerY = (int)(ci.h / 2 - distanceV);

        Rect targetRect = new Rect(centerX - bmpWidth/2, centerY - bmpHeight / 2, centerX + bmpWidth/2, centerY + bmpHeight / 2);

        /*
        Matrix rotator = new Matrix();

        rotator.preScale((float)bmpSize / this.bmpGhost.getWidth(), (float)bmpSize / this.bmpGhost.getHeight());
        //rotator.postRotate(90);

        int xRotate = this.bmpGhost.getWidth() / 2;
        int yRotate = this.bmpGhost.getHeight() / 2;
        rotator.postRotate(90, xRotate, yRotate);

        int xTranslate = centerX;
        int yTranslate = centerY;

        rotator.postTranslate(xTranslate - bmpSize / 2, yTranslate - bmpSize / 2);
        ci.canvas.drawBitmap(this.bmpGhost, rotator, this.currentGhostPainter);
        */

        ci.canvas.drawBitmap(this.bmpGhost, rectGhost, targetRect, this.currentGhostPainter);
    }

    private String formatAngle(double angle) {
         return StringTools.formatNumber(MathTools.radToDeg(angle), 2);
    }

    private void drawRotationValues(CanvasInformation ci) {

        this.currentPainter.setStyle(Paint.Style.FILL);
        this.currentPainter.setColor(Color.WHITE);
        this.currentPainter.setStrokeWidth(2);

        int spSize = 10;
        float scaledSizeInPixels = spSize * getResources().getDisplayMetrics().scaledDensity;
        this.currentPainter.setTextSize(scaledSizeInPixels);

        UVWVector rotation = this.rotationManager.getRotation();

        ci.canvas.drawText("u: " + this.formatAngle(rotation.u), 20, 50, this.currentPainter);
        ci.canvas.drawText("v: " + this.formatAngle(rotation.v), 20, 80, this.currentPainter);
        ci.canvas.drawText("w: " + this.formatAngle(rotation.w), 20, 110, this.currentPainter);
    }

    private void drawWAngleInformation(CanvasInformation ci) {
        UVWVector rotation = this.rotationManager.getRotation();

        float r = ci.delta / 2;
        float dx = (float)(r * Math.cos(rotation.w));
        float dy = (float)(r * Math.sin(rotation.w));

        float x1 = ci.w / 2 + dx;
        float y1 = ci.h / 2 - dy;

        float x2 = ci.w / 2 - dx;
        float y2 = ci.h / 2 + dy;

        ci.canvas.drawLine(x1, y1, x2, y2, this.painterWSolid);
    }

    private void drawUAngleInformation(CanvasInformation ci) {
        float miniDelta = ci.delta / 8;
        float referenceBarSize = 3 * ci.delta / 4;
        float delta2 = referenceBarSize / 2;

        UVWVector rotation = this.rotationManager.getRotation();

        ci.canvas.drawLine(miniDelta, ci.h /2 - delta2, miniDelta + miniDelta, ci.h /2 - delta2, this.painterUSolid);
        ci.canvas.drawLine(miniDelta + miniDelta / 2, ci.h /2 - delta2, miniDelta + miniDelta / 2, ci.h /2 + delta2, this.painterUSolid);
        ci.canvas.drawLine(miniDelta, ci.h /2 + delta2, miniDelta + miniDelta, ci.h /2 + delta2, this.painterUSolid);

        ci.canvas.drawLine(miniDelta, ci.h /2, miniDelta + miniDelta, ci.h /2, this.painterUSolid);


        double portion = rotation.u / (Math.PI / 2);

        Path path = new Path();

        int pathCenterY = (int) (ci.h / 2 - delta2 * portion);

        path.moveTo(miniDelta + miniDelta / 2, pathCenterY);
        path.lineTo(miniDelta, pathCenterY + miniDelta / 4);
        path.lineTo(miniDelta, pathCenterY - miniDelta / 4);
        path.close();

        ci.canvas.drawLine(miniDelta + miniDelta / 2, pathCenterY, ci.w - (miniDelta + miniDelta / 2), pathCenterY, this.painterUGuide);

        ci.canvas.drawPath(path, this.painterUSolid);
    }

    private void drawVAngleInformation(CanvasInformation ci) {
        float miniDelta = ci.delta / 7;
        float referenceBarSize = 3 * ci.delta / 4;
        float delta2 = referenceBarSize;

        UVWVector rotation = this.rotationManager.getRotation();

        ci.canvas.drawLine(ci.w / 2 - delta2, ci.h - miniDelta, ci.w / 2 - delta2, ci.h - miniDelta - miniDelta, this.painterVSolid);
        ci.canvas.drawLine(ci.w / 2 - delta2, ci.h - miniDelta - miniDelta / 2, ci.w / 2 + delta2, ci.h - miniDelta - miniDelta / 2, this.painterVSolid);
        ci.canvas.drawLine(ci.w / 2 + delta2, ci.h - miniDelta, ci.w / 2 + delta2, ci.h - miniDelta - miniDelta, this.painterVSolid);

        ci.canvas.drawLine(ci.w / 2, ci.h - miniDelta, ci.w / 2, ci.h - miniDelta - miniDelta, this.painterVSolid);

        Path path = new Path();

        float barWidth = delta2;
        double angle = rotation.v;

        double portion = angle / Math.PI;

        int pathCenterX = ci.w / 2 + (int)(barWidth * portion);

        path.moveTo(pathCenterX, ci.h - miniDelta - miniDelta / 2);
        path.lineTo(pathCenterX - miniDelta / 4, ci.h - miniDelta);
        path.lineTo(pathCenterX + miniDelta / 4, ci.h - miniDelta);
        path.close();

        ci.canvas.drawLine(pathCenterX, ci.h - miniDelta - miniDelta / 2, pathCenterX,  miniDelta + miniDelta / 2, this.painterVGuide);

        ci.canvas.drawPath(path, this.painterVSolid);
    }

    private CanvasInformation createCanvasInformation(Canvas canvas) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int delta = this.getDelta(w, h);
        int max = w > h? w: h;

        return new CanvasInformation(canvas, w, h, delta, max);
    }

    /*
    void drawInstructions(CanvasInformation ci) {
        Rect rect = new Rect();

        this.currentPainter.setTextAlign(Paint.Align.LEFT);
        this.currentPainter.getTextBounds(this.currentInstructions, 0 ,this.currentInstructions.length(), rect);

        int x = ci.w /2 - rect.width() / 2;
        int y = ci.delta/8;

        ci.canvas.drawText(this.currentInstructions, x, y, this.currentPainter);
    }
    */

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld)
    {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        this.createArCalibrationPoints();

    }

    protected void onDraw(Canvas canvas) {
        if (this.rotationManager == null) {
            return;
        }

        CanvasInformation canvasInformation = createCanvasInformation(canvas);

        //this.drawInstructions(canvasInformation);

        this.drawUAngleInformation(canvasInformation);
        this.drawVAngleInformation(canvasInformation);
        this.drawWAngleInformation(canvasInformation);

        this.drawPointer(canvasInformation);

        if (this.currentMode == EMode.AUGMENTED_REALITY /*&& this.arGhostPin != null*/ && !this.targetPosition.isArInformationEmpty()) {
            this.drawGhostPin(canvasInformation);
        } else {
            this.drawCalibrationPointer(canvasInformation);
        }

        this.drawRotationValues(canvasInformation);
    }

    public void calibrationMode() {
        this.createArCalibrationPoints();
        this.currentMode = EMode.CALIBRATING;
    }

    public void zoomMode() {
        this.createArCalibrationPoints();
        this.currentMode = EMode.ADJUST_ZOOM;
    }

    public void augmentedRealityMode() {
        this.createArCalibrationPoints();
        this.currentMode = EMode.AUGMENTED_REALITY;
    }

    public void onHoldMode() {
        this.currentMode = EMode.ON_HOLD;
    }

}
