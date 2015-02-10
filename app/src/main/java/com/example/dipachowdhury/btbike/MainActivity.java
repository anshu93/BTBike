package com.example.dipachowdhury.btbike;

import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


/* This is the launching page of the app. From here it is necessary to first connect
    to the bike before being able to do anything else. This Activity is listening for a Data Intent
    which means a thread running in the background which is trying to retrieve data from the Arduino
    has succeeded. The GlobalThread is just to store a bunch of globals so that it is easy to
    check on the state of the connection. The data should also be stored there. BTDownload is
    a thread that runs to collect data from the Arduino. It is run in the background because it
    is extremely time intensive
 */

public class MainActivity extends ActionBarActivity {

    //Initialize 4 main elements
    Button btConnect;
    Button btMap;
    Button btDisplayData;
    TextView tvStatus;

    // IDS for when the ConnectActivity returns
    int CONNECT_STATUS = 0;
    int CONNECT_SUCCESS = 1;

    BluetoothSocket btSocket;

    String data;        // Should be a global of the data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a listener for the data. The intent is called "GotData"
        LocalBroadcastManager.getInstance(this).registerReceiver(DataReceiver, new IntentFilter("GotData"));

        initialize();

    }

    public void onResume(){
        super.onResume();

        // Retrieve any existing sockets
        btSocket = ((GlobalThread) this.getApplication()).getBtSocket();
        if(btSocket != null) {
            if (!btSocket.isConnected()) {
                initialize();
            }
        }else{
            initialize();
        }
    }

    private BroadcastReceiver DataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            tvStatus.setText("Received Data: " + data);

            // Make buttons visible
            btMap.setClickable(true);
            btMap.setVisibility(View.VISIBLE);
            btDisplayData.setClickable(true);
            btDisplayData.setVisibility(View.VISIBLE);
        }
    };

    private void initialize() {

        // Set buttons up
        btConnect = (Button) findViewById(R.id.btConnect);
        btMap = (Button) findViewById(R.id.btMap);
        btMap.setClickable(false);
        btMap.setVisibility(View.INVISIBLE);

        btDisplayData = (Button) findViewById(R.id.btRideData);
        btDisplayData.setClickable(false);
        btDisplayData.setVisibility(View.INVISIBLE);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvStatus.setText("Please Connect to Bike...");

        // On click listener for the initial connect button
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ConnectIntent = new Intent(MainActivity.this, ConnectActivity.class);
                startActivityForResult(ConnectIntent, CONNECT_STATUS);
            }
        });

        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MapIntent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(MapIntent);
            }
        });

    }

    // Called when returning from the connections page
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONNECT_STATUS){
            if(resultCode == CONNECT_SUCCESS){
                Toast.makeText(getBaseContext(), "Connection Successful", Toast.LENGTH_SHORT).show();
                tvStatus.setText("Gathering Data...");
            }else{
                Toast.makeText(getBaseContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Close the bluetooth connection when the app is closed
    public void onDestroy(){
        super.onDestroy();
        btSocket = ((GlobalThread) this.getApplication()).getBtSocket();
        if(btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Could not close connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
