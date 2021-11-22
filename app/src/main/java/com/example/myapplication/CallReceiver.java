package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

    public class CallReceiver extends BroadcastReceiver {


    private static boolean isIncoming;
    private static boolean isAccepted;
    private static String savedNumber;
    private static String savedNumber2;
    private static int count = 2;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;

    @Override
    public void onReceive(Context arg0, Intent arg1) {

Log.e("test", "test");
        String code_device="";
        SharedPreferences sPref;
        sPref = PreferenceManager.getDefaultSharedPreferences(arg0.getApplicationContext());
        code_device = sPref.getString("CODE_DEVICE","");


        if (arg1.getAction().equals("android.intent.action.PHONE_STATE")) {

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
                            new HTTPConnect().readCustomerContact(code_device, arg0, number);



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


                            Log.e("telephone", "COUNT = " + count);
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

                            Log.e("telephone", sb.toString());
                        }
                    }, 2000);

                    count = 2;
                    lastState = TelephonyManager.CALL_STATE_IDLE;

                }
            }
        }
    }


}




