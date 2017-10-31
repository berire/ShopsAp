package com.example.user.shopsap;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
public class MainActivity extends AppCompatActivity {

    private Button btnBuss, btnPers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mama);

        btnBuss= (Button) findViewById(R.id.business_btn);
        btnPers= (Button) findViewById(R.id.person_btn);

       /* btnPers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });*/

        btnBuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginMarket.class));
                finish();
            }
        });
    }
}