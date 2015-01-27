package com.push.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenStateReceiver extends BroadcastReceiver {
    public ScreenStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();

        if(Intent.ACTION_SCREEN_ON.equals(action)) {
            Intent serviceIntent = new Intent(context,PushClientService.class);
            context.startService(serviceIntent);
        }else {

        }
    }
}
