package com.example.user.shopsap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by user on 20.8.2017.
 */

public class LoginMarket extends AppCompatActivity {

    private static final String TAG = "";
    private EditText inputEmail, inputPasswordShop;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private DatabaseReference newRef;
    private Button btnLogin,btnsignup;

   // private final String shop_pass="123455";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();



        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginMarket.this, MenuMarket.class));
            finish();
        }


        // set the view now
        setContentView(R.layout.activity_market_login);
        //Realm.init(this); //initialize other plugins

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        inputEmail = (EditText) findViewById(R.id.shop_mail);
        inputPasswordShop = (EditText) findViewById(R.id.shop_passwordM);
        progressBar = (ProgressBar) findViewById(R.id.shop_progressBarM);
        btnLogin = (Button) findViewById(R.id.login_market);
        btnsignup=(Button) findViewById(R.id.sign_upM);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginMarket.this, SignupMarket.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString();
                final String password1 = inputPasswordShop.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password1)) {
                    Toast.makeText(getApplicationContext(), "Enter your Password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password1)
                        .addOnCompleteListener(LoginMarket.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password1.length() < 6) {
                                        inputPasswordShop.setError("Password too short!");
                                    } else {
                                        Toast.makeText(LoginMarket.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginMarket.this, MenuMarket.class);
                                    startActivity(intent);
                                    //finish();
                                }
                            }
                        });

            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
