package com.example.myapplication;




        import static android.content.Context.WINDOW_SERVICE;

        import org.jetbrains.annotations.NotNull;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.preference.PreferenceManager;
        import android.util.Log;


        import java.io.IOException;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import okhttp3.Call;
        import okhttp3.Callback;
        import okhttp3.FormBody;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.RequestBody;
        import okhttp3.Response;

public class HTTPConnect {
    OkHttpClient client;
    public HTTPConnect(){
        client = new OkHttpClient();
    }


    public void login(ResultHandler callback, String login, String password, Context context)throws IOException {

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        String host = sPref.getString(Constants.HOST_URL,"");

        RequestBody formBody = new FormBody.Builder()
                .add("login", login)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("https://"+host+"/api/auth/login")
                .post(formBody)
                .build();




        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                callback.onFail(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String result = response.body().string();
                Log.d(Constants.TAG,"return Auth:"+result);
                callback.onSuccess(result);
            }
        });

    }
    public void endCall(String code_device,Context context){




        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        String host = sPref.getString(Constants.HOST_URL,"");
        String token = sPref.getString(Constants.TOKEN_KEY,"").replace("%20"," ");

        RequestBody formBody = new FormBody.Builder()
                .add("code_device", code_device)
                .build();

        Request request = new Request.Builder()
                .addHeader("Authorization",token)
                .url("https://"+host+"/api/calls/endCall ")
                .patch(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==401){
                    refreshToken(context);
                    Log.d(Constants.TAG,"endCall Error 401 (Refresh Token)");
                }
                else Log.d(Constants.TAG,"endCall Ok:"+response.body().string());
            }
        });
    }

    public void refreshToken(Context context) {

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sPref.getString(Constants.TOKEN_KEY,"").replace("Bearer%20","Bearer ");
        String host = sPref.getString(Constants.HOST_URL,"");

        RequestBody formBody = new FormBody.Builder()
                .add("token", token)
                .build();
        Request request = new Request.Builder()
                .url("https://"+host+"/api/auth/refreshToken")
                .post(formBody)
                .build();

        Log.d(Constants.TAG, "refresh: "+token);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(Constants.TOKEN_KEY, token);
                ed.commit();
                Log.d(Constants.TAG, "(Refresh Token) Result Err ");

            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                SharedPreferences.Editor ed = sPref.edit();


                ed.commit();
                Log.d(Constants.TAG, "(Refresh Token) Result OK");


                try {
                    String jsonStr = response.body().string();
                    JSONObject json = new JSONObject(jsonStr);

                    String newToken = json.getString("token");
                    String newTokenFormaf = newToken.replace("Bearer ","Bearer%20");
                    ed.putString(Constants.TOKEN_KEY, newTokenFormaf);
                    Log.d(Constants.TAG, "refresh new token: "+newTokenFormaf);
                    ed.commit();




                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });
    }


    public void customerContact(String tel, String code_device, String date, Context context)throws IOException {

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sPref.getString(Constants.TOKEN_KEY,"").replace("%20"," ");
        String host = sPref.getString(Constants.HOST_URL,"");
        Log.d(Constants.TAG,"inCall tel="+tel+"code_device="+code_device);
        RequestBody formBody = new FormBody.Builder()
                .add("tel", tel)
                .add("typeContact", "inCall")
                .add("code_device", code_device)
                .add("date", date)
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization",token)
                .url("https://"+host+"/api/calls/customerContact")
                .post(formBody)
                .build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==401){
                    refreshToken(context);
                    Log.d(Constants.TAG,"customerContact Error 401 (Refresh Token)");
                }
                else Log.d(Constants.TAG,"customerContact Ok:"+response.body().string());
            }
        });
    }

    public void customerContactSecondLine(String tel, String code_device,  String date, Context context)throws IOException {


        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sPref.getString(Constants.TOKEN_KEY,"").replace("%20"," ");
        String host = sPref.getString(Constants.HOST_URL,"");
        Log.d(Constants.TAG,"inCall tel="+tel+"code_device="+code_device);
        RequestBody formBody = new FormBody.Builder()
                .add("tel", tel)
                .add("typeContact", "inCall")
                .add("code_device", code_device)
                .add("date", date)
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization",token)
                .url("https://"+host+"/api/calls/customerContactSecondLine")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==401){
                    refreshToken(context);
                    Log.d(Constants.TAG,"customerContact Error 401 (Refresh Token)");
                }
                else Log.d(Constants.TAG,"customerContact Ok:"+response.body().string());
            }
        });
    }

    public void updateCustomerContact(String tel, String talk_time, String date, Context context)throws IOException {


        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sPref.getString(Constants.TOKEN_KEY,"").replace("%20"," ");
        String code_device = sPref.getString("CODE_DEVICE","");
        String host = sPref.getString(Constants.HOST_URL,"");

        RequestBody formBody = new FormBody.Builder()
                .add("tel", tel)
                .add("talk_time", talk_time)
                .add("code_device", code_device)
                .add("date", date)
                .build();
        Request request = new Request.Builder()
                .addHeader("Authorization",token)
                .url("https://"+host+"/api/calls/customerContactUpdateDiraction")
                .patch(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==401){
                    refreshToken(context);
                    Log.d(Constants.TAG,"customerContact Error 401 (Refresh Token)");
                }
                else Log.d(Constants.TAG,"updateCustomerContact Ok:"+response.body().string());
            }
        });
    }



    public void readCustomerContact(String code_device, Context context, String tel){

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String token = sPref.getString(Constants.TOKEN_KEY,"").replace("%20"," ");
        String host = sPref.getString(Constants.HOST_URL,"");
        Request request = new Request.Builder()
                .addHeader("Authorization",token)
                .url("https://"+host+"/api/calls/readCustomerContact?code_device="+code_device+"&typeContact=inCall&tel="+tel.replace("+38", ""))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==401){
                    refreshToken(context.getApplicationContext());
                    Log.d(Constants.TAG,"readCustomerContact Error 401 (Refresh Token)");
                }
                else {
                    String res=response.body().string();
                    Log.d(Constants.TAG,"readCustomerContact Ok:"+res);
                    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("readCustomerContact", res);
                    ed.commit();

                    try {
                        String jsonStr = res;
                        JSONObject json = new JSONObject(jsonStr);
                        JSONArray docs = json.getJSONArray("docs");
                        JSONObject doc = docs.getJSONObject(0);
                        if(doc.length()==0){
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                    Intent intent = new Intent(context, InfoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);




                }
            }
        });
    }


}
