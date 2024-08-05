package iss.workshop.adproject;

import java.util.List;

public class UserReviewDTO {
    private long userId;
    private List<ReviewDetailDTO> reviews;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<ReviewDetailDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDetailDTO> reviews) {
        this.reviews = reviews;
    }
}