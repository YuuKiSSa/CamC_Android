package iss.workshop.adproject;

import java.util.List;

public class CameraFavouriteDTO {
    private long id;
    private Brand brand;
    private String model;
    private double idealPrice;
    private String imageUrl;
    private double latestPrice;

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getIdealPrice() {
        return idealPrice;
    }

    public void setIdealPrice(double initialPrice) {
        this.idealPrice = initialPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

}
