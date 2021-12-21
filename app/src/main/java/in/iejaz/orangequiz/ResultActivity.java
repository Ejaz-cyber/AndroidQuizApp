package in.iejaz.orangequiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.tapadoo.alerter.Alerter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ResultActivity extends AppCompatActivity implements devBottomSheet.devSheetListener , ReviewDialog.myReviewDialogListener{

    int qWrong;
    int qRight;
    int qSkiped;
    int noOfQues;
    String qTitle;

    long pMain;
    long pAttempted;

    Toolbar toolbar;

    CircularProgressBar progressBar;
    TextView percentageTxt;
    TextView attempted;
    TextView totalQues;
    TextView rightAns;
    TextView wrongAns;

    CardView cardDev;
    CardView cardShare;
    CardView cardPlayOther;
    CardView cardLeaderBoard;
    CardView cardReview;
    LottieAnimationView lottieAnimationView;
    Handler mHandler;

    DatabaseReference dbRef;
    FirebaseUser mUser;
    long totalScore = 0;

    HashMap<String , Object> myMap;

    static String USERNAME = null;
    static long USERPOINTS;

    // if no images found then this image will be used
    private static final String NO_IMG_URL = "https://firebasestorage.googleapis.com/v0/b/orange-quiz-32e35.appspot.com/o/noImglogo%2Flogo_final_no_dp.jpg?alt=media&token=d679e49c-e197-43b5-8093-f24e52ab1582";

    String apkLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        qWrong = getIntent().getIntExtra("qWrong", -1);
        qRight = getIntent().getIntExtra("qRight", -1);
        qSkiped = getIntent().getIntExtra("qSkiped", -1);
        noOfQues = getIntent().getIntExtra("noOfQues", -1);
        qTitle = getIntent().getStringExtra("quizTitle");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Score Board");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.circularProgressBar);
        percentageTxt = findViewById(R.id.progress_text_percent);
        attempted = findViewById(R.id.attempted);
        totalQues = findViewById(R.id.total_ques);
        rightAns = findViewById(R.id.right_ans);
        wrongAns = findViewById(R.id.wrong_ans);

        cardDev = findViewById(R.id.card_dev);
        cardLeaderBoard = findViewById(R.id.card_leaderboard);
        cardPlayOther = findViewById(R.id.card_play_again);
        cardReview = findViewById(R.id.card_review);
        cardShare = findViewById(R.id.card_share);
        lottieAnimationView = findViewById(R.id.lottie_animationView);


        getAPKLink();

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        pMain = (long) (((float)qRight/ (float) noOfQues) * 100);
        animateProgress((int) pMain);

        HashMap<String, Object> map = new HashMap<>();
//        map.put("Total score",)
        map.put(qTitle,df.format(pMain));

        // to upload last quiz's result
        dbRef.child(mUser.getUid()).child("Score").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

               setAlerterMsg("Score Uploaded","", R.color.alerter_default_success_background,R.drawable.ic_baseline_cloud_done_24);

            }

        });


        // dhukke sara categories ka score fetch krna hai aur update krna hai final score ko score array ke andar firebase ka
        final ArrayList<Long> arrScore = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child("Score")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrScore.add(Long.parseLong(dataSnapshot.getValue().toString()));
                }
                for (int i =  0; i< arrScore.size(); i++){
                    totalScore = totalScore + arrScore.get(i);

                }
                updateTotalScore(totalScore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        percentageTxt.setText(df.format(pMain) + "%");
        percentageTxt.setVisibility(View.INVISIBLE);

        pAttempted = ((long) (qRight + qWrong) * 100) / (long) noOfQues;
        attempted.setText(df.format(pAttempted) + "%");

        totalQues.setText(noOfQues + "");

        cardDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devBottomSheet devBottomSheet = new devBottomSheet();
                devBottomSheet.show(getSupportFragmentManager(), "devBottomSheet");
                
            }
        });

        cardShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey there,"+"\n"+"checkout this great quiz app made by one of MY FRIEND"+"\n\n"+apkLink);

                // (Optional) Here we're setting the title of the content
                sendIntent.putExtra(Intent.EXTRA_TITLE, "Share your score by...");

                // Show the Sharesheet
                startActivity(Intent.createChooser(sendIntent, null));
            }
        });

        cardReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ReviewDialog reviewDialog = new ReviewDialog();
            reviewDialog.show(getSupportFragmentManager(),"Review Dialog");

            }
        });

        cardPlayOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, MainActivity.class));
            }
        });

        cardLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, Leaderboard.class));
            }
        });

    }

    public void getAPKLink() {

        final DatabaseReference[] dbReef = {FirebaseDatabase.getInstance().getReference().child("Useful Links")};
        dbReef[0].addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                apkLink = snapshot.child("appLink").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setAlerterMsg(String title,String msg ,  int color, int drawable){
        Alerter.create(ResultActivity.this)
                .setTitle(title)
                .setText(msg)
                .setIcon(drawable)
                .enableSwipeToDismiss()
                .setBackgroundColorRes(color) // or setBackgroundColorInt(Color.CYAN)
                .show();
    }

    public void updateTotalScore(float totalScore){
    // updating total score - sum of all quiz's score
    HashMap<String, Object> map2 = new HashMap<>();
    map2.put("tPoints",totalScore+"pts");

    dbRef.child(mUser.getUid()).updateChildren(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            setAlerterMsg("Failed to update score","Check your internet connection", R.color.alert_default_error_background, R.drawable.sad);
        }
    });

    // getting info to update leaderboard
    // setting data to firebase leaderboard

//        display name set kr diye hai
//         yaha pr ab sirf getDisplayName() krke hashmap pe set krna hoga

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        USERNAME = firebaseUser.getDisplayName();
        USERPOINTS = (long) totalScore;
        myMap = new HashMap<>();
        myMap.put("name", USERNAME);
        if(firebaseUser.getPhotoUrl() != null) {
            myMap.put("imgUrl", firebaseUser.getPhotoUrl().toString());
        }else{
            myMap.put("imgUrl", NO_IMG_URL);
        }
        myMap.put("tPoints", totalScore);
        FirebaseDatabase.getInstance().getReference("LeaderBoard").child(mUser.getUid()).updateChildren(myMap);


    }



    public void animateProgress(final int p) {

//        final int[] currentProgressCount = {0};
        mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= p; i++) {
//                   currentProgressCount[0] = i;
                    final int currentProgressCount = i;
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Update the value background thread to UI thread
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(currentProgressCount);

                            if (currentProgressCount == p) {
                                setFun(p);

                            }

                        }
                    });
                }
            }

        }).start();
    }


    public void setFun(int p) {
        percentageTxt.setVisibility(View.VISIBLE);
        rightAns.setText(qRight + "");
        wrongAns.setText(qWrong + "");
        if (p > 50) {
            lottieAnimationView.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(ResultActivity.this, MainActivity.class));
    }

    @Override
    public void onButtonClicked(String text) {

        String githubLink = "https://github.com/Ejaz-cyber";
        String fbLink = "https://www.facebook.com/ejaz.mahmood.505";
        String instaLink = "https://www.instagram.com/invites/contact/?i=16nobqrqwqyvr&utm_content=i0vgt0w";

        switch (text){
            case "github":
                devSocialLinks(githubLink);

                break;
            case "insta":
                devSocialLinks(instaLink);

                break;
            case "fb":
                devSocialLinks(fbLink);

                break;
        }

    }

    private void devSocialLinks(String link) {
//        String appLink = "www.google.com";
//        Intent sendIntent = new Intent(Intent.ACTION_SEND);
//        sendIntent.setType("text/plain");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
//
//        // (Optional) Here we're setting the title of the content
//        sendIntent.putExtra(Intent.EXTRA_TITLE, "Connect with Ejaz");
//
//        // Show the Sharesheet
//        startActivity(Intent.createChooser(sendIntent, null));


        Uri instaUri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, instaUri);
        startActivity(intent);
    }


    @Override
    public void applyReview(float rating, String review) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("rating", String.valueOf(rating));
        map.put("review", review);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference().child("Reviews").child(firebaseUser.getUid()+" "+firebaseUser.getDisplayName()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    setAlerterMsg("Thank you","You can also update this review later", R.color.lightGreen, R.drawable.alerter_ic_face);
                }else{
                    setAlerterMsg("Oops, Review not uploaded","Please check your network connection", R.color.lightRed, R.drawable.sad);

                }
            }
        });

    }
}