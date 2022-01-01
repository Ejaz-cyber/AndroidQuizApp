package in.iejaz.orangequiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tapadoo.alerter.Alerter;

public class FragLogin extends Fragment {

    EditText login_name;
    EditText login_pwd;

    Boolean rememberMe = false;
    CheckBox CB;
    String email;
    String pwd;

    ProgressBar progressBar;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    ProgressDialog progressDialog;

    private FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_login, container, false);

        auth = FirebaseAuth.getInstance();

        Fragment fragment_signup = new FragSignup();
        login_name = view.findViewById(R.id.login_name);
        login_pwd = view.findViewById(R.id.login_pwd);

//        login_name.setText("dssd");
        Button btnLogin = view.findViewById(R.id.login_btn);
        CB = view.findViewById(R.id.checkBox);

        preferences = getActivity().getSharedPreferences("in.iejaz.orangequiz", Context.MODE_PRIVATE);
        editor = preferences.edit();

        progressBar = view.findViewById(R.id.progress_circular);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Logging in...");

        applyLoginData();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = login_name.getText().toString();
                pwd = login_pwd.getText().toString();

                if (TextUtils.isEmpty(email)){
                    login_name.setError("Username cant be empty");
                    login_name.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(pwd)){
                    login_pwd.setError("Password cant be empty");
                    login_pwd.requestFocus();
                    return;
                }

                if (CB.isChecked()) {
                    rememberMe = true;
                    editor.putString("name", email);
                    editor.putString("pwd", pwd);
                    editor.apply();
                    editor.commit();
                    Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                }
                // make login here
//                progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();
                makeLogin(email,pwd);




            }
        });

        return view;

    }

    public void makeLogin(String email,String pass){
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("loginViaEmail",true);
                    startActivity(intent);
                }else {
                    setAlerterMsg("Login Failed",""+task.getException().getMessage(), R.color.lightRed);
                }
            }
        });

    }


    public void applyLoginData(){
            SharedPreferences preferences = getActivity().getSharedPreferences("in.iejaz.orangequiz",Context.MODE_PRIVATE);

            if (preferences != null) {
                login_name.setText(preferences.getString("name", ""));
                login_pwd.setText(preferences.getString("pwd", ""));
                CB.setChecked(true);
            }
    }

    public void setAlerterMsg(String title, String msg ,  int color){
        Alerter.create((Activity) getContext())
                .setTitle(title)
                .setText(msg)
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .enableSwipeToDismiss()
                .setBackgroundColorRes(color) // or setBackgroundColorInt(Color.CYAN)
                .show();
    }
}
