package com.example.dipachowdhury.btbike;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ListActivity;


import java.util.Set;


public class MainActivity extends Activity {

    public static String PAIRED_DEVICE_ADDR;

    TextView text_conn_status;
    ListView paired_list;

    private BluetoothAdapter btAdapter;                     // BTadapter for connection
    private ArrayAdapter<String> paired_list_adapter;       // Adapter for list of paired devices


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                String name = ((TextView) view).getText().toString();
                String addr = name.substring(name.length() - 17);   // MAC address is last 17 chars of name
                Intent SendActivity = new Intent(MainActivity.this, SendMain.class);
                SendActivity.putExtra(PAIRED_DEVICE_ADDR, addr);
                startActivity(SendActivity);
            }
        });
    }

    public void onResume(){
        super.onResume();

        paired_list_adapter.clear();

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // Get the status

        checkBluetoothState();          // Check the status and start throwing exceptions

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
//                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBT,1);
                Toast.makeText(getBaseContext(), "Please turn on Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }
}
