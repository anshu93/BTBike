package com.example.dipachowdhury.btbike;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;




public class ConnectedThread extends Thread{
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    public Handler BluetoothIn;
    //creation of the connect thread
    public ConnectedThread(BluetoothSocket socket, Handler btin) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        BluetoothIn = btin;

        try {
            //Create I/O streams for connection
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmOutStream = tmpOut;
        mmInStream = tmpIn;

    }

    public void run() {
        byte[] buffer = new byte[2048];
        int bytes;

        // Keep looping to listen for received messages
        while (true) {
            try {
                bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);

                // Send the obtained bytes to the UI Activity via handler
                BluetoothIn.obtainMessage(0, bytes, -1, readMessage).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    public String GetData() {
        byte[] buffer = new byte[2048];
        int bytes;

        // Keep looping to listen for received messages
        while (true) {
            try {
                bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                return readMessage;
            } catch (IOException e) {
                break;
            }
        }
        return null;
    }

    //write method
    public void write(String input) {
        byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
        } catch (IOException e) {
            //if you cannot write, close the application
            BluetoothIn.obtainMessage(0,1,-1,"failed").sendToTarget();
        }
    }
}