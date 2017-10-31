package com.example.user.shopsap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 24.9.2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ViewItemActivityM extends AppCompatActivity{

    private ArrayList<String> userClone;

    private ListView itemsList;
    ArrayList<Item> items;
    ArrayList<String> name;
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef;
    private FirebaseDatabase myRef;
    private String userID;
    private DatabaseReference mPostReference;
    ArrayList<String> Actids;
    ArrayList<String> inames;
    String anid;
    String value;

    ArrayAdapter<String> arrAdap;
    private final Context context=getBaseContext();
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_showitems);
        itemsList = (ListView) findViewById(R.id.itemsList);
        Actids=new ArrayList<>();
        inames=new ArrayList<>();
        inames.add("Your Items; ");
        //Previous versions of Firebase
        Firebase.setAndroidContext(this);

        items=new ArrayList<Item>();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Shops");


        arrAdap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, inames);
        itemsList.setAdapter(arrAdap);


        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference( )
                .child("Shops").child(userID).child("Items");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()){
                    anid=(String)child.getKey();
                    Actids.add(anid);

                    Toast.makeText(ViewItemActivityM.this, "bebelr",
                            Toast.LENGTH_SHORT).show();
                }
                for (int i=0;i<Actids.size();i++)
                {
                    Item act=new Item();
                    String aKey= Actids.get(i);

                    Toast.makeText(ViewItemActivityM.this, "urdayms: "+aKey,
                            Toast.LENGTH_SHORT).show();
                    value=(String) dataSnapshot.child(aKey).child("ItemName").getValue();
                    act.setItemname(value);
                    act.setBarcode(aKey);

                    act.setCount((dataSnapshot.child(aKey).child("ItemCount").getValue()).toString());
                    act.setDescription((( dataSnapshot.child(aKey).child("ItemDExpln").getValue()).toString()));
                    act.setPrice(( dataSnapshot.child(aKey).child("ItemPrice").getValue()).toString());
                    act.setType(( dataSnapshot.child(aKey).child("ItemType").getValue()).toString());

                    items.add(act);
                    inames.add(value);
                    arrAdap.notifyDataSetChanged();
                }
                if(Actids.size()==0)
                {
                    inames.add("NO ITEM EXISTS");
                    arrAdap.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // [START_EXCLUDE]
                Toast.makeText(ViewItemActivityM.this, "Failed to load activities",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mPostReference.addValueEventListener(postListener);





        /*itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder adb=new AlertDialog.Builder(ViewItemActivityM.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete the item " + i);
                final int positionToRemove = i;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String b = Actids.get(positionToRemove);
                        dataRef.child(userID).child("Items").child(b).removeValue();
                        items.remove(positionToRemove);
                        arrAdap.notifyDataSetChanged();
                        Toast.makeText(ViewItemActivityM.this, "Item Removed !",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ViewItemActivityM.this,MenuMarket.class);
                        startActivity(intent);
                    }});
                adb.show();
            }

        });*/
    }
}
