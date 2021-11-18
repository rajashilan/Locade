package com.example.locade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private Button mBtnLinkToLogin, mBtnSignup;
    private ProgressBar spinner;
    private EditText fullName, nickName, email, number, password, confirmPassword;
    private DatePicker birthday;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private String emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+";
    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        spinner = findViewById(R.id.progress_bar_signup);
        spinner.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                spinner.setVisibility(View.VISIBLE);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null && user.isEmailVerified()){
                    Intent goToHome = new Intent(SignupActivity.this, LocationsActivity.class);
                    startActivity(goToHome);
                    finish();
                    spinner.setVisibility(View.GONE);
                    return;
                }
                spinner.setVisibility(View.GONE);
            }
        };


        mBtnLinkToLogin = findViewById(R.id.login_link_button);

        mBtnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLogin = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(goToLogin);
                finish();
            }
        });

        fullName = findViewById(R.id.full_name_input);
        nickName = findViewById(R.id.nick_name_input);
        email = findViewById(R.id.email_input);
        number = findViewById(R.id.number_input);
        password = findViewById(R.id.password_input);
        confirmPassword = findViewById(R.id.confirm_password_input);
        birthday = findViewById(R.id.birthday_input);
        mBtnSignup = findViewById(R.id.signup_button);

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);

                final String fullNameText = fullName.getText().toString();
                final String nickNameText = nickName.getText().toString();
                final String emailText = email.getText().toString();
                final String phoneText = number.getText().toString();
                final String passwordText = password.getText().toString();
                final String confirmPasswordText = confirmPassword.getText().toString();
                final String birthdayText = birthday.getDayOfMonth()+"/"+ (birthday.getMonth() + 1)+"/"+birthday.getYear();

                if(validateInputs(fullNameText, nickNameText, emailText, passwordText, confirmPasswordText, birthdayText)){
                    mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Log.e(TAG, "onComplete: ", task.getException() );
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SignupActivity.this, "Please check your email for verification.", Toast.LENGTH_SHORT).show();

                                            String userID = mAuth.getCurrentUser().getUid();
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                                    .child("Users").child(userID);

                                            Map userData = new HashMap<>();
                                            userData.put("fullName", fullNameText);
                                            userData.put("nickName", nickNameText);
                                            userData.put("birthday", birthdayText);
                                            userData.put("profileImgUrl", "default");
                                            userData.put("number", phoneText);
                                            databaseReference.updateChildren(userData);

                                            fullName.setText("");
                                            nickName.setText("");
                                            email.setText("");
                                            number.setText("");
                                            password.setText("");
                                            confirmPassword.setText("");
                                            Calendar cal = Calendar.getInstance();
                                            birthday.updateDate(cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH) - 1,cal.get(Calendar.YEAR));

                                            Intent goToLogin = new Intent(SignupActivity.this, LoginActivity.class);
                                            startActivity(goToLogin);
                                            finish();
                                        } else {
                                            Log.e(TAG, "onComplete2: ", task.getException() );
                                            Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                spinner.setVisibility(View.GONE);
            }
        });
    }

    private boolean validateInputs(String fullName, String nickName, String email, String password, String confirmPassword, String birthday){
        if(fullName.trim().equals("") || nickName.trim().equals("") || email.trim().equals("") || password.trim().equals("")){
            Toast.makeText(this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!email.matches(emailRegex)){
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
        Intent goToLogin = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(goToLogin);
        finish();
    }
}