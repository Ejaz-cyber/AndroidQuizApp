package in.iejaz.orangequiz;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.iejaz.orangequiz.databinding.ActivityLeaderboardBinding;

public class Leaderboard extends AppCompatActivity {

    ActivityLeaderboardBinding binding;

    RecyclerView recyclerViewLeaders;
    LinearLayout bottomLayout;
    MaterialCardView cardUser;
    DatabaseReference dbRef;
    LeaderAdapter leaderAdapter;

    public static int totalLeaders;

    ///--------------------------------
    List<LeaderModalClass> listData;

    static int RANK;

    Toolbar toolbar;

    ProgressDialog progressDialog;

//    LinearLayout ll1;
//    LinearLayout ll2;
//    LinearLayout ll3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_leaderboard);        // no need for this because

        // using viewBinding
        binding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


//        ll1 = findViewById(R.id.ll1);
//        ll2 = findViewById(R.id.ll2);
//        ll3 = findViewById(R.id.ll3);

        binding.ll1.setVisibility(View.INVISIBLE);
        binding.ll2.setVisibility(View.INVISIBLE);
        binding.ll3.setVisibility(View.INVISIBLE);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting leaders...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

//        bottomLayout = findViewById(R.id.botton_layout);
        recyclerViewLeaders = findViewById(R.id.recycler_img_list);
        cardUser = findViewById(R.id.card_leader_1);

        // getting total numbers of leaders for leaderboard ranking
        dbRef = FirebaseDatabase.getInstance().getReference().child("LeaderBoard");
        dbRef.orderByChild("tPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalLeaders = (int) snapshot.getChildrenCount();
                listData = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    LeaderModalClass modalData = dataSnapshot.getValue(LeaderModalClass.class);
                    listData.add(modalData);
                }

                progressDialog.dismiss();
                setTopLeaders(listData);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




//         firebse se data LeaderModalClass me leke uspe sort lgana hoga then recyclerview me dalna hoga
        FirebaseRecyclerOptions<LeaderModalClass> options =
                new FirebaseRecyclerOptions.Builder<LeaderModalClass>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("LeaderBoard").orderByChild("tPoints"), LeaderModalClass.class)
                        .build();


        leaderAdapter = new LeaderAdapter(options);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Leaderboard.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerViewLeaders.setLayoutManager(layoutManager);
        recyclerViewLeaders.setAdapter(leaderAdapter);
//        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_anim);
//        recyclerViewLeaders.setLayoutAnimation(animation);
        leaderAdapter.startListening();


        //------------------------------   ---------------------------------- method 2 of usin recycler view with firebase

//        dbRef = FirebaseDatabase.getInstance().getReference().child("LeaderBoard");
//        listData = new ArrayList<>();
//        LinearLayoutManager layoutManager = new LinearLayoutManager(Leaderboard.this);
//        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);
//        recyclerViewLeaders.setLayoutManager(layoutManager);
//        recyclerViewLeaders.setHasFixedSize(true);
//
//        dbRef.orderByChild("tPoints").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    LeaderModalClass modalData = dataSnapshot.getValue(LeaderModalClass.class);
//                    listData.add(modalData);
//                }
//
//                adapter2 = new LeaderAdapter2(listData, Leaderboard.this);
//                recyclerViewLeaders.setAdapter(adapter2);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        //------------------------------   ---------------------------------- method 2 of usin recycler view with firebase


        //  bottom me ek linear layout me user ka rank set krne ke liye try kiye the nhi hua
//        setCurrentUserData(RANK);


    }

//    private void setCurrentUserData(int rank) {
//
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        binding.leaderUserRank.setText(rank+"");
//        Glide.with(this).load(user.getPhotoUrl()).into(binding.leaderUserImg);
//        binding.leaderUserName.setText(user.getDisplayName());
//        binding.leaderUserChip.setText((int) ResultActivity.USERPOINTS+"");
//    }

    private void setTopLeaders(List<LeaderModalClass> listData) {


        if (listData.size() >= 3) {

            binding.ll1.setVisibility(View.VISIBLE);
            binding.ll2.setVisibility(View.VISIBLE);
            binding.ll3.setVisibility(View.VISIBLE);

            Glide.with(getApplicationContext()).load(listData.get(totalLeaders - 3).getImgUrl()).into(binding.leader3Img);
            binding.leader3Name.setText(formatName(listData.get(totalLeaders - 3).getName()));

            Glide.with(getApplicationContext()).load(listData.get(totalLeaders - 2).getImgUrl()).into(binding.leader2Img);
            binding.leader2Name.setText(formatName(listData.get(totalLeaders - 2).getName()));

            Glide.with(getApplicationContext()).load(listData.get(totalLeaders - 1).getImgUrl()).into(binding.leader1Img);
            binding.leader1Name.setText(formatName(listData.get(totalLeaders - 1).getName()));
        }else{
            Toast.makeText(Leaderboard.this, "Not much users till now", Toast.LENGTH_SHORT).show();

            if (listData.size() == 1){
                binding.ll1.setVisibility(View.VISIBLE);

                Glide.with(getApplicationContext()).load(listData.get(totalLeaders - 1).getImgUrl()).into(binding.leader1Img);
                binding.leader1Name.setText(formatName(listData.get(totalLeaders - 1).getName()));
            }else if (listData.size() == 2){
                binding.ll1.setVisibility(View.VISIBLE);
                binding.ll2.setVisibility(View.VISIBLE);


                Glide.with(getApplicationContext()).load(listData.get(totalLeaders - 1).getImgUrl()).into(binding.leader1Img);
                binding.leader1Name.setText(formatName(listData.get(totalLeaders - 1).getName()));

                Glide.with(getApplicationContext()).load(listData.get(totalLeaders - 2).getImgUrl()).into(binding.leader2Img);
                binding.leader2Name.setText(formatName(listData.get(totalLeaders - 2).getName()));
            }

        }
        
        
    }

    private String formatName(String name) {
        if (name.contains(" ")){
            name = name.replace(" ", "\n");
        }

        return name;
    }


    public void showLeaderImgDialog(View view) {

        switch (view.getId()){
            case R.id.leader_1_img:
                    openDialog(listData.get(totalLeaders-1).getImgUrl(), listData.get(totalLeaders-1).getName());
                break;

            case R.id.leader_2_img:
                openDialog(listData.get(totalLeaders-2).getImgUrl(),listData.get(totalLeaders-2).getName() );
                break;

            case R.id.leader_3_img:
                openDialog(listData.get(totalLeaders-3).getImgUrl(),listData.get(totalLeaders-3).getName());
                break;
        }


    }

    public void openDialog(String url,String n){

        TextView name;

        AlertDialog.Builder builder = new AlertDialog.Builder(Leaderboard.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_leader_img_dialog, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        if (url != null) {
            Glide.with(getApplicationContext()).load(url).into((ImageView) dialogView.findViewById(R.id.img_leader));
        }
        name = dialogView.findViewById(R.id.name_leader);
        name.setText(n);
        alertDialog.show();
    }


}