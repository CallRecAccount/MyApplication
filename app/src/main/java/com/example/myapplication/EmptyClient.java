package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.nio.ByteBuffer;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class EmptyClient extends WebSocketClient {
    Context context;
    URI serverURI;

    public EmptyClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public EmptyClient(URI serverURI, Context context) {
        super(serverURI);
        Log.d(Constants.TAG,"new connection EmptyClient");
        this.context=context;
        this.serverURI=serverURI;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(Constants.TAG,"connected");
        Log.d(Constants.TAG,"new connection opened");
        Log.d(Constants.TAG, String.valueOf(this.isOpen()));

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {


        Log.d(Constants.TAG,"closed with exit code " + code + " additional info: " + reason);
    }


    @Override
    public void onMessage(String message) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sPref.getString(Constants.TOKEN_KEY,"");
        if(token.length()==0)return;
        String data="";
        String code_device="";

        String code_device_local = sPref.getString("CODE_DEVICE","");

        String name ="";
        Log.d(Constants.TAG,message);
        try {
            JSONObject jsonRoot = new JSONObject(message);
            name = jsonRoot.getString("name");
            data = jsonRoot.getString("data");
            code_device = jsonRoot.getString("code_device");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(name.equals("outCall")){

            if(code_device.equals(code_device_local)) {

                Log.d(Constants.TAG,code_device+" + "+code_device_local );


                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + data));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.d(Constants.TAG, data);
                context.startActivity(intent);
            }
        }


        Log.d(Constants.TAG,"received message: " + data);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        Log.d(Constants.TAG,"received ByteBuffer");
    }



    @Override
    public void onError(Exception ex) {
        Log.d(Constants.TAG,"an error occurred:" + ex);
    }

}