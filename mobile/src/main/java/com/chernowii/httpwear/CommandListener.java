package com.chernowii.httpwear;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Konrad Iturbe on 05/02/16.
 */
public class CommandListener  extends WearableListenerService {
    String sprefs = "shared_prefs";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals("/command1")) {
            launch_command("1");
        }
        if (messageEvent.getPath().equals("/command2")) {
            launch_command("2");
        }
        if (messageEvent.getPath().equals("/command3")) {
            launch_command("3");
        }
        if (messageEvent.getPath().equals("/command4")) {
            launch_command("4");
        }
    }
    public void launch_command(String number){
        final String prefsfornumber = "url_comm"+number;

        //get url for command 1 from SP
        SharedPreferences sharedpreferences = getSharedPreferences(sprefs, Context.MODE_PRIVATE);

        String restoredText = sharedpreferences.getString(prefsfornumber, null);


        new BackgroundTask().execute(number);

    }
    public class BackgroundTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "BackgroundTask";

        @Override
        protected String doInBackground(String... num) {

            //get url for command 1 from SP
            SharedPreferences sharedpreferences = getSharedPreferences(sprefs, Context.MODE_PRIVATE);
            final String prefsfornumber = "url_comm"+num;
            String restoredText = sharedpreferences.getString(prefsfornumber, null);
            final String prefsforgetpost = "getpost_comm"+num;
            final String prefsformimetype = "postmime_comm"+num;
            final String prefsforpostdata = "postdata_comm"+num;
            Boolean getorPost = sharedpreferences.getBoolean(prefsforgetpost, false);
            String restoredMimeType = sharedpreferences.getString(prefsformimetype, null);
            String restoredPostData = sharedpreferences.getString(prefsforpostdata, null);
            //.post(RequestBody.create(MimeType, postBody))
            if(!getorPost) {
                Response response = null;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(restoredText)
                        .build();

                try {
                    response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else{
                Response response = null;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(restoredText)
                        .post(RequestBody.create(MediaType.parse(restoredMimeType), restoredPostData))
                        .build();

                try {
                    response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("OkHttp :", result);
        }

    }

}