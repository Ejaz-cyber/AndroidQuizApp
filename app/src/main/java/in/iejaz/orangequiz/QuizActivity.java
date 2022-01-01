package in.iejaz.orangequiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    String QUIZ_TITLE;
    String imgLogoUrl;

    Toolbar toolbar2;

    CountDownTimer countDownTimer;
    private static final long timeInMillis = 20000;     // 20 seconds in milliseconds
    private static final int progressbarMax = 20;
    private static final int time = 20;
    RoundCornerProgressBar progressBarHorizontal;
//    ProgressBar progressBar;


    TextView questionTxt;
    ImageView questionImg;
    TextView answerTxt1;
    TextView answerTxt2;
    TextView answerTxt3;
    TextView answerTxt4;
    TextView qNoOutof;
    TextView qTitle;
    Button btnNext;

    MaterialCardView cardOption1;
    MaterialCardView cardOption2;
    MaterialCardView cardOption3;
    MaterialCardView cardOption4;

    CardView cardA;
    CardView cardB;
    CardView cardC;
    CardView cardD;

    DatabaseReference dbRef;
    static ArrayList<modalQuestion> questionList;
//    static ArrayList<modalQuestion> wrongAttemptedList;
    static ArrayList<Integer> wrongAnsIndices;
    static ArrayList<Integer> wrongQIndices;
    ProgressDialog progressDialog;

    int qIndex = 0;
    int moveTill;

    int selectedOptionCard;

    int rightAnsCount = 0;
    int wrongAnsCount = 0;
    int questionSkipped = 0;
    int noOfQues = 0;
    String selectedAns = null;


    modalQuestion mq;
//    modalQuestion mq2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Just a sec");
        progressDialog.setMessage("Getting questions...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        QUIZ_TITLE = getIntent().getStringExtra("QUIZ_TITLE");
        imgLogoUrl = getIntent().getStringExtra("imgLogoUrl");

        progressBarHorizontal = findViewById(R.id.timer_pbar);
        progressBarHorizontal.setMax(progressbarMax);
        progressBarHorizontal.enableAnimation();
        progressBarHorizontal.setRadius(5);

        questionTxt = findViewById(R.id.question_txt);
        questionImg = findViewById(R.id.question_img);
        answerTxt1 = findViewById(R.id.answer_txt_1);
        answerTxt2 = findViewById(R.id.answer_txt_2);
        answerTxt3 = findViewById(R.id.answer_txt_3);
        answerTxt4 = findViewById(R.id.answer_txt_4);
        btnNext = findViewById(R.id.btn_next);

        cardOption1 = findViewById(R.id.cardoption1);
        cardOption2 = findViewById(R.id.cardoption2);
        cardOption3 = findViewById(R.id.cardoption3);
        cardOption4 = findViewById(R.id.cardoption4);

        cardA = findViewById(R.id.cardA);
        cardB = findViewById(R.id.cardB);
        cardC = findViewById(R.id.cardC);
        cardD = findViewById(R.id.cardD);

        qNoOutof = findViewById(R.id.q_no_outof);
        qTitle = findViewById(R.id.q_title);

        qTitle.setText(QUIZ_TITLE);

        wrongAnsIndices = new ArrayList<>();
        wrongQIndices = new ArrayList<>();
        makeCardClickable();

        // loading all questions----------------------------------------------------------------------------

    questionList = new ArrayList<>();

    dbRef = FirebaseDatabase.getInstance().getReference().child("Quiz Questions").child(QUIZ_TITLE);
    dbRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                mq = dataSnapshot.getValue(modalQuestion.class);
                questionList.add(mq);
            }
            moveTill = questionList.size();
            progressDialog.dismiss();
            noOfQues = questionList.size();
            Collections.shuffle(questionList);
            // to make list of questions random
            if (questionList.size() == 0) {
                makeDialog("noQuestionAlert");
            } else {
                setData(qIndex, qIndex, noOfQues);
                startTimer(time);

                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // next question
//                            checkAnswer(getSelectedAns(), mq.getoAns());

                        checkAnswer(mq.getoAns(), qIndex);
                        selectedAns = "notSelected";

                        qIndex++;


                        if (qIndex < moveTill) {
                            animateLayout();
                            setData(qIndex, qIndex, noOfQues);
                            resetCardColor();
                            // resetting timer progress
                            countDownTimer.cancel();
                            startTimer(time);

                        } else {
                            countDownTimer.cancel();

                            Intent gotoResult = new Intent(QuizActivity.this, ResultActivity.class);
                            gotoResult.putExtra("qSkiped", questionSkipped);
                            gotoResult.putExtra("qRight", rightAnsCount);
                            gotoResult.putExtra("qWrong", wrongAnsCount);
                            gotoResult.putExtra("noOfQues", noOfQues);
                            gotoResult.putExtra("QUIZ_TITLE", QUIZ_TITLE);
                            startActivity(gotoResult);


                        }
                    }
                });

            }
        }


        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            progressDialog.dismiss();
        }
    });

}

    private void animateLayout() {

        LinearLayout linearLayout = findViewById(R.id.quiz_layout_cards);
//        linearLayout.animate().scaleX(50).setDuration(500).setStartDelay(300).setInterpolator(new DecelerateInterpolator());

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.woople_anim);
        linearLayout.setAnimation(animation);



        Animation animation2 = AnimationUtils.loadAnimation(this,R.anim.roll);
        cardA.setAnimation(animation2);
        cardB.setAnimation(animation2);
        cardC.setAnimation(animation2);
        cardD.setAnimation(animation2);


    }

    public void startTimer(int time) {

        final int[] t = {time};

        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                t[0]--;
                progressBarHorizontal.setProgress(t[0]);
            }

            @Override
            public void onFinish() {
                // show dialog - oops times up
                makeDialog("timeupGoBack");
            }
        }.start();


    }

    public void makeDialog(String dialogForWhat) {

        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);

        if (dialogForWhat.equals("timeupGoBack")) {

            View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.timeout_dialog, viewGroup, false);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            dialogView.findViewById(R.id.btn_timeup_goback).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(QuizActivity.this, "ok", Toast.LENGTH_SHORT).show();
                    alertDialog.cancel();
                    startActivity(new Intent(QuizActivity.this, MainActivity.class));
                }
            });
            alertDialog.show();

        } else if (dialogForWhat.equals("noQuestionAlert")) {

            View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.no_question_dialog, viewGroup, false);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            dialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(QuizActivity.this, "thank you", Toast.LENGTH_SHORT).show();
                    alertDialog.cancel();
                    startActivity(new Intent(QuizActivity.this, MainActivity.class));
                }
            });
            alertDialog.show();

        }


    }

    private void setData(int qNo, int qIndex, int noOfQues ) {

        qNoOutof.setText(new StringBuilder().append(qIndex+1).append("/").append(noOfQues).toString());

        mq = questionList.get(qNo);
        questionTxt.setText(mq.getQuestion());
        answerTxt1.setText(mq.getoA());
        answerTxt2.setText(mq.getoB());
        answerTxt3.setText(mq.getoC());
        answerTxt4.setText(mq.getoD());

        if (mq.hasImg != null) {
            if (mq.hasImg.equals("yes")) {
                questionImg.setVisibility(View.VISIBLE);
                Glide.with(this).load(mq.qImgUrl).into(questionImg);
            } else {
                questionImg.setVisibility(View.GONE);
            }
        } else {
            questionImg.setVisibility(View.GONE);
        }
    }

    public void resetCardColor() {
        cardOption1.setCardBackgroundColor(getResources().getColor(R.color.ansCardDefault));
        cardOption2.setCardBackgroundColor(getResources().getColor(R.color.ansCardDefault));
        cardOption3.setCardBackgroundColor(getResources().getColor(R.color.ansCardDefault));
        cardOption4.setCardBackgroundColor(getResources().getColor(R.color.ansCardDefault));

// previous clicked wala card k wapas lana hai
    }

    public void cardOptionClick(View view) {

        switch (view.getId()) {
            case R.id.cardoption1:
                resetCardColor();
                selectedAns = mq.oA;
                setSelectedCardColor(cardOption1);
                selectedOptionCard = 1;
                break;

            case R.id.cardoption2:
                resetCardColor();
                selectedAns = mq.oB;
                setSelectedCardColor(cardOption2);
                selectedOptionCard = 2;
                break;

            case R.id.cardoption3:
                resetCardColor();
                selectedAns = mq.oC;
                setSelectedCardColor(cardOption3);
                selectedOptionCard = 3;
                break;

            case R.id.cardoption4:
                resetCardColor();
                selectedAns = mq.oD;
                setSelectedCardColor(cardOption4);
                selectedOptionCard = 4;
                break;

            default:
                selectedAns = "notSelected";
                break;
        }


    }

    public void setSelectedCardColor(CardView cardMain) {
        cardMain.setCardBackgroundColor(getResources().getColor(R.color.btn1_light));
        cardMain.setCardElevation(0);


    }

    public void makeCardClickable() {
        cardOption1.setClickable(true);
        cardOption2.setClickable(true);
        cardOption3.setClickable(true);
        cardOption4.setClickable(true);
    }

    public void checkAnswer(String actualAns, int qIndex) {
//        Toast.makeText(QuizActivity.this, "selected = "+selectedAns+"\nactual = "+actualAns, Toast.LENGTH_LONG).show();

        if (selectedAns == null || selectedAns.equals("notSelected")) {
            questionSkipped++;
            return;
        }

        if (selectedAns.equals(actualAns)) {
//            Toast.makeText(QuizActivity.this, "good", Toast.LENGTH_SHORT).show();
            rightAnsCount++;
        } else {
            wrongAnsCount++;
//            Toast.makeText(QuizActivity.this, "bad", Toast.LENGTH_SHORT).show();
            wrongAnsIndices.add(selectedOptionCard);
            wrongQIndices.add(qIndex);
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cancel_quiz_dialog, viewGroup, false);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        dialogView.findViewById(R.id.btn_yes_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QuizActivity.this, "Test Canceled", Toast.LENGTH_SHORT).show();
                alertDialog.cancel();
//                QuizActivity.super.onBackPressed();
                startActivity(new Intent(QuizActivity.this, MainActivity.class));
            }
        });
        dialogView.findViewById(R.id.btn_no_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });
        alertDialog.show();

    }
}