package com.example.user.shopsap;

import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by user on 20.8.2017.
 */

public class SignupMarket extends AppCompatActivity {

    private EditText inputName,inputEmail, inputPassword1,inputPassword2;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private Firebase mRef;
    private DatabaseReference mDatabase;
    public String signUpUserName;
    private TextView txt;
    private final String shop_pass="123455";
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static String email2x;
    private final int usertypex=2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_signup_market);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();



        btnSignIn = (Button) findViewById(R.id.signup_sign_in_button_market);
        btnSignUp = (Button) findViewById(R.id.signup_sign_up_button_market);
        inputName = (EditText) findViewById(R.id.signup_name_editText_market);
        inputEmail = (EditText) findViewById(R.id.signup_email_editText_market);
        inputPassword1 = (EditText) findViewById(R.id.signup_password_editText_market);
        inputPassword2 = (EditText) findViewById(R.id.pass_app_market);
        progressBar = (ProgressBar) findViewById(R.id.signup_progressBar_market);
        btnResetPassword = (Button) findViewById(R.id.signup_reset_password_button_market);



        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupMarket.this,ResetPassword.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUserName = inputName.getText().toString();
                final String email2 = inputEmail.getText().toString().trim();
                String password1 = inputPassword1.getText().toString().trim();
                String password2 = inputPassword2.getText().toString().trim();
                final int UserType2=2;

                if (TextUtils.isEmpty(signUpUserName)) {
                    Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email2)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password1)) {
                    Toast.makeText(getApplicationContext(), "Enter a password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password1.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!(password2.equals(shop_pass))) {
                    Toast.makeText(getApplicationContext(), "App Password is incorrect!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                email2x=email2;
                //create user
                auth.createUserWithEmailAndPassword(email2, password1)
                        .addOnCompleteListener(SignupMarket.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupMarket.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupMarket.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    final FirebaseUser user = auth.getCurrentUser();
                                    if (user != null) {
                                        Toast.makeText(SignupMarket.this,"User created",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(SignupMarket.this,"BİR ŞEYLER YANLIŞ",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });

            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){

                    String ee=getEmail2x();
                    // Name, email address, and profile photo Url
                    String uid = user.getUid();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();

                    //adding profile image is missing
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(signUpUserName)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupMarket.this, "Profile updated",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    user.updateEmail(ee)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupMarket.this, "email updated",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    myRef.child("Shops").child(uid).child("Mail").setValue(ee);
                    myRef.child("Shops").child(uid).child("Name").setValue(signUpUserName);
                    myRef.child("Shops").child(uid).child("UserType").setValue(usertypex);

                    ////////////////////////////////////////////////////////////////////
                    startActivity(new Intent(SignupMarket.this, OptionsActivityM.class));
                }
            }
        };
    }

    @Override
    public void onResume(){
        super.onResume();
        auth.addAuthStateListener(mAuthListener);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    public static String getEmail2x() {
        return email2x;
    }
}
