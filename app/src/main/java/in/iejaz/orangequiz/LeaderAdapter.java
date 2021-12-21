package in.iejaz.orangequiz;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LeaderAdapter extends FirebaseRecyclerAdapter<LeaderModalClass, LeaderAdapter.myViewHolder> {
    FirebaseUser user;

    public LeaderAdapter(@NonNull FirebaseRecyclerOptions<LeaderModalClass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull LeaderModalClass model) {

        Animation animation = AnimationUtils.loadAnimation(holder.root.getContext(),R.anim.slide_recycler);
        holder.root.setAnimation(animation);

        if (holder.userImg != null){
            Glide.with(holder.userImg.getContext()).load(model.getImgUrl()).into(holder.userImg);
        }
        holder.userPts.setText(String.valueOf(model.gettPoints()+"pts"));
        holder.rank.setText(new StringBuilder().append((Leaderboard.totalLeaders - position)).append("").toString());

        if (user.getDisplayName().equals(model.getName())){
            holder.root.setBackgroundColor(Color.parseColor("#4FCDBA9D"));
        }
        holder.userName.setText(model.getName());



        // linear layout se user ka rank set krne ke liye try kiye the nhi hua
//        if (model.getName().equals(ResultActivity.USERNAME) && model.gettPoints() == ResultActivity.USERPOINTS){
//            Leaderboard.RANK = Leaderboard.totalLeaders - position;
//        }

    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaders_card,parent,false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        return new myViewHolder(view);
    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userImg;
        Chip userPts;
        ConstraintLayout root;
        TextView rank;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            userPts = itemView.findViewById(R.id.leader_user_pts);
            userName = itemView.findViewById(R.id.leader_user_name);
            userImg = itemView.findViewById(R.id.leader_user_img);
            root = itemView.findViewById(R.id.root_leader_user);
            rank = itemView.findViewById(R.id.rank);


        }
    }

}
