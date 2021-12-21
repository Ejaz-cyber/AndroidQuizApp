package in.iejaz.orangequiz;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class myQuizCardAdapter extends FirebaseRecyclerAdapter<QuizCatoModel,myQuizCardAdapter.myViewHolder> {


    public myQuizCardAdapter(@NonNull FirebaseRecyclerOptions<QuizCatoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull final QuizCatoModel model) {
        holder.Title.setText(model.getTitle());
        Glide.with(holder.Img.getContext()).load(model.getImgUrl()).into(holder.Img);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openQuizActivity = new Intent(v.getContext(),QuizActivity.class);
                openQuizActivity.putExtra("QUIZ_TITLE",model.getTitle());
                openQuizActivity.putExtra("imgLogoUrl",model.getImgUrl());
                v.getContext().startActivity(openQuizActivity);

            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_card,parent,false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        ImageView Img;
        CardView root;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.quiz_card_name);
            Img = itemView.findViewById(R.id.quiz_card_image);
            root = itemView.findViewById(R.id.quiz_card_root);

        }
    }

}
