package iss.workshop.adproject;

public class FavoriteDTO {
    private Long cameraId;
    private Double idealPrice;

    public FavoriteDTO() {

    }

    public FavoriteDTO(Long cameraId, Double idealPrice) {
        this.cameraId = cameraId;
        this.idealPrice = idealPrice;
    }

    // Getters and setters
    public Long getCameraId() {
        return cameraId;
    }

    public void setCameraId(Long cameraId) {
        this.cameraId = cameraId;
    }

    public Double getIdealPrice() {
        return idealPrice;
    }

    public void setIdealPrice(Double idealPrice) {
        this.idealPrice = idealPrice;
    }

}
