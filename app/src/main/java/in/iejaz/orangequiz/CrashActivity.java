package in.iejaz.orangequiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Objects;

public class CrashActivity extends AppCompatActivity implements devBottomSheet.devSheetListener{

    LinearLayout moreDetailsLayout;
    Toolbar toolbar;
    RadioGroup radioGroup;
    Button btnReport;
    EditText editTextCrash;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String crashHeading;
    String crashDetails;

    HashMap<String, Object> crashMap;
    private static final int devMenuId = 121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Report App Crash");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setStatusBarColor();

        moreDetailsLayout = findViewById(R.id.more_details_crash);
        radioGroup = findViewById(R.id.radioGroup);
        btnReport = findViewById(R.id.btn_report);
        editTextCrash = findViewById(R.id.editTextTextPersonName);

        moreDetailsLayout.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        radioGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreDetailsLayout.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(CrashActivity.this, R.anim.woople_anim);
                moreDetailsLayout.setAnimation(animation);

            }
        });

        crashMap = new HashMap<>();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioButton:
                        crashHeading = "Stopped Suddenly";
                        break;
                    case R.id.radioButton2:
                        crashHeading = "Not working properly";

                        break;
                    case R.id.radioButton3:
                        crashHeading = "Not getting question";

                        break;
                    case R.id.radioButton4:
                        crashHeading = "taking so much time";
                        break;
                }

                crashMap.put("heading", crashHeading);
                moreDetailsLayout.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(CrashActivity.this, R.anim.slide_btm);
                moreDetailsLayout.setAnimation(animation);

            }
        });


        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crashDetails = editTextCrash.getText().toString();
                if (TextUtils.isEmpty(crashDetails)){
                    Toast.makeText(CrashActivity.this, "Details would be more helpful though :)", Toast.LENGTH_SHORT).show();
                }else{
                    crashMap.put("details", crashDetails);
                }
                uploadCrashDetails();
            }
        });




    }

    private void uploadCrashDetails() {
        FirebaseDatabase.getInstance().getReference().child("Crash Report").child(mUser.getUid()+" name =  "+mUser.getDisplayName())
                .updateChildren(crashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    setAlerterMsg("Thank you","I will do my best to resolve this ASAP", R.color.lightGreen, R.drawable.alerter_ic_face);
                }else{
                    setAlerterMsg("Error sending crash report","please check your internet", R.color.lightRed, R.drawable.ic_baseline_error_outline_24);

                }
            }
        });
    }

    private void setAlerterMsg(String title,String msg ,  int color, int drawable){
        Alerter.create(CrashActivity.this)
                .setTitle(title)
                .setText(msg)
                .setIcon(drawable)
                .enableSwipeToDismiss()
                .setBackgroundColorRes(color) // or setBackgroundColorInt(Color.CYAN)
                .show();
    }

    private void setStatusBarColor() {
//         setting status bar collor
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.crashPage));
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
        Uri instaUri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, instaUri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem a = menu.add(0,devMenuId,0,"Developer's Profile"); // position, id, 0, title
        a.setIcon(R.drawable.about_dev_img);
        a.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case devMenuId:
                devBottomSheet devBottomSheet = new devBottomSheet();
                devBottomSheet.show(getSupportFragmentManager(), "devBottomSheet");
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}