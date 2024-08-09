package iss.workshop.adproject;

public class PriceDetailDTO {
    private String productName;
    private double price;
    private String link;

    // Constructors
    public PriceDetailDTO() {}

    public PriceDetailDTO(String productName, double price, String link) {
        this.productName = productName;
        this.price = price;
        this.link = link;
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}