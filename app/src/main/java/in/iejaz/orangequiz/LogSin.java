package in.iejaz.orangequiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;

//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class LogSin extends AppCompatActivity {

    private static final String TAG = "tag01";
    private static final int RC_SIGN_IN = 1101;
    TabLayout tabLayout;
    TabItem tabLogin, tabSignup;
    ViewPager viewPager;

    LottieAnimationView lottieGoogle;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private GoogleSignInClient mGoogleSignInClient;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logsin);

        setStatusBarColor();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Google Sign In...");

// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mAuth = FirebaseAuth.getInstance();

        lottieGoogle = findViewById(R.id.google_signin);
        lottieGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                Toast.makeText(LogSin.this, "Working on it", Toast.LENGTH_SHORT).show();
            }
        });

        tabLayout = findViewById(R.id.tablayout);
        tabLogin = findViewById(R.id.tab_login);
        tabSignup = findViewById(R.id.tab_signup);
        viewPager = findViewById(R.id.viewPager);
        final MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0 || tab.getPosition() == 1 ){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }

    private void setStatusBarColor() {
//         setting status bar collor
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.splashStatusBar));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null){
            Intent intent = new Intent(LogSin.this,MainActivity.class);
            intent.putExtra("loginViaEmail",false);
            startActivity(intent);

        }

    }


    private void firebaseAuthWithGoogle(String idToken) {
        final HashMap<String, Object> map = new HashMap<>();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    final FirebaseUser user = mAuth.getCurrentUser();
                    map.put("email",user.getEmail());
                    map.put("name",user.getDisplayName());
                    map.put("password","notApplicable");
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(user);
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            setAlerterMsg("Failed to create account", R.color.alert_default_error_background,R.drawable.sad);
                            Log.d(TAG, "firebaseAuthWithGoogle:" + task.getException());
                            updateUI(null);
                        }
                    });


                } else {
                    // If sign in fails, display a message to the user.
//                            Toast.makeText(LogSin.this, "E_02EA : "+task.getException(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "firebaseAuthWithGoogle:" + task.getException());
                    updateUI(null);
                }
            }
        });






    }

    public void setAlerterMsg(String msg ,  int color, int drawable){
        Alerter.create(LogSin.this)
                .setTitle("Orange Quiz")
                .setText(msg)
                .setIcon(drawable)
                .enableSwipeToDismiss()
                .setBackgroundColorRes(color) // or setBackgroundColorInt(Color.CYAN)
                .show();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LogSin.this, "E 2 : "+e.getCause(), Toast.LENGTH_SHORT).show();
            }
        }
    }




}