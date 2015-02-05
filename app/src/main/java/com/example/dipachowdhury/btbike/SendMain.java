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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class SendMain extends ActionBarActivity {

    private ConnectedThread mConnectedThread;
    Handler BluetoothIn;

    final int handlerState = 0;

    private StringBuilder recDataString = new StringBuilder();

    TextView info;
    private EditText userText;
    TextView rec_data;

    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private OutputStream outStream;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public String receiver_addr = null;      // Addr for the receiver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_main);

        initialize();

        addKeyListener();

        checkBluetoothState();

        BluetoothIn = new Handler(){
            public void handleMessage(Message msg){
                if (msg.what == handlerState) {										//if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);      								//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        rec_data.setText("Data Received = " + dataInPrint);
                        recDataString.delete(0, recDataString.length()); 					//clear all string data
                        dataInPrint = " ";
                    }
                }
            }
        };

    }

    public void onResume(){
        super.onResume();

        // Get MAC addr
        Intent i = getIntent();
        receiver_addr = i.getStringExtra(MainActivity.PAIRED_DEVICE_ADDR);

        //Toast.makeText(getBaseContext(), "The Mac addr is: " + receiver_addr, Toast.LENGTH_SHORT).show();
        // Set ptr to device with its addr
        BluetoothDevice receiver = btAdapter.getRemoteDevice(receiver_addr);


        //Create socket
        try{
            btSocket = createBlueToothSocket(receiver);
        }catch (IOException e) {
            Toast.makeText(getBaseContext(),"Socket Creation Failed", Toast.LENGTH_LONG).show();
        }
        try{
            btSocket.connect();
        } catch (IOException e) {
            try{
                btSocket.close();
            } catch (IOException e1) {
                Toast.makeText(getBaseContext(),"Could not close socket", Toast.LENGTH_LONG).show();
            }
        }

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        mConnectedThread.write("1");
    }

    public void onPause(){
        super.onPause();

        // Close connections to not waste phone's resources
        try {
            btSocket.close();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Could not close connection", Toast.LENGTH_SHORT).show();
        }
    }

    private BluetoothSocket createBlueToothSocket(BluetoothDevice d)throws IOException{
        return d.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void addKeyListener() {
        userText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)){
                    mConnectedThread.write(userText.getText().toString());
                    Toast.makeText(getBaseContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });
    }

    private void initialize(){
        info = (TextView) findViewById(R.id.info_send_text);
        userText = (EditText) findViewById(R.id.send_text);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        rec_data = (TextView) findViewById(R.id.rec_data);
    }

    private void checkBluetoothState() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(getBaseContext(), "Device does not have bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            if(!btAdapter.isEnabled()){
                Toast.makeText(getBaseContext(), "Please turn on Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmOutStream = tmpOut;
            mmInStream = tmpIn;

        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    BluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure, could not write data", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
