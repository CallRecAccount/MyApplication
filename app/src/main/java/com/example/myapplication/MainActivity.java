package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b0,bstar,bhash,bcall;
ImageButton back;
TextView tv;
TextView txtstate;
TextView txtcode;
TextView txtusername;
ImageView imgConnect;
    String token;
    String code_device = "";
    String host;
    String host_ws;
    String userName;
    SharedPreferences sPref;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s1 = intent.getStringExtra("DATA");
            txtstate.setText(s1);
        if(s1.contains("Disconect")){
            imgConnect.setVisibility(View.INVISIBLE);
        }else{
            imgConnect.setVisibility(View.VISIBLE);
        }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9);
        b0 = findViewById(R.id.b0);
        bstar = findViewById(R.id.bstar);
        bhash = findViewById(R.id.bhash);
        bcall = findViewById(R.id.bcall);
        back = findViewById(R.id.back);
        tv = findViewById(R.id.txtphone);
        txtstate = findViewById(R.id.txtstate);
        txtcode = findViewById(R.id.txtcode);
        txtusername = findViewById(R.id.txtusername);
        imgConnect = findViewById(R.id.connect);


        txtstate.setText("Connecting...");
        imgConnect.setVisibility(View.INVISIBLE);
        load();



        Dexter.withContext(this).withPermission(Manifest.permission.CALL_PHONE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();



        Dexter.withContext(this).withPermission(Manifest.permission.READ_PHONE_STATE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"1");
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"2");
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"3");
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"4");
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"5");
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"6");
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"7");
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"8");
            }
        });
        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"9");
            }
        });
        b0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"0");
            }
        });
        bstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"*");
            }
        });
        bhash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(tv.getText().toString()+"#");
            }
        });
        bcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makephonecall();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv.getText().length()==0){
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder(tv.getText());
                stringBuilder.deleteCharAt(tv.getText().length()-1);
                String newString = stringBuilder.toString();
                tv.setText(newString);
            }
        });




        txtcode.setText("Code Device:"+code_device);
        txtusername.setText("user: "+userName);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.callerid");
        registerReceiver(broadcastReceiver, intentFilter);
        Intent i=new Intent(MainActivity.this, WebSocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("DebugTest","startForegroundService");
            startForegroundService(i);
        }
        else {
            startService(i);
        }

    }




    private void makephonecall(){
        if(tv.getText().toString().length()==0){return;}
        String number = tv.getText().toString();
        String dial = "tel:"+number;
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
    }

    public void save(String token, String code_device){
        this.token=token.replace(" ","%20");
        this.code_device=code_device;
        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(Constants.TOKEN_KEY,token);
        ed.putString("CODE_DEVICE",code_device);


        ed.commit();
    }

    public void load(){
        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.token = sPref.getString(Constants.TOKEN_KEY,"");
        this.code_device = sPref.getString("CODE_DEVICE","");
        this.userName = sPref.getString("USER_NAME","");
        this.host = sPref.getString(Constants.HOST_URL,"");
        this.host_ws = sPref.getString(Constants.HOST_URL_WOCKET,"");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mymenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if ("Exit".equals(item.getTitle())) {
            Log.d(Constants.TAG,"Exit");
            save("","");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



}


