package com.a.mqttdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MqttBroadCastReciver extends BroadcastReceiver {

    private MqttReceiveListener mqttReceiveListener;
    private static final String TAG = MqttBroadCastReciver.class.getSimpleName();

    public MqttBroadCastReciver(Context context, MqttReceiveListener mqttReceiveListener)
    {
        //this.mContext=context;
        this.mqttReceiveListener=mqttReceiveListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case MyService.MQTT_CONNECTED:
                mqttReceiveListener.onConnect();
                break;
            case MyService.MQTT_DISCONNECTED:
                mqttReceiveListener.onDisconnect();
                break;
            case MyService.MQTT_DATA_AVAILABLE:
                final byte[] data = intent.getByteArrayExtra(MyService.EXTRA_DATA);
                mqttReceiveListener.onDataAvailable(data);
                break;
            case MyService.MQTT_DELEVIERY_COMPLETED:
                mqttReceiveListener.onDelivery();
                break;
            case MyService.MQTT_UNABLE_TO_PUBLISH:
                mqttReceiveListener.unableToPublish();
                break;
            case MyService.MQTT_UNABLE_TO_SUBSCRIBE:
                mqttReceiveListener.unableToSubscribe();
                break;
            case MyService.MQTT_USUBSCRIBE:
                mqttReceiveListener.subscribed();
                break;
        }
    }
}
