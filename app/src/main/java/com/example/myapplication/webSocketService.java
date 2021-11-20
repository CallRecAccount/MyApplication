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


import static com.example.myapplication.Constants.HOST_URL_WOCKET;

public class webSocketService extends Service {


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
        intent1.setAction("com.example.myapplication");
        intent1.putExtra("DATA", type);
        sendBroadcast(intent1);
    }




}


 class callService extends BroadcastReceiver {

     private static boolean isIncoming;
     private static boolean isAccepted;
     private static String savedNumber;
     private static String savedNumber2;
     private static int count = 2;
     private static int lastState = TelephonyManager.CALL_STATE_IDLE;

     @Override
     public void onReceive(Context arg0, Intent arg1) {
                String code_device="";
                SharedPreferences sPref;
                sPref = PreferenceManager.getDefaultSharedPreferences(arg0.getApplicationContext());
                code_device = sPref.getString("CODE_DEVICE","");


         if(arg1.getAction().equals("android.intent.action.PHONE_STATE")){

             String state = arg1.getExtras().getString(TelephonyManager.EXTRA_STATE);
             String number = arg1.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);


             if (number != null && !number.isEmpty() && !number.equals("null")) {


                 if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                     lastState = TelephonyManager.CALL_STATE_OFFHOOK;
                     Log.e("telephone", number);
                     isAccepted = true;

                 } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                     Log.e("telephone", "Вхідний дзвінок");
                     Log.e("telephone", number);

                     isAccepted = false;
                     isIncoming = true;
                     savedNumber = number;


                     if (lastState == TelephonyManager.CALL_STATE_OFFHOOK) {
                         Log.e("telephone", "Друга лінія");
                         Log.e("telephone", number);
                         count = count + 1;

                         try {
                             new HTTPConnect().customerContactSecondLine(number, code_device, new Date().toString(), arg0.getApplicationContext());
                         } catch (IOException e) {
                             e.printStackTrace();
                         }

                     } else {

                         try {
                             new HTTPConnect().customerContact(number, code_device, new Date().toString(), arg0.getApplicationContext());
//                   new HTTPConnect().readCustomerContact(code_device, getApplicationContext());
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         lastState = TelephonyManager.CALL_STATE_RINGING;

                     }


                 } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                     Log.e("telephone", "Завершення дзвінка");


                     Handler handler = new Handler();
                     handler.postDelayed(new Runnable() {
                         public void run() {
                             try {
                                 this.getCallDetails(arg0.getApplicationContext(), count);
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                         }

                         private void getCallDetails(Context arg0, int count) throws IOException {

                             StringBuffer sb = new StringBuffer();
                             String[] projection = new String[]{
                                     CallLog.Calls.CACHED_NAME,
                                     CallLog.Calls.NUMBER,
                                     CallLog.Calls.TYPE,
                                     CallLog.Calls.DATE,
                                     CallLog.Calls.DURATION,
                                     CallLog.Calls._ID
                             };


                             Log.e(Constants.TAG, "COUNT = " + count);
                             Cursor managedCursor = arg0.getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls._ID + " DESC Limit " + count);


                             int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                             int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                             int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                             int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                             sb.append("Call Details :");
                             while (managedCursor.moveToNext()) {
                                 String phNumber = managedCursor.getString(number);
                                 String callType = managedCursor.getString(type);
                                 String callDate = managedCursor.getString(date);
                                 Date callDayTime = new Date(Long.valueOf(callDate));
                                 String callDuration = managedCursor.getString(duration);
                                 String dir = null;
                                 int dircode = Integer.parseInt(callType);
                                 switch (dircode) {
                                     case CallLog.Calls.OUTGOING_TYPE:
                                         dir = "outCall";
                                         break;

                                     case CallLog.Calls.INCOMING_TYPE:
                                         dir = "inCall";
                                         break;

                                     case CallLog.Calls.MISSED_TYPE:
                                         dir = "MISSED";
                                         break;
                                 }
                                 sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                                         + dir + " \nCall Date:--- " + callDayTime
                                         + " \nCall duration in sec :--- " + callDuration);
                                 sb.append("\n----------------------------------");


                                 new HTTPConnect().updateCustomerContact(phNumber, callDuration, callDayTime.toString(), arg0);
                             }
                             managedCursor.close();

                             Log.e(Constants.TAG, sb.toString());
                         }
                     }, 2000);

                     count = 2;
                     lastState = TelephonyManager.CALL_STATE_IDLE;

                 }
             }
         }
     }





//     private void getCallDetails(Context arg0, int count) throws IOException {
//
//         StringBuffer sb = new StringBuffer();
//         String[] projection = new String[] {
//                 CallLog.Calls.CACHED_NAME,
//                 CallLog.Calls.NUMBER,
//                 CallLog.Calls.TYPE,
//                 CallLog.Calls.DATE,
//                 CallLog.Calls.DURATION,
//                 CallLog.Calls._ID
//         };
//
//         Date d = new Date();//intialize your date to any date
//         Date dateBefore = new Date(d.getTime() - 1 * 1 * 3600 * 1000  );
//Log.e(Constants.TAG, "COUNT = "+count);
//         Cursor managedCursor = arg0.getApplicationContext().getContentResolver().query( CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls._ID + " DESC Limit "+count);
//
//
//
//         int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
//         int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
//         int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
//         int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
//         sb.append("Call Details :");
//         while (managedCursor.moveToNext()) {
//             String phNumber = managedCursor.getString(number);
//             String callType = managedCursor.getString(type);
//             String callDate = managedCursor.getString(date);
//             Date callDayTime = new Date(Long.valueOf(callDate));
//             String callDuration = managedCursor.getString(duration);
//             String dir = null;
//             int dircode = Integer.parseInt(callType);
//             switch (dircode) {
//                 case CallLog.Calls.OUTGOING_TYPE:
//                     dir = "outCall";
//                     break;
//
//                 case CallLog.Calls.INCOMING_TYPE:
//                     dir = "inCall";
//                     break;
//
//                 case CallLog.Calls.MISSED_TYPE:
//                     dir = "MISSED";
//                     break;
//             }
//             sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
//                     + dir + " \nCall Date:--- " + callDayTime
//                     + " \nCall duration in sec :--- " + callDuration);
//             sb.append("\n----------------------------------");
//
//
//             new HTTPConnect().updateCustomerContact(phNumber, callDuration, callDayTime.toString(), arg0);
//         }
//         managedCursor.close();
//
//Log.e(Constants.TAG, sb.toString());
//
//     }



}