package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;



public class InfoActivity extends AppCompatActivity {
    TextView textView;
    TextView dateField;
    TextView adress;
    TextView userField;
    TextView stateField;


    TelephonyManager tMg;
    PhoneStateListener listener;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        textView=findViewById(R.id.textView3);

        dateField=findViewById(R.id.dateField);

        adress=findViewById(R.id.adressField);

        userField=findViewById(R.id.userField);

        stateField=findViewById(R.id.stateField);

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        String data = sPref.getString("readCustomerContact","");


        try {
            String jsonStr = data;
            JSONObject json = new JSONObject(jsonStr);


                JSONArray docs = json.getJSONArray("docs");
                JSONObject doc = docs.getJSONObject(0);
            if(doc.length()!=0){

                String docName = doc.getString("documentName");
                String docnumber = doc.getString("number");
                String docDate = doc.getString("dateDocument");
                String user = doc.getString("userName");
                String county = doc.getString("county");
                String city = doc.getString("city");
                String street = doc.getString("street");
                String state = doc.getString("state");



                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat newFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

                try {

                    Date date = formatter.parse(docDate);

                    dateField.setText(newFormatter.format(date));

                } catch (ParseException e) {
                    e.printStackTrace();
                }




                textView.setText(docName+docnumber);

                adress.setText(county+" "+city+" "+street);

                userField.setText(user);
                stateField.setText(state);


            }else{

                textView.setText("");
                dateField.setText("");
                adress.setText("");

                userField.setText("");
                stateField.setText("");


            }








        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}