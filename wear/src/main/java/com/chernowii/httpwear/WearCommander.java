package com.chernowii.httpwear;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class WearCommander extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public Button command1;
   public Button command2;
    public Button command3;
    public Button command4;

    Node wearNode;
    GoogleApiClient wearGoogleApiClient;
    private boolean mResolvingError=false;
    String sprefs = "shared_prefs_wear";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_commander);
        wearGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                command1 = (Button) stub.findViewById(R.id.command_wear_1);
                command1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        send("1");
                    }
                });

                command2 = (Button) stub.findViewById(R.id.command_wear_2);
                command2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        send("2");
                    }
                });

                command3 = (Button) stub.findViewById(R.id.command_wear_3);
                command3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        send("3");
                    }
                });

                command4 = (Button) stub.findViewById(R.id.command_wear_4);
                command4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        send("4");
                    }
                });
                SharedPreferences sharedpreferences = getSharedPreferences(sprefs, Context.MODE_PRIVATE);

                command1.setText(sharedpreferences.getString("name1", "Command 1"));
                command2.setText(sharedpreferences.getString("name2", "Command 2"));
                command3.setText(sharedpreferences.getString("name3", "Command 3"));
                command4.setText(sharedpreferences.getString("name4", "Command 4"));

            }
        });
    }
    private void send(String number) {
        String command = "/command"+number;
        if (wearNode != null && wearGoogleApiClient!=null && wearGoogleApiClient.isConnected()) {

            Wearable.MessageApi.sendMessage(
                    wearGoogleApiClient, wearNode.getId(), command,null).setResultCallback(

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