package com.example.dipachowdhury.btbike;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by DipaChowdhury on 2/10/15.
 * This class simply polls the Arduino for data and returns it to the main Activity when done
 */
public class BTDownload extends IntentService {

    ConnectedThread mConnectedThread;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *   Used to name the worker thread, important only for debugging.
     */
    public BTDownload() {
        super("BTDownload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mConnectedThread = ((GlobalThread) this.getApplication()).getmConnectedThread();

        // Get the data from the connection thread
        String data = mConnectedThread.GetData();

        // Start a new intent "GotData" and broadcast it
        Intent i = new Intent("GotData").putExtra("data", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}
