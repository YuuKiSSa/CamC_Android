package iss.workshop.adproject;

import java.time.LocalDateTime;

public class ReviewDetailDTO {
    private String userName;
    private int rating;
    private String comment;
    private LocalDateTime date;

    // Default constructor (required for deserialization)
    public ReviewDetailDTO() {
    }

    // Parameterized constructor
    public ReviewDetailDTO(String userName, int rating, String comment, LocalDateTime date) {
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    // Getters and setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
