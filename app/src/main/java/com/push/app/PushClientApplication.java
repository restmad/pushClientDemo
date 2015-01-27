package com.push.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by xiaominfc on 1/27/15.
 */
public class PushClientApplication extends Application {

    private BroadcastReceiver receiver;

    public void onCreate() {
        super.onCreate();
        initContent();
    }


    private void initContent() {
        if(receiver == null) {
            receiver = new ScreenStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        this.registerReceiver(receiver,filter);
    }
}
