package com.a.mqttdemo;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyService extends Service {
    public static final String TAG = MyService.class.getSimpleName();
    public static final String EXTRA_DATA = "android.nfc.extra.DATA";
    public static final String MQTT_CONNECTED = "android.nfc.extra.MQTT_CONNECTED";
    public static final String MQTT_DISCONNECTED = "android.nfc.extra.MQTT_DISCONNECTED";
    public static final String MQTT_DATA_AVAILABLE = "android.nfc.extra.MQTT_DATA_AVAILABLE";
    public static final String MQTT_DELEVIERY_COMPLETED = "android.nfc.extra.MQTT_DELEVIERY_COMPLETED";
    public static final String MQTT_UNABLE_TO_PUBLISH = "android.nfc.extra.MQTT_UNABLE_TO_PUBLISH";
    public static final String MQTT_UNABLE_TO_SUBSCRIBE = "android.nfc.extra.MQTT_UNABLE_TO_SUBSCRIBE";
    public static final String MQTT_USUBSCRIBE = "android.nfc.extra.MQTT_USUBSCRIBE";

    private PahoMqttClient pahoMqttClient;
    private MqttAndroidClient mqttAndroidClient;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        pahoMqttClient = new PahoMqttClient();
        mqttAndroidClient = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                broadcastUpdate(MyService.MQTT_CONNECTED);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                broadcastUpdate(MyService.MQTT_DISCONNECTED);
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                //setMessageNotification(s, new String(mqttMessage.getPayload()));
                Log.d(TAG, "messageArrived: ");
                String msg = new String(mqttMessage.getPayload(),"ISO-8859-1");
                byte b[] = mqttMessage.getPayload();
                broadcastUpdate(MyService.MQTT_DATA_AVAILABLE,b);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
    private final IBinder mBinder = new LocalService();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        //close();
        return super.onUnbind(intent);
    }
    public class LocalService extends Binder{
        public MyService getService(){return  MyService.this;}
    }

    public void subcribe(){
        try {
            //pahoMqttClient.subscribe(mqttAndroidClient, "BridgeId_SCAN_RESPONSE", 0);
            IMqttToken token = mqttAndroidClient.subscribe("hi", 0);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.d(TAG, "Subscribe Successfully " + "BridgeId_SCAN_RESPONSE");
                    broadcastUpdate(MyService.MQTT_USUBSCRIBE);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.e(TAG, "Subscribe Failed " + "BridgeId_SCAN_RESPONSE");
                    broadcastUpdate(MyService.MQTT_UNABLE_TO_SUBSCRIBE);
                }
            });
        } catch (MqttException e) {
            broadcastUpdate(MyService.MQTT_UNABLE_TO_SUBSCRIBE);
            e.printStackTrace();
        }

    }
    public void publish(byte payload[]){
        MqttMessage message = new MqttMessage(payload);
        message.setId(320);
        message.setRetained(true);
        message.setQos(0);
        try {
            mqttAndroidClient.publish("BridgeId", message);
        } catch (MqttException e) {
            e.printStackTrace();
            broadcastUpdate(MyService.MQTT_UNABLE_TO_PUBLISH);
        }
    }

    public void dataAvailable(byte b[]){
        broadcastUpdate(MyService.MQTT_DATA_AVAILABLE,b);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,byte[] data) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
