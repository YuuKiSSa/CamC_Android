package iss.workshop.adproject;

import java.io.Serializable;
import java.time.LocalDate;

public class CameraDetailDTO implements Serializable {
    private String brand;
    private String model;
    private String category;
    private String description;
    private LocalDate releaseTime;
    private double initialPrice;
    private double effectivePixel;
    private int ISO;
    private Integer focusPoint;
    private int continuousShot;
    private int videoResolution;
    private int videoRate;

    // Getters and Setters

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(LocalDate releaseTime) {
        this.releaseTime = releaseTime;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(double initialPrice) {
        this.initialPrice = initialPrice;
    }

    public double getEffectivePixel() {
        return effectivePixel;
    }

    public void setEffectivePixel(double effectivePixel) {
        this.effectivePixel = effectivePixel;
    }

    public int getISO() {
        return ISO;
    }

    public void setISO(int ISO) {
        this.ISO = ISO;
    }

    public Integer getFocusPoint() {
        return focusPoint;
    }

    public void setFocusPoint(Integer focusPoint) {
        this.focusPoint = focusPoint;
    }

    public int getContinuousShot() {
        return continuousShot;
    }

    public void setContinuousShot(int continuousShot) {
        this.continuousShot = continuousShot;
    }

    public int getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(int videoResolution) {
        this.videoResolution = videoResolution;
    }

    public int getVideoRate() {
        return videoRate;
    }

    public void setVideoRate(int videoRate) {
        this.videoRate = videoRate;
    }
}
