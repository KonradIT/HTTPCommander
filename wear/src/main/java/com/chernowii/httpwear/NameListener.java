package com.chernowii.httpwear;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Konrad Iturbe on 05/03/16.
 */
public class NameListener extends WearableListenerService {
    String sprefs = "shared_prefs_wear";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals("/name1")) {
            setname_command("1", String.valueOf(messageEvent.getData()));
        }
        if (messageEvent.getPath().equals("/name2")) {
            setname_command("2", String.valueOf(messageEvent.getData()));
        }
        if (messageEvent.getPath().equals("/name3")) {
            setname_command("3", String.valueOf(messageEvent.getData()));
        }
        if (messageEvent.getPath().equals("/name4")) {
            setname_command("4", String.valueOf(messageEvent.getData()));
        }
    }
    void setname_command(String id, String name){
        SharedPreferences sharedpreferences = getSharedPreferences(sprefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("name"+id, name);
        editor.commit();
    }
}