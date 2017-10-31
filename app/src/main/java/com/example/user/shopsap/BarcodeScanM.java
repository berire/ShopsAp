package com.example.user.shopsap;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 24.9.2017.
 */


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.acker.simplezxing.activity.CaptureActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


public class BarcodeScanM extends AppCompatActivity {


    //https://barcodesdatabase.org/barcode/9421021461303
    //http://api.upcdatabase.org/xml/07e5adeadd8c349323f156c40356ae87/9421021461303
    //ean 13 code

    private static final int REQ_CODE_PERMISSION = 0x1111;
    private TextView tvResult;
    private static String baseUrl = "http://api.upcdatabase.org/xml/07e5adeadd8c349323f156c40356ae87/";
    private static String thebarcode="1122334455667";

    //Barcode details

    private String isvalid,itemname,description,avgprice,htmlerror,reason;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        tvResult = (TextView) findViewById(R.id.tv_result);
        Button btn = (Button) findViewById(R.id.btn_sm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Scan Activity
                if (ContextCompat.checkSelfPermission(BarcodeScanM.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(BarcodeScanM.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    startCaptureActivityForResult();
                }
            }
        });
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(BarcodeScanM.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);

        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    Toast.makeText(this, "You must agree the camera permission request before you use the code scan function", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        thebarcode=data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                        AddItemActivityM.setItembarcode(thebarcode);
                        if(thebarcode.equals("1122334455667"))
                        {
                            AddItemActivityM.itembarcode="0000000000000";
                        }
                        tvResult.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));  //or do sth
                        new RequestTask(new OnTaskFinished()
                        {
                            @Override
                            public void onFeedRetrieved(String feeds)
                            {
                                BarcodeSearch(feeds,thebarcode);

                            }
                        }).execute(baseUrl+thebarcode);
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                            tvResult.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                }
                break;
        }
    }

    public void BarcodeSearch(String feeds,String barcode)
    {

        StringBuilder builder = new StringBuilder();

        builder.append(feeds.trim());

        String startV = "<valid>";
        String endV = "</valid>";
        String startN = "<number>";
        String endN = "</number>";
        String startD = "<description>";
        String endD = "</description>";
        String startP = "<avgprice>";
        String endP = "</avgprice>";


        Pattern pattern1 = Pattern.compile(startV+"(.*?)"+endV);
        Matcher matcher1 = pattern1.matcher(feeds);
        while (matcher1.find()) {
            isvalid=matcher1.group(1);
        }

        AddItemActivityM.setisvalidx("false");
        AddItemActivityM.setItembarcode(barcode);
        AddItemActivityM.reasonx="not found on datbase";
        if(isvalid.contains("true"))
        {
            AddItemActivityM.setisvalidx("true");
            Pattern pattern2 = Pattern.compile(startN+"(.*?)"+endN);
            Matcher matcher2 = pattern2.matcher(feeds);
            while (matcher2.find()) {
                itemname=matcher2.group(1);
            }

            Pattern pattern3 = Pattern.compile(startD+"(.*?)"+endD);
            Matcher matcher3 = pattern3.matcher(feeds);
            while (matcher3.find()) {

                description=matcher3.group(1);
            }

            Pattern pattern4 = Pattern.compile(startP+"(.*?)"+endP);
            Matcher matcher4 = pattern4.matcher(feeds);
            while (matcher4.find()) {
                avgprice=matcher4.group(1);
            }
            AddItemActivityM.setItembarcode(barcode);
            AddItemActivityM.setItemnamex(itemname);
            AddItemActivityM.setAvgpricex(avgprice);
            AddItemActivityM.setDescriptionx(description);

        }
        Intent intent = new Intent(BarcodeScanM.this,AddItemActivityM.class);
        startActivity(intent);

    }



}
