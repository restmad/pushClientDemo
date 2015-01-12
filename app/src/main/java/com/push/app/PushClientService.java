package com.push.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class PushClientService extends Service {

    private final static String BASESERVERURL = "http://192.168.1.114:3000";

    Socket socket = null;


    public PushClientService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("start service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(socket == null) {
            initClientSocket();
            bindPushEvent(new CallBack() {
                @Override
                public void callback(String content) {
                    System.out.println(content);
                    showNotification(content);
                }
            });
        }

        if(!socket.connected()) {
            socket.connect();
        }
    }

    private void bindPushEvent(CallBack callBack){
        bindListenerForEvent("message",new PushEventListener(callBack));
    }

    public void bindListenerForEvent(String event,Emitter.Listener listener) {
        if(null != listener && null != socket) {
            socket.on(event,listener);
        }
    }

    private void register(){
        if(null != socket) {

            JSONObject msg = new JSONObject();
            try {
                msg.put("uname","xiaominfc");
                msg.put("pword","123456");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("register", msg.toString());
        }
    }

    public String getIdentification(){
        String  imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        return  imei;
    }

    private void bindBaseEventForSocket(){

        bindListenerForEvent(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                register();
            }
        });

        bindListenerForEvent(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("disconnect");
            }
        });

        bindListenerForEvent(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0].toString());
            }
        });

        bindListenerForEvent(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0].toString());
            }
        });

    }

    private void initClientSocket(){
        try {
            //socket = IO.socket("https://cloudchat-xiaominfc.c9.io");
            socket = IO.socket(BASESERVERURL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        bindBaseEventForSocket();
    }


    private void showNotification(String message) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification =new Notification(R.drawable.ic_launcher,
                "督导系统", System.currentTimeMillis());
        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
        //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
        //FLAG_ONGOING_EVENT 通知放置在正在运行
        //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
        //DEFAULT_LIGHTS  使用默认闪光提示
        //DEFAULT_SOUNDS  使用默认提示声音
        //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
        notification.defaults = Notification.DEFAULT_LIGHTS;
        //叠加效果常量
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS = 5000; //闪光时间，毫秒

        // 设置通知的事件消息
        CharSequence contentTitle = "新消息"; // 通知栏标题
        CharSequence contentText = message; // 通知栏内容
        Intent notificationIntent = new Intent(PushClientService.this, PushClientActivity.class); // 点击该通知后要跳转的Activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, contentTitle, contentText, contentItent);

        // 把Notification传递给NotificationManager
        notificationManager.notify(0, notification);
    }

    public class PushEventListener implements Emitter.Listener{

        private CallBack mCallback;

        public PushEventListener(CallBack callBack){
            mCallback = callBack;
        }


        @Override
        public void call(Object... args) {
            if(null != mCallback && null != args && args.length > 0) {
                mCallback.callback(args[0].toString());
            }
        }
    }

    public interface  CallBack {
        public void callback(String content);
    }
}
