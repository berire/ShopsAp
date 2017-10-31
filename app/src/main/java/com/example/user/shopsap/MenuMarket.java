package com.example.user.shopsap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by user on 22.9.2017.
 */

public class MenuMarket extends AppCompatActivity {

    private Button add_item,view_orders,all_items,options;

    private FirebaseAuth mAuth;


    private FirebaseDatabase fire_database;
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu_market);
        Intent intent = this.getIntent();


        //
        add_item = (Button)findViewById(R.id.shop_add);
        all_items = (Button)findViewById(R.id.shop_items);
        view_orders = (Button)findViewById(R.id.shop_orders);
        options = (Button)findViewById(R.id.shop_org);

        //////
        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuMarket.this, BarcodeScanM.class);
                startActivity(intent);
            }
        });

        all_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuMarket.this,ViewItemActivityM.class);
                startActivity(intent);
            }
        });

        /*view_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuMarket.this,ViewOrdersActivityM.class);
                startActivity(intent);
            }
        });*/

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuMarket.this,OptionsActivityM.class);
                startActivity(intent);
            }
        });




    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
