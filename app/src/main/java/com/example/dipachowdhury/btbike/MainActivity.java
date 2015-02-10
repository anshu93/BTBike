package com.example.dipachowdhury.btbike;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    //Initialize 4 main buttons
    Button btConnect;
    Button btCollectData;
    Button btMap;
    Button btDisplayData;

    int CONNECT_STATUS = 0;
    int CONNECT_SUCCESS = 1;
    int CONNECT_FAILURE = 2;

    int DATA_STATUS = 3;
    int DATA_SUCCESS = 4;

    BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {

        btConnect = (Button) findViewById(R.id.btConnect);
        //btCollectData = (Button) findViewById(R.id.btCollectData);
        btMap = (Button) findViewById(R.id.btMap);
        btDisplayData = (Button) findViewById(R.id.btRideData);

        // On click listener for the initial connect button
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ConnectIntent = new Intent(MainActivity.this, ConnectActivity.class);
                startActivityForResult(ConnectIntent, CONNECT_STATUS);
            }
        });

//        btCollectData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent CollectDataIntent = new Intent(MainActivity.this, CollectDataActivity.class);
//                startActivityForResult(CollectDataIntent, DATA_STATUS);
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONNECT_STATUS){
            if(resultCode == CONNECT_SUCCESS){
                Toast.makeText(getBaseContext(), "Connection Successful", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getBaseContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == DATA_STATUS){
            if(resultCode == DATA_SUCCESS){
                Toast.makeText(getBaseContext(), "Download Complete", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onPause(){
        super.onPause();
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
