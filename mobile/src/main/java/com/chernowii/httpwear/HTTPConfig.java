package com.chernowii.httpwear;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.internal.Command;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPConfig extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    //config buttons
    ImageButton config1;
    ImageButton config2;
    ImageButton config3;
    ImageButton config4;

    //command buttons

    Button Command_1;
    Button Command_2;
    Button Command_3;
    Button Command_4;

    //okhttp stuff
    OkHttpClient client = new OkHttpClient();

    //shared prefs stuff
    String sprefs = "shared_prefs";


    //sends to wear

    Node wearNode;
    GoogleApiClient wearGoogleApiClient;
    private boolean mResolvingError=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_httpconfig);

        wearGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        SharedPreferences sharedpreferences = getSharedPreferences(sprefs, Context.MODE_PRIVATE);

        config1 = (ImageButton)findViewById(R.id.config1);
        config1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchConfig("1");
            }
        });
        config2 = (ImageButton)findViewById(R.id.config2);
        config2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchConfig("2");
            }
        });
        config3 = (ImageButton)findViewById(R.id.config3);
        config3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchConfig("3");
            }
        });
        config4 = (ImageButton)findViewById(R.id.config4);
        config4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchConfig("4");
            }
        });

        //command button code
        Command_1 = (Button)findViewById(R.id.command1);
        Command_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch_command("1");
            }
        });

        Command_2 = (Button)findViewById(R.id.command2);
        Command_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch_command("2");
            }
        });

        Command_3 = (Button)findViewById(R.id.command3);
        Command_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch_command("3");
            }
        });

        Command_4 = (Button)findViewById(R.id.command4);
        Command_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch_command("4");
            }
        });
        if(sharedpreferences.getBoolean("name_1_set", false)){
            Command_1.setText(sharedpreferences.getString("name_comm1","Command 1"));
        }
        if(sharedpreferences.getBoolean("name_2_set", false)){
            Command_2.setText(sharedpreferences.getString("name_comm2","Command 2"));
        }
        if(sharedpreferences.getBoolean("name_3_set", false)){
            Command_3.setText(sharedpreferences.getString("name_comm3","Command 3"));
        }
        if(sharedpreferences.getBoolean("name_4_set", false)){
            Command_4.setText(sharedpreferences.getString("name_comm4","Command 4"));
        }
    }
    public void launchConfig(final String number){
        final String prefsfornumber = "url_comm"+number;
        final String prefsforname = "name_comm"+number;
        final String prefsforgetpost = "getpost_comm"+number;
        final String prefsformimetype = "postmime_comm"+number;
        final String prefsforpostdata = "postdata_comm"+number;



        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HTTPConfig.this);
        alertDialog.setTitle("Command "+number + " details");
        alertDialog.setMessage("Fill in the Name and HTTP GET URL");

        final LinearLayout layout = new LinearLayout(HTTPConfig.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText NameText = new EditText(HTTPConfig.this);
        NameText.setHint("Command Name");
        layout.addView(NameText);

        final EditText URLText = new EditText(HTTPConfig.this);
        URLText.setHint("http://");
        layout.addView(URLText);

        //
        final EditText MIMEType = new EditText(HTTPConfig.this);
        final EditText POSTParams = new EditText(HTTPConfig.this);
        //

        final CheckBox GetPostBox = new CheckBox(HTTPConfig.this);
        GetPostBox.setText("Unchecked=GET / Checked=POST");
        layout.addView(GetPostBox);
        GetPostBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    MIMEType.setHint("Type (text/x-markdown; charset=utf-8)");
                    layout.addView(MIMEType);

                    POSTParams.setHint("Parameters");
                    layout.addView(POSTParams);
                    alertDialog.setView(layout);
                }
            }
        });
        SharedPreferences sharedpreferences = getSharedPreferences(sprefs, Context.MODE_PRIVATE);

        String restoredText = sharedpreferences.getString(prefsfornumber, null);
        String restoredName = sharedpreferences.getString(prefsforname, null);

        if (restoredText != null) {
            URLText.setHint(restoredText);
        }
        else{
            URLText.setHint("http://");
        }
        if (restoredName != null) {
            NameText.setHint(restoredName);
        }
        else{
            NameText.setHint("Command Name");
        }
        alertDialog.setView(layout);


        alertDialog.setIcon(android.R.drawable.ic_menu_preferences);

        alertDialog.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = URLText.getText().toString();
                        String name = NameText.getText().toString();

                        String MimeType = MIMEType.getText().toString();
                        String postParams = POSTParams.getText().toString();
                        //set the URL to a SharedPreference
                        SharedPreferences sharedpreferences = getSharedPreferences(sprefs, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        if(url != null && !url.isEmpty()) {
                            editor.putString(prefsfornumber, url);
                            editor.commit();
                        }
                        if(MimeType != null && !MimeType.isEmpty()) {
                            editor.putString(prefsformimetype, MimeType);
                            editor.commit();
                        }
                        if(postParams != null && !postParams.isEmpty()) {
                            editor.putString(prefsforpostdata, postParams);
                            editor.commit();
                        }
                        if(GetPostBox != null && GetPostBox.isChecked()) {
                            editor.putBoolean(prefsforgetpost, true);
                            editor.commit();
                        }
                        else{
                            editor.putBoolean(prefsforgetpost, false);
                            editor.commit();
                        }
                        //set the NAME to a SharedPreference
                        if(name != null && !name.isEmpty()) {
                            editor.putString(prefsforname, name);
                            editor.commit();
                            int cmdBtn = HTTPConfig.this.getResources().getIdentifier("command"+number, "id", HTTPConfig.this.getPackageName());
                            Button CommandBtn = (Button)findViewById(cmdBtn);
                            CommandBtn.setText(name);
                            if(number.equals("1")){
                                editor.putBoolean("name_1_set", true);
                                editor.commit();
                                sendName("1",name);
                            }
                            if(number.equals("2")){
                                editor.putBoolean("name_2_set", true);
                                editor.commit();
                                sendName("2",name);

                            }
                            if(number.equals("3")){
                                editor.putBoolean("name_3_set", true);
                                editor.commit();
                                sendName("3",name);

                            }
                            if(number.equals("4")){
                                editor.putBoolean("name_4_set", true);
                                editor.commit();
                                sendName("4",name);

                            }
                        }

                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }



    //Button HTTP requests:

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

    private void sendName(String number, String name) {
        String command = "/name"+number;
        if (wearNode != null && wearGoogleApiClient!=null && wearGoogleApiClient.isConnected()) {

            Wearable.MessageApi.sendMessage(
                    wearGoogleApiClient, wearNode.getId(), command,name.getBytes()).setResultCallback(

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }else{
            Toast.makeText(getApplicationContext(),
                    "No connection to phone", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),
                    "Connect watch to phone!", Toast.LENGTH_SHORT).show();

        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            wearGoogleApiClient.connect();
        }
    }

    /*
    * Resolve the node = the connected device to send the message to
    */
    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(wearGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    wearNode = node;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        resolveNode();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


