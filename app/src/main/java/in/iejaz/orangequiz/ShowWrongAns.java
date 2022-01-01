package in.iejaz.orangequiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class ShowWrongAns extends AppCompatActivity {

    private static final String TAG = "t1";
    TextView qNoOutOf;

    TextView questionTxt;
    ImageView questionImg;
    TextView answerTxt1;
    TextView answerTxt2;
    TextView answerTxt3;
    TextView answerTxt4;
    TextView qTitle;
    Button btnNext;

    ArrayList<modalQuestion> totalQuzstionList = null;
    int wrongListSize;
    int index = 0;
    ArrayList<Integer> selectedAnsIndices;
    modalQuestion MQ;


    MaterialCardView cardOption1;
    MaterialCardView cardOption2;
    MaterialCardView cardOption3;
    MaterialCardView cardOption4;

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        qNoOutOf = findViewById(R.id.q_no_outof);
        qNoOutOf.setVisibility(View.GONE);

        questionTxt = findViewById(R.id.question_txt);
        questionImg = findViewById(R.id.question_img);
        answerTxt1 = findViewById(R.id.answer_txt_1);
        answerTxt2 = findViewById(R.id.answer_txt_2);
        answerTxt3 = findViewById(R.id.answer_txt_3);
        answerTxt4 = findViewById(R.id.answer_txt_4);
        btnNext = findViewById(R.id.btn_next);
        qTitle = findViewById(R.id.q_title);
        qTitle.setText(getIntent().getStringExtra("QUIZ_TITLE"));

        layout = findViewById(R.id.quiz_layout_cards);

        cardOption1 = findViewById(R.id.cardoption1);
        cardOption2 = findViewById(R.id.cardoption2);
        cardOption3 = findViewById(R.id.cardoption3);
        cardOption4 = findViewById(R.id.cardoption4);

        makeCardNotClickable();

        selectedAnsIndices = QuizActivity.wrongAnsIndices;
        totalQuzstionList = QuizActivity.questionList;
        wrongListSize = QuizActivity.wrongQIndices.size();

        setData(getQIndex());
        setSelectedCard(getQIndex(), selectedAnsIndices);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                if (index < wrongListSize) {
                    resetCardColor();
                    setData(getQIndex());
                    setSelectedCard(getQIndex(), selectedAnsIndices);

                } else {
                    Toast.makeText(ShowWrongAns.this, "Done", Toast.LENGTH_SHORT).show();
                    QuizActivity.wrongQIndices = null;
                    QuizActivity.wrongAnsIndices = null;
                    startActivity(new Intent(ShowWrongAns.this, MainActivity.class));

                }


            }
        });

    }

    private int getQIndex() {

        int i = QuizActivity.wrongQIndices.get(index);
        return i;
    }

    private void setSelectedCard(int qIndex, ArrayList<Integer> selectedAnsIndices) {
        if (index < selectedAnsIndices.size()) {
            switch (selectedAnsIndices.get(index)) {
                case 1:
                    cardOption1.setCardBackgroundColor(getResources().getColor(R.color.btn1_light));
                    break;
                case 2:
                    cardOption2.setCardBackgroundColor(getResources().getColor(R.color.btn1_light));
                    break;
                case 3:
                    cardOption3.setCardBackgroundColor(getResources().getColor(R.color.btn1_light));
                    break;
                case 4:
                    cardOption4.setCardBackgroundColor(getResources().getColor(R.color.btn1_light));
                    break;
            }
        } else {
            Toast.makeText(ShowWrongAns.this, "done", Toast.LENGTH_SHORT).show();
        }

    }

    public void resetCardColor() {
        cardOption1.setCardBackgroundColor(getResources().getColor(R.color.ansCardDefault));
        cardOption2.setCardBackgroundColor(getResources().getColor(R.color.ansCardDefault));
        cardOption3.setCardBackgroundColor(getResources().getColor(R.color.ansCardDefault));
        cardOption4.setCardBackgroundColor(getResources().getColor(R.color.ansCardDefault));

    }

    private void setData(int qIndex) {

        MQ = totalQuzstionList.get(qIndex);
        questionTxt.setText(MQ.getQuestion());
        answerTxt1.setText(MQ.getoA());
        answerTxt2.setText(MQ.getoB());
        answerTxt3.setText(MQ.getoC());
        answerTxt4.setText(MQ.getoD());

        if (MQ.hasImg != null) {
            if (MQ.hasImg.equals("yes")) {
                questionImg.setVisibility(View.VISIBLE);
                Glide.with(this).load(MQ.qImgUrl).into(questionImg);
            } else {
                questionImg.setVisibility(View.GONE);
            }
        } else {
            questionImg.setVisibility(View.GONE);
        }

        if (MQ.getoAns().equals(MQ.getoA())) {
            cardOption1.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));
        } else if (MQ.getoAns().equals(MQ.getoB())) {
            cardOption2.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));

        } else if (MQ.getoAns().equals(MQ.getoC())) {
            cardOption3.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));

        } else if (MQ.getoAns().equals(MQ.getoD())) {
            cardOption4.setCardBackgroundColor(getResources().getColor(R.color.lightGreen));

        }


    }

    public void makeCardNotClickable() {
        cardOption1.setClickable(false);
        cardOption2.setClickable(false);
        cardOption3.setClickable(false);
        cardOption4.setClickable(false);
    }


}
