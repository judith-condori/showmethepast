package com.tesis.yudith.showmethepast.domain.collections.childs;

import java.io.Serializable;

public class ARPosition implements Serializable {
    private double uAngle;
    private double vAngle;
    private double wAngle;

    private double uDistanceCalibration;
    private double vDistanceCalibration;

    private GPSPosition startPosition;
    private GPSPosition targetPosition;

    private ImageSizeInformation imageSizeInformation;

    public boolean isArInformationEmpty() {
        return  this.uDistanceCalibration == 0 &&
                this.vDistanceCalibration == 0 &&
                this.uAngle == 0 && this.vAngle ==0 && this.wAngle ==0;
    }

    public ARPosition() {
        this.startPosition = new GPSPosition();
        this.targetPosition = new GPSPosition();
        this.imageSizeInformation = new ImageSizeInformation();
    }

    public double getuAngle() {
        return uAngle;
    }

    public void setuAngle(double uAngle) {
        this.uAngle = uAngle;
    }

    public double getvAngle() {
        return vAngle;
    }

    public void setvAngle(double vAngle) {
        this.vAngle = vAngle;
    }

    public double getwAngle() {
        return wAngle;
    }

    public void setwAngle(double wAngle) {
        this.wAngle = wAngle;
    }

    public double getuDistanceCalibration() {
        return uDistanceCalibration;
    }

    public void setuDistanceCalibration(double uDistanceCalibration) {
        this.uDistanceCalibration = uDistanceCalibration;
    }

    public double getvDistanceCalibration() {
        return vDistanceCalibration;
    }

    public void setvDistanceCalibration(double vDistanceCalibration) {
        this.vDistanceCalibration = vDistanceCalibration;
    }

    public GPSPosition getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(GPSPosition startPosition) {
        this.startPosition = startPosition;
    }

    public GPSPosition getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(GPSPosition targetPosition) {
        this.targetPosition = targetPosition;
    }

    public ImageSizeInformation getImageSizeInformation() {
        return imageSizeInformation;
    }

    public void setImageSizeInformation(ImageSizeInformation imageSizeInformation) {
        this.imageSizeInformation = imageSizeInformation;
    }
}
