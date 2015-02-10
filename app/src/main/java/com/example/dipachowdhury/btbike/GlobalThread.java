package com.example.dipachowdhury.btbike;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by DipaChowdhury on 2/9/15.
 */
public class GlobalThread extends Application{

    private ConnectedThread mConnectedThread;

    private BluetoothSocket btSocket;

    private Handler BluetoothIn;

    // Methods for getting and setting the thread
    public ConnectedThread getmConnectedThread(){
        return mConnectedThread;
    }

    public void setmConnectedThread(ConnectedThread t){
        this.mConnectedThread = t;
    }

    // Methods for getting and setting the btSocket
    public BluetoothSocket getBtSocket(){
        return btSocket;
    }

    public void setBtSocket(BluetoothSocket b){
        this.btSocket = b;
    }

    public Handler getBluetoothIn(){
        return BluetoothIn;
    }

    public void setBluetoothIn(Handler b){
        this.BluetoothIn = b;
    }
}
