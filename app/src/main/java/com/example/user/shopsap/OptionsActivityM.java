package com.example.user.shopsap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OptionsActivityM extends Activity {


    private Button btnShowLocation,btnRdr;

    // GPSTracker class
    private GPSTracker gps;
    private FirebaseAuth auth;
    private static double latitude=0.0 ;
    private static double  longitude = 0.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        btnShowLocation = (Button) findViewById(R.id.btn_addres);
        btnRdr=(Button) findViewById(R.id.btn_rdr);

        auth = FirebaseAuth.getInstance();

        // Show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Create class object
                gps = new GPSTracker(OptionsActivityM.this);

                // Check if GPS enabled
                if(gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    String lat=String.valueOf(latitude);
                    String lon=String.valueOf(longitude);

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid();
                    myRef.child("Shops").child(uid).child("Latitude").setValue(lat);
                    myRef.child("Shops").child(uid).child("Longitude").setValue(lon);

                    startActivity(new Intent(OptionsActivityM.this, MapsActivity.class));

                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }


            }
        });


        btnRdr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(OptionsActivityM.this, MapsActivity.class));

            }
        });
    }

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }
}
