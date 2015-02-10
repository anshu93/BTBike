package com.example.dipachowdhury.btbike;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class ConnectActivity extends Activity {

    public static String PAIRED_DEVICE_ADDR; // MAC address of device chosen

    TextView text_conn_status;  // Initialize TextView
    ListView paired_list;   // Initialize ListView

    BluetoothSocket btSocket;

    private BluetoothAdapter btAdapter;                     // BTadapter for connection
    private ArrayAdapter<String> paired_list_adapter;       // Adapter for list of paired devices

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public Handler BluetoothIn;
    private StringBuilder recDataString = new StringBuilder();

    // Intent IDS that will be returned to the Main Activity
    int CONNECT_SUCCESS = 1;
    int CONNECT_FAILURE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view to be the 4 buttons
        setContentView(R.layout.activity_main_connect);

        initialize();

    }

    private void initialize(){
        text_conn_status = (TextView) findViewById(R.id.connecting);

        paired_list_adapter = new ArrayAdapter<String>(this, R.layout.device_name);     // Initialize the adapter for the paired device list
        paired_list = (ListView) findViewById(R.id.paired_devices);
        paired_list.setAdapter(paired_list_adapter);                // Pair the device list and the adapter

        paired_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                text_conn_status.setText("Trying to Connect...");

                // Convert the item into a string and use its MAC address
                String name = ((TextView) view).getText().toString();
                PAIRED_DEVICE_ADDR = name.substring(name.length() - 17);   // MAC address is last 17 chars of name
                try {
                    OpenConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void OpenConnection() throws IOException {
        // Check and close any previous open sockets
        BluetoothSocket previous = ((GlobalThread) this.getApplication()).getBtSocket();
        if(previous != null) {
            previous.close();
        }

        BluetoothDevice receiver = btAdapter.getRemoteDevice(PAIRED_DEVICE_ADDR);


        //Create socket
        try{
            btSocket = createBlueToothSocket(receiver);
        }catch (IOException e) {
            Toast.makeText(getBaseContext(),"Socket Creation Failed", Toast.LENGTH_LONG).show();
        }
        try{
            btSocket.connect();
            ((GlobalThread) this.getApplication()).setBtSocket(btSocket);
        } catch (IOException e) {
            try{
                btSocket.close();
            } catch (IOException e1) {
                Toast.makeText(getBaseContext(),"Could not close socket", Toast.LENGTH_LONG).show();
            }
        }

        // This is a handler that the thread would return any failure messages to
        BluetoothIn = new Handler(){
            public void handleMessage(Message msg){
                if (msg.what == 0) {										//if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread

                    if(readMessage.equals("failed") || readMessage == null){
                        //Toast.makeText(getBaseContext(),"Could not write data to destination", Toast.LENGTH_LONG).show();
                        Intent intent = getIntent();
                        setResult(CONNECT_FAILURE, intent);
                        Toast.makeText(getBaseContext(), "Connection Failed", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    recDataString.append(readMessage);      								//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("\n");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        recDataString.delete(0, recDataString.length()); 					//clear all string data
                    }
                }
            }
        };

        // Set the global handler
        ((GlobalThread) this.getApplication()).setBluetoothIn(BluetoothIn);

        ConnectedThread mConnectedThread = new ConnectedThread(btSocket, BluetoothIn);
        mConnectedThread.start();

        // Set the global thread
        ((GlobalThread) this.getApplication()).setmConnectedThread(mConnectedThread);

        // Write garbage to the Arduino just to check that the connection is good
        mConnectedThread.write("x");

        // If here, the app successfully sent stuff

        // Launch the data gatherer
        Intent DataIntent = new Intent(ConnectActivity.this, BTDownload.class);
        ConnectActivity.this.startService(DataIntent);

        // Go back to main screen
        Intent intent = getIntent();
        setResult(CONNECT_SUCCESS, intent);
        finish();
    }

    private BluetoothSocket createBlueToothSocket(BluetoothDevice d)throws IOException{
        return d.createRfcommSocketToServiceRecord(MY_UUID);
    }

    public void onResume(){
        super.onResume();

        paired_list_adapter.clear();

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // Get the status

        checkBluetoothState();  // Check the status and start throwing exceptions

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for(BluetoothDevice device : pairedDevices){
                paired_list_adapter.add(device.getName() + "\n" + device.getAddress());
            }
        }else{
            paired_list_adapter.add("No paired devices found");
        }
    }

    private void checkBluetoothState() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(getBaseContext(), "Device does not have bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            if(!btAdapter.isEnabled()){
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT,1);
                Toast.makeText(getBaseContext(), "Please turn on Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }
}
