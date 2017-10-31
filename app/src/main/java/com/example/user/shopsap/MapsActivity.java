package com.example.user.shopsap;

import android.content.Intent;
import android.graphics.Color;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

/**
 * Created by user on 30.10.2017.
 */

public class MapsActivity extends AppCompatActivity
        implements OnSeekBarChangeListener, OnMarkerDragListener, OnMapLongClickListener,
        OnItemSelectedListener, OnMapReadyCallback {

    private static final double DEFAULT_RADIUS_METERS = 1000;
    private static final double RADIUS_OF_EARTH_METERS = 6371009;
    private GoogleMap mMap;

    private List<DraggableCircle> mCircles = new ArrayList<>(1);
    private CheckBox mClickabilityCheckbox;
    private Button approve;
    private FirebaseAuth auth;

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    private class DraggableCircle {
        private final Marker mCenterMarker;
        private final Marker mRadiusMarker;
        private final Circle mCircle;
        private double mRadiusMeters;
        private LatLng thecenter;

        public DraggableCircle(LatLng center, double radiusMeters) {
            mRadiusMeters = radiusMeters;
            mCenterMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true));
            mRadiusMarker = mMap.addMarker(new MarkerOptions()
                    .position(toRadiusLatLng(center, radiusMeters))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE)));
            mCircle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radiusMeters)
                    .strokeWidth(0)
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.GREEN)
                    .clickable(mClickabilityCheckbox.isChecked()));
            thecenter=center;

        }

        public boolean onMarkerMoved(Marker marker) {
            if (marker.equals(mCenterMarker)) {
                thecenter=marker.getPosition();
                mCircle.setCenter(marker.getPosition());
                mRadiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), mRadiusMeters));
                return true;
            }
            if (marker.equals(mRadiusMarker)) {
                mRadiusMeters =
                        toRadiusMeters(mCenterMarker.getPosition(), mRadiusMarker.getPosition());
                mCircle.setRadius(mRadiusMeters);
                return true;
            }
            return false;
        }
        public void setClickable(boolean clickable) {
            mCircle.setClickable(clickable);
        }

        public LatLng getThecenter() {
            return thecenter;
        }

        public double getmRadiusMeters() {
            return mRadiusMeters;
        }
    }

    /** Generate LatLng of radius marker */
    private static LatLng toRadiusLatLng(LatLng center, double radiusMeters) {
        double radiusAngle = Math.toDegrees(radiusMeters / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_demo);
        auth = FirebaseAuth.getInstance();
        mClickabilityCheckbox = (CheckBox) findViewById(R.id.toggleClickability);
        approve=(Button) findViewById(R.id.btn_okk);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap map) {
        // Override the default content description on the view, for accessibility mode.
        map.setContentDescription(getString(R.string.map_circle_description));

        mMap = map;
        mMap.setOnMarkerDragListener(this);

        double lat=OptionsActivityM.getLatitude();
        double lon=OptionsActivityM.getLongitude();

        LatLng home = new LatLng(lat, lon);
        final DraggableCircle circle = new DraggableCircle(home, DEFAULT_RADIUS_METERS);
        mCircles.add(circle);

        // Move the map so that it is centered on the initial circle
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 4.0f));

        approve.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                LatLng cent = circle.getThecenter();
                double lat=cent.latitude;
                double lon=cent.longitude;
                double rad=circle.getmRadiusMeters();

                String x=String.valueOf(rad);
                String y=String.valueOf(lon);
                String z=String.valueOf(lat);

                // \n is for new line
                Toast.makeText(getApplicationContext(), "Your circle's center is - \nLat: " + lat + "\nLong: " + lon + "\nRadius: " + rad, Toast.LENGTH_LONG).show();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                FirebaseUser user = auth.getCurrentUser();
                String uid = user.getUid();
                myRef.child("Shops").child(uid).child("CircleLat").setValue(z);
                myRef.child("Shops").child(uid).child("CircleLong").setValue(y);
                myRef.child("Shops").child(uid).child("CircleRad").setValue(x);

                startActivity(new Intent(MapsActivity.this, MenuMarket.class));
            }
        });




    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Don't do anything here.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }


    @Override
    public void onMarkerDragStart(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        onMarkerMoved(marker);
    }

    private void onMarkerMoved(Marker marker) {
        for (DraggableCircle draggableCircle : mCircles) {
            if (draggableCircle.onMarkerMoved(marker)) {
                break;
            }
        }
    }

    public void toggleClickability(View view) {
        boolean clickable = ((CheckBox) view).isChecked();
        // Set each of the circles to be clickable or not, based on the
        // state of the checkbox.
        for (DraggableCircle draggableCircle : mCircles) {
            draggableCircle.setClickable(clickable);
        }
    }
}
