package iss.workshop.adproject;

import java.io.Serializable;
import java.time.LocalDate;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CameraDetailDTO implements Parcelable, Serializable {
    private static final long serialVersionUID = 1L; // Serializable 的版本号

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

    // Default constructor
    public CameraDetailDTO() {}

    // Constructor for Parcelable
    protected CameraDetailDTO(Parcel in) {
        brand = in.readString();
        model = in.readString();
        category = in.readString();
        description = in.readString();
        releaseTime = LocalDate.parse(in.readString(), DateTimeFormatter.ISO_LOCAL_DATE);
        initialPrice = in.readDouble();
        effectivePixel = in.readDouble();
        ISO = in.readInt();
        focusPoint = (Integer) in.readSerializable();
        continuousShot = in.readInt();
        videoResolution = in.readInt();
        videoRate = in.readInt();
    }

    public static final Creator<CameraDetailDTO> CREATOR = new Creator<CameraDetailDTO>() {
        @Override
        public CameraDetailDTO createFromParcel(Parcel in) {
            return new CameraDetailDTO(in);
        }

        @Override
        public CameraDetailDTO[] newArray(int size) {
            return new CameraDetailDTO[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(releaseTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dest.writeDouble(initialPrice);
        dest.writeDouble(effectivePixel);
        dest.writeInt(ISO);
        dest.writeSerializable(focusPoint);
        dest.writeInt(continuousShot);
        dest.writeInt(videoResolution);
        dest.writeInt(videoRate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters (unchanged)
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
