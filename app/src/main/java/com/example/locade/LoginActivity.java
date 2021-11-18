package com.example.locade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextEmail, mEditTextPassword;
    private ProgressBar spinner;
    private Button mBtnLogin,  mBtnLinkToSignup;
    private Boolean loginBtnClicked;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtnClicked = false;
        spinner = findViewById(R.id.progress_bar_login);
        spinner.setVisibility(View.GONE);
        mEditTextEmail = findViewById(R.id.email_input_login);
        mEditTextPassword = findViewById(R.id.password_input_login);
        mBtnLinkToSignup = findViewById(R.id.signup_link_button);
        mBtnLogin = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtnClicked = true;
                spinner.setVisibility(View.VISIBLE);

                final String email = mEditTextEmail.getText().toString();
                final String password = mEditTextPassword.getText().toString();

                if(isEmpty(email) || isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    Intent goToHome = new Intent(LoginActivity.this, LocationsActivity.class);
                                    startActivity(goToHome);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                spinner.setVisibility(View.GONE);
            }
        });

        mBtnLinkToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignup = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(goToSignup);
                finish();
            }
        });


        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null && user.isEmailVerified() && !loginBtnClicked){
                    spinner.setVisibility(View.VISIBLE);
                    Intent goToHome = new Intent(LoginActivity.this, LocationsActivity.class);
                    startActivity(goToHome);
                    finish();
                    spinner.setVisibility(View.GONE);
                }
            }
        };
    }

    private Boolean isEmpty(String string){
         return string.trim().equals("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goToSignup = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(goToSignup);
        finish();
    }
}