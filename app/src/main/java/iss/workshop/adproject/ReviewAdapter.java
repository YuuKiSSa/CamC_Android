package iss.workshop.adproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewDetailDTO> reviewList;

    public ReviewAdapter(List<ReviewDetailDTO> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        ReviewDetailDTO review = reviewList.get(position);
        holder.userNameTextView.setText(review.getUserName());
        holder.commentTextView.setText(review.getComment());
        holder.dateTextView.setText(review.getDate().toString());
        holder.ratingBar.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView commentTextView;
        public TextView dateTextView;
        public RatingBar ratingBar;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.reviewUserName);
            ratingBar = itemView.findViewById(R.id.reviewRatingBar);
            commentTextView = itemView.findViewById(R.id.reviewComment);
            dateTextView = itemView.findViewById(R.id.reviewDate);
        }
    }
}
