package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;




public class LoginActivity extends AppCompatActivity implements ResultHandler {

    EditText login;
    EditText password;
    EditText host;
    String token;
    String code_device;
    String userName;
    public static SharedPreferences sPref;
    public static Context context;
    public static Context getContextOfApplication(){
        return context;
    }
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=findViewById(R.id.login);
        password=findViewById(R.id.password);
        host=findViewById(R.id.host);


        host.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.HOST_URL,""));
        login.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("login",""));
        password.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("password",""));


        loadToken();
        if(token.length()!=0)startSetting();
    }

    public void login(View view) {
        try {

            String l = login.getText().toString();
            String p = password.getText().toString();
            String h = host.getText().toString();

            sPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(Constants.HOST_URL_WOCKET, "wss://"+h+"/wsCalls?token=");
            ed.putString(Constants.HOST_URL, h);
            ed.putString("login", l);
            ed.putString("password", p);



            ed.commit();


            String h_ws = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.HOST_URL_WOCKET,"");


            if(l.length()!=0&&p.length()!=0&&host.length()!=0&&h_ws.length()!=0){
                new HTTPConnect().login(this, l, p, getApplicationContext());
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToken(String token, String code_device, String userName){
        token=token.replace(" ","%20");
        this.token=token;
        this.userName = userName;
        this.code_device=code_device;
        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(Constants.TOKEN_KEY,token);
        ed.putString("CODE_DEVICE",code_device);
        ed.putString("USER_NAME",userName);


        ed.commit();
    }
    public void loadToken(){
        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        token = sPref.getString(Constants.TOKEN_KEY,"");
        code_device = sPref.getString("CODE_DEVICE","");

    }
    public void startSetting() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onSuccess(String response) {
        try {
            JSONObject json = new JSONObject(response);
            if(json.has("token")){
                String token =json.getString("token");
                String code_device =json.getString("code_device");
                String userName =json.getString("name");

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveToken(token,code_device,userName);
                        Log.d(Constants.TAG,"Loggin");
                        startSetting();

                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onFail(IOException error) {
        Log.d(Constants.TAG,"ERROR");
    }
}