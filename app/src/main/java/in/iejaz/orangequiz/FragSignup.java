package in.iejaz.orangequiz;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FragSignup extends Fragment {

    EditText name;
    EditText pass;
    EditText email;
    Button signupBtn;

    FirebaseAuth auth;
    FirebaseUser user;

    ProgressBar progressBar;
    ProgressDialog progressDialog;

    String mName;
    String mPas;
    String mEmail;

    TextInputLayout passErrorLayout;
    TextInputLayout emailErrorLayout;
    TextInputLayout nameErrorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_signup, container, false);
        name = view.findViewById(R.id.signup_name);
        pass = view.findViewById(R.id.signup_pwd);
        email = view.findViewById(R.id.signup_email);
        signupBtn = view.findViewById(R.id.signup_btn);

        auth = FirebaseAuth.getInstance();

        progressBar = view.findViewById(R.id.progressBar3);
        progressDialog = new ProgressDialog(getContext());

        passErrorLayout = view.findViewById(R.id.signup_pwd_layout);
        nameErrorLayout = view.findViewById(R.id.signup_name_layout);
        emailErrorLayout = view.findViewById(R.id.signup_email_layout);


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mName = name.getText().toString();
                 mPas = pass.getText().toString();
                 mEmail = email.getText().toString();

                if (TextUtils.isEmpty(mName)) {
                    nameErrorLayout.setError("Cant be blank");
                    return;
                }
                if (TextUtils.isEmpty(mPas)) {
                    passErrorLayout.setError("Cant be blank");
                    return;
                }
                if (mPas.length() < 6){
                    passErrorLayout.setError("must be greater than 6");
                    return;
                }
                if (TextUtils.isEmpty(mEmail)) {
                    emailErrorLayout.setError("Email cant be blank");
                    return;
                }

                RegisterUser(mName, mPas, mEmail);

            }
        });

        return view;

    }

    public void RegisterUser(final String name, final String pass, final String email) {
//        progressBar.setVisibility(View.VISIBLE);
        progressDialog.setMessage("Just a sec...");
        progressDialog.show();

        final HashMap<String, Object> loginDetailsMap = new HashMap<>();

        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){                   // account created sucessfully
                    // we will store the additional fields in firebase database

//                    User user = new User(name,email,pass);      // you can do this using hashmap also   shown at line no 156 of LogSin class

                    loginDetailsMap.put("name", name);
                    loginDetailsMap.put("email", email);
                    loginDetailsMap.put("pass", pass);
                    loginDetailsMap.put("tPoints", "");
                    FirebaseDatabase.getInstance().getReference("Users")
//                                    .child("Users Database")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                            .setValue(user)
                            .setValue(loginDetailsMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()){

                                updateUserInfo();
                            }else{
                                // // registration failed
                                Toast.makeText(getContext(), "Failed please try again later", Toast.LENGTH_LONG).show();
                                Log.d("createAccountFailed",task.getException().getMessage());
                            }
                        }
                    });


                }else{
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });




    }

    private void updateUserInfo() {

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(mName).build();
        auth.getCurrentUser().updateProfile(profileChangeRequest);
        auth.signOut();
        Toast.makeText(getContext(), "You are now registered", Toast.LENGTH_SHORT).show();


    }
}


