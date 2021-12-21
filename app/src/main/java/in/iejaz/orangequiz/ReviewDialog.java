package in.iejaz.orangequiz;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ReviewDialog extends AppCompatDialogFragment {

    RatingBar ratingBar;
    EditText review;
    float userRating;
    String userReview;
    myReviewDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // dialog box to get review from users
//                RatingBar ratingBar ;
//


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.review_dialog,null);

        ratingBar = dialogView.findViewById(R.id.rating_bar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                userRating = rating;
            }
        });

        review = dialogView.findViewById(R.id.review_text);


                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                dialogView.findViewById(R.id.btn_post)
                        .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getContext(), "ok", Toast.LENGTH_SHORT).show();
                        userReview = review.getText().toString();
                        if (userReview.equals("") || TextUtils.isEmpty(userReview)){
                            review.requestFocus();
                            review.setError("Please provide something");
                        }else {

                            listener.applyReview(userRating, userReview);
                            alertDialog.cancel();
                        }
                    }
                });





        return alertDialog;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (myReviewDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement myReviewDialogListener");
        }
    }

    public interface myReviewDialogListener {
        void applyReview(float rating, String review);
    }
}
