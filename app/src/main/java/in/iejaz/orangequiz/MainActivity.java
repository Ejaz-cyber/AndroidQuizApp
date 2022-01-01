package in.iejaz.orangequiz;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView Name, Email, TotalScore;
    ImageView Image;
    myQuizCardAdapter adapter;
    RecyclerView recyclerView;

    private GoogleSignInClient mGoogleSignInClient;

    Toolbar toolbar;
    GoogleSignInAccount acct;

    ProgressDialog progressDialog;

    ArrayList<String> arr ;

    final static int REQUEST_CODE = 1121;
    Uri pickedImg;

    FirebaseUser firebaseUser;

    LinearLayout progBg;
    ProgressBar progressBar;

    String apkLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QuizActivity.wrongQIndices = null;
        QuizActivity.wrongAnsIndices = null;

        Email = findViewById(R.id.textView2);
        Name = findViewById(R.id.text_name);
        Image = findViewById(R.id.imageView);
        TotalScore = findViewById(R.id.total_score);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Orange Welcomes you");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting everything for you...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        progBg = findViewById(R.id.upload_img_prog_blur);
        progressBar = findViewById(R.id.upload_img_prog);
        progressBar.setVisibility(View.GONE);
        progBg.setVisibility(View.GONE);



        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22){
                    checkRequestPermission();
                }else{
                    chooseImage();

                }
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


//         to execute only once - to add score fields and setting all score to 0
//        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//        boolean firstStart = prefs.getBoolean("firstStart", true);
//
//        if (firstStart) {
//            setScoretoZero();
//        }



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(this);

        recyclerView = findViewById(R.id.recycler_view_quizcard);

        FirebaseRecyclerOptions<QuizCatoModel> options =
                new FirebaseRecyclerOptions.Builder<QuizCatoModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Quiz Categories"), QuizCatoModel.class)
                        .build();

        adapter = new myQuizCardAdapter(options);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.startListening();


        FillCard_method2();

        getApkLink();
    }

    private void checkRequestPermission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);
        }else{
            chooseImage();
        }
    }

    private void chooseImage() {
        startActivityForResult(new Intent().
                setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if (data != null){
                pickedImg = data.getData();
                Image.setImageURI(pickedImg);
                uploadImg();
            }
        }
    }

    // upload to storage
    public void uploadImg(){

        progressBar.setVisibility(View.VISIBLE);
        progBg.setVisibility(View.VISIBLE);

        final HashMap<String, Object> map = new HashMap<>();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference sRef = FirebaseStorage.getInstance().getReference().child(currentUser.getUid()+" : "+currentUser.getDisplayName());
        final StorageReference imgFilePath = sRef.child(pickedImg.getLastPathSegment());
        imgFilePath.putFile(pickedImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imgFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    map.put("imgUrl", firebaseUser.getPhotoUrl().toString());
                                    FirebaseDatabase.getInstance().getReference("LeaderBoard").child(currentUser.getUid()).updateChildren(map);

                                    progressBar.setVisibility(View.GONE);
                                    progBg.setVisibility(View.GONE);
                                    setAlerterMsg("Profile photo uploaded", R.color.alerter_default_success_background, R.drawable.alerter_ic_face);

                                }else{
                                    setAlerterMsg("Failed to upload...\nPlease try again...", R.color.alert_default_error_background, R.drawable.sad);

                                }
                            }
                        });

                    }
                });

            }
        });
    }

    public void setAlerterMsg(String msg ,  int color, int drawable){
        Alerter.create(MainActivity.this)
                .setTitle("Orange Quiz")
                .setText(msg)
                .setIcon(drawable)
                .enableSwipeToDismiss()
                .setBackgroundColorRes(color) // or setBackgroundColorInt(Color.CYAN)
                .show();
    }

    // not needed now
//    private void setScoretoZero(){
//        arr = new ArrayList<>();
//        DatabaseReference dbRef2;
//
//        // getting every quiz titles
//        final HashMap<String,Object> map2 = new HashMap<>();
//        dbRef2 = FirebaseDatabase.getInstance().getReference().child("Quiz Categories");
//        dbRef2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
////                    mq = dataSnapshot.getValue(modalQuestion.class);
////                    questionList.add(mq);
////                    Log.i("fire","cato = "+dataSnapshot.getValue().toString());
//                    arr.add(dataSnapshot.child("Title").getValue().toString());
//                }
//
//                for (int  i = 0; i< arr.size(); i++){
//                    map2.put(arr.get(i), String.valueOf(0));
//                    Log.i("fireTag", arr.get(i));
//                }
//
//                FirebaseDatabase.getInstance().getReference("Users")
//                        //                                    .child("Users Database")
//                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Score")
//                        .updateChildren(map2);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean("firstStart", false);
//        editor.apply();
//    }






    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);

        // to create menu dynamically
//        MenuItem a = menu.add(0,1,0,"menu1");
//        a.setIcon(R.drawable.ic_round_leaderboard_24);
//        a.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
//
//        MenuItem b = menu.add(0,2,1,"menu2");
//        b.setIcon(R.drawable.ic_launcher_foreground);
//        b.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.crash_report:
                startActivity(new Intent(MainActivity.this, CrashActivity.class));
                return true;


            case R.id.signout:
                signout();
                return true;

            case R.id.shareApp:
                shareApp();
                return true;

            case R.id.leaders:
                // open new Activity to show all leaders with points and there profile pic & name
                startActivity(new Intent(MainActivity.this,Leaderboard.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void shareApp() {

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey there,"+"\n"+"checkout this great quiz app made by one of MY FRIEND"+"\n\n"+apkLink);

        // (Optional) Here we're setting the title of the content
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Share this app using...");

        // Show the Sharesheet
        startActivity(Intent.createChooser(sendIntent, null));

    }

    public void getApkLink() {

        DatabaseReference dbReef = FirebaseDatabase.getInstance().getReference().child("Useful Links");
        dbReef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                apkLink = snapshot.child("appLink").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


//    public void FillCard_method1(){     // through email and password
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            // User is signed in
//            // Name, email address, and profile photo Url
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
//
//            Log.i("tam", String.valueOf(user.getPhotoUrl()));
//            // Check if user's email is verified
//            boolean emailVerified = user.isEmailVerified();
//            // The user's ID, unique to the Firebase project. Do NOT use this value to
//            // authenticate with your backend server, if you have one. Use
//            // FirebaseUser.getIdToken() instead.
//            String uid = user.getUid();
//
//            // Id of the provider (ex: google.com)
////            String providerId = user.getProviderId();
//            String providerId = String.valueOf(user.getProviderData());
//
//            Toast.makeText(MainActivity.this,"photo = "+user.getPhotoUrl(),Toast.LENGTH_LONG).show();
//
//            Name.setText(name);
//            Email.setText(email);
//            Glide.with(this)
//                    .load(photoUrl)
//                    .into(Image);
//
//        } else {
//            // No user is signed in
//            signout();
//        }
//    }


    public void FillCard_method2(){
        if(firebaseUser != null) {

            String uid = Objects.requireNonNull(firebaseUser).getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Email.setText(snapshot.child("email").getValue().toString());
                    Name.setText(snapshot.child("name").getValue().toString());
                    if (snapshot.child("tPoints").getValue() != null) {
                        TotalScore.setText(snapshot.child("tPoints").getValue().toString());
                    }else{
                        TotalScore.setText("--");
                    }
                    if (firebaseUser.getPhotoUrl() != null) {
                        Glide.with(getApplicationContext())
                                .load(firebaseUser.getPhotoUrl())
                                .into(Image);
                    }else{
                        Image.setImageResource(R.drawable.logo_final_no_dp);
                    }


                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }else{
            signout();
        }
    }


    private void signout() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                // ...
                FirebaseAuth.getInstance().signOut();
                finish();
                Toast.makeText(MainActivity.this, "--- LOGGED OUT ---", Toast.LENGTH_SHORT).show();
            }
        });
    }

}