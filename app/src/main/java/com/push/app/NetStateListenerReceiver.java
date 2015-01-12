package com.push.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetStateListenerReceiver extends BroadcastReceiver {
    public NetStateListenerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        String action = intent.getAction();

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d("push", "网络状态已经改变");
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isAvailable()) {
                String name = info.getTypeName();
                Log.d("push", "当前网络名称：" + name);
                Intent serviceIntent = new Intent(context,PushClientService.class);
                context.startService(serviceIntent);
            } else {
                Log.d("push", "没有可用网络");
            }
        }
    }
}
