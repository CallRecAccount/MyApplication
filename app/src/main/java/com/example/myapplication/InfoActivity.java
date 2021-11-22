package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;



public class InfoActivity extends AppCompatActivity {
    TextView textView;
    TextView dateField;
    TextView adress;
    TextView userField;
    TextView stateField;
    TextView fdResason;
    TextView fdKetel;
    TextView fdDebt;
    TextView fbMaster_name;
    TextView fbGarantState;

    ImageButton imgBack;
    ImageView imageImportant;

    TelephonyManager tMg;
    PhoneStateListener listener;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        imgBack = findViewById(R.id.btnBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("buttonTest", "click");
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        textView=findViewById(R.id.txtDocName);
        dateField=findViewById(R.id.dateField);
        adress=findViewById(R.id.adressField);
        userField=findViewById(R.id.userField);
        stateField=findViewById(R.id.stateField);
        fdResason = findViewById(R.id.fdResason);
        fdKetel = findViewById(R.id.fdKetel);
        fdDebt = findViewById(R.id.fdDebt);
        imageImportant = findViewById(R.id.important);
        fbMaster_name = findViewById(R.id.master_name);
        fbGarantState  = findViewById(R.id.fdGarantState);

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
                String ketel = doc.getString("ketel");
                String resason = doc.getString("resason");
                String state = doc.getString("state");
                String debtFlag = doc.getString("debtFlag");
                String debt = doc.getString("debt");
                String important = doc.getString("important");
                String master_name = doc.getString("master_name");
                JSONArray garantState = doc.getJSONArray("garantState");


                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat newFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                SimpleDateFormat newFormatter2 = new SimpleDateFormat("dd.MM.yyyy");


                try {

                    Date date = formatter.parse(docDate);
                    Calendar instance = Calendar.getInstance();
                    instance.setTime(date); //устанавливаем дату, с которой будет производить операции
                    instance.add(Calendar.HOUR, 3);// прибавляем 3 часа к установленной дате
                    Date newDate = instance.getTime(); // получаем измененную дату



                    dateField.setText(newFormatter.format(newDate));



                    if(docName.contains("Гарант")){
                        if(garantState.getString(0).contains("notLaunched")){
                            fbGarantState.setText("Запуск не проведений");
                            fbGarantState.setTextColor(Color.rgb(170,150,20));
                        }else  if(garantState.getString(0).contains("notGuaranteed")){
                            fbGarantState.setText("Гарантія закінчилася: "+ newFormatter2.format(formatter.parse(garantState.getString(2))));
                            fbGarantState.setTextColor(Color.rgb(170,10,0));
                        }else{
                            fbGarantState.setText("Гарантія дяйсна до: "+ newFormatter2.format(formatter.parse(garantState.getString(2))));
                            fbGarantState.setTextColor(Color.rgb(0,170,40));
                        }

                    }else{
                        fbGarantState.setText("");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView.setText(docName+docnumber);
                adress.setText((county+" "+city+" "+street).trim());
                userField.setText(user);
                stateField.setText(state);
                fdKetel.setText(ketel);
                fdResason.setText(resason);
                if(debt!="" && debtFlag.contains("1")) {
                    fdDebt.setText("Борг " +debt+" грн.");
                }else{
                    fdDebt.setText("");
                }

                if(important.contains("1")){
                    imageImportant.setVisibility(View.VISIBLE);
                }else{
                    imageImportant.setVisibility(View.INVISIBLE);
                }
                if(state.contains("Прийнят")){
                    stateField.setTextColor(Color.rgb(200,0,0));
                }else if(state.contains("Очікує")){
                    stateField.setTextColor(Color.rgb(200,180,0));
                }else  if(state.contains("Видан")||state.contains("робот")){
                    stateField.setTextColor(Color.rgb(0,140,70));
                }

                if(master_name.contains("null")){
                    fbMaster_name.setText("");
                }else {
                    fbMaster_name.setText(master_name);
                }



            }else{

                textView.setText("");
                dateField.setText("");
                adress.setText("");
                userField.setText("");
                stateField.setText("");
                fdKetel.setText("");
                fdResason.setText("");
                fdDebt.setText("");
                fbMaster_name.setText("");
            }








        } catch (JSONException e) {
            e.printStackTrace();
        }



    }




}