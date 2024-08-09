package iss.workshop.adproject;

import java.util.List;

public class PriceDTO {
    private String platform;
    private List<PriceDetailDTO> details;

    public PriceDTO() {}

    public PriceDTO(String platform, List<PriceDetailDTO> details) {
        this.platform = platform;
        this.details = details;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<PriceDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<PriceDetailDTO> details) {
        this.details = details;
    }
}