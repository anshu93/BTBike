package com.example.dipachowdhury.btbike;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;


public class CollectDataActivity extends ActionBarActivity {

    int DATA_SUCCESS = 4;

    private ConnectedThread mConnectedThread;
    Handler BluetoothIn;

    final int handlerState = 0;

    private StringBuilder recDataString = new StringBuilder();

    TextView rec_data;

    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;

    public String receiver_addr = null;      // Addr for the receiver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        initialize();

    }

    public void onResume(){
        super.onResume();


    }

    private void initialize(){

        // Set up the handler

        BluetoothIn = new Handler(){
            public void handleMessage(Message msg){
                if (msg.what == handlerState) {										//if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread

                    if(readMessage.equals("failed")){
                        Toast.makeText(getBaseContext(),"Could not write data to destination", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    recDataString.append(readMessage);      								//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("\n");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        rec_data.setText(dataInPrint);
                        recDataString.delete(0, recDataString.length()); 					//clear all string data
                    }
                }
            }
        };

        ((GlobalThread) this.getApplication()).setBluetoothIn(BluetoothIn);
//
//        btSocket = ((GlobalThread) this.getApplication()).getBtSocket();
//
//        // If not previously connected
//        if(btSocket == null){
//            Intent i = new Intent(CollectDataActivity.this, MainActivity.class);
//            Toast.makeText(getBaseContext(), "Please Connect first", Toast.LENGTH_LONG).show();
//            startActivity(i);
//        }

        // Create a new thread with the current handler
//        mConnectedThread = new ConnectedThread(btSocket, BluetoothIn);
//
//         // Turn any previously running threads off
//        ConnectedThread altThread = ((GlobalThread) this.getApplication()).getmConnectedThread();
//        altThread.interrupt();
//
//        mConnectedThread.start();

        // Test the connection
        mConnectedThread = ((GlobalThread) this.getApplication()).getmConnectedThread();
        mConnectedThread.write("t");

        // If here the connection is alive
        Toast.makeText(getBaseContext(), "Test Successful, Starting Download", Toast.LENGTH_SHORT).show();
        Intent i = getIntent();
        setResult(DATA_SUCCESS, i);
        finish();
    }
}
