package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;


        import android.app.Notification;
        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.Service;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.os.Build;
        import android.os.Handler;
        import android.os.IBinder;
        import android.preference.PreferenceManager;
        import android.provider.CallLog;
        import android.telephony.PhoneStateListener;
        import android.telephony.TelephonyManager;
        import android.util.Log;

        import androidx.core.app.NotificationCompat;
        import org.java_websocket.client.WebSocketClient;


        import java.io.IOException;
        import java.net.URI;
        import java.net.URISyntaxException;
        import java.util.Date;


        import static com.example.myapplication.Constants.HOST_URL_WOCKET;

public class WebSocketService extends Service {


    String token;
    String code_device;
    String host_ws;
    TelephonyManager tMg;
    PhoneStateListener listener;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;

    private static boolean isIncoming;
    private static boolean isAccepted;
    private static String savedNumber;

    WebSocketClient client;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {


        createNotification();
//        createlistener();
        load();
        try {
            client = new EmptyClient(new URI(host_ws+token),getApplicationContext());
            Log.d(Constants.TAG,"Create Socket");
            Log.d(Constants.TAG,code_device);
            Log.d(Constants.TAG, "HOST SOCKET: "+host_ws );
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d(Constants.TAG,"Error Create Socket");
            Log.d(Constants.TAG,code_device);
            Log.d(Constants.TAG, "HOST SOCKET: "+host_ws );
        }




        final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 5000);


                String status = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("STATUS_SERVICE","True");
                String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.TOKEN_KEY,"");

//                if(token.length()!=0) {
                if (client.isClosed() || client.isClosing()) {

                    SaveData("Disconect");
                    Log.d(Constants.TAG, "reconect");
                    new HTTPConnect().refreshToken(getApplicationContext());
                    try {
                        client = new EmptyClient(new URI(host_ws + token), getApplicationContext());
                        client.connect();
                    }catch(URISyntaxException e){

                    }

                } else {
                    SaveData("Connected");
                    Log.d(Constants.TAG, "Connected, token: "+ token);
                }
                if(client.isOpen()){
                    Log.d(Constants.TAG, "WEBSOCKET isOpen");
                }
//                }

            }
        };
        handler.post(run);
    }




    public void createNotification(){
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public void onDestroy() {
        Log.d("Destroy","Destroy");
        //client.close();

    }

    public void load(){
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.token = sPref.getString(Constants.TOKEN_KEY,"");
        this.code_device = sPref.getString("CODE_DEVICE","");
        this.host_ws = sPref.getString(HOST_URL_WOCKET, "");
    }

    public void  SaveData(String type){
        Intent intent1 = new Intent();
        intent1.setAction("com.example.callerid");
        intent1.putExtra("DATA", type);
        sendBroadcast(intent1);
    }




}
