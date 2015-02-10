package com.example.dipachowdhury.btbike;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by DipaChowdhury on 2/10/15.
 */
public class BTDownload extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BTDownload(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
