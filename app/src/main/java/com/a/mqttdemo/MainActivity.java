package com.a.mqttdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements MqttDataSendListener{

    private MqttAndroidClient client;
    private String TAG = "MainActivity";
    private PahoMqttClient pahoMqttClient;

    private EditText textMessage, subscribeTopic, unSubscribeTopic;
    private Button publishMessage, subscribe, unSubscribe;

    SliderLayout sliderLayout;
    MyService myService;
    boolean isBind = false;
    MyInterface myInterface;

    byte mattSentData[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.setDataSendListener(this);
        pahoMqttClient = new PahoMqttClient();

        textMessage = (EditText) findViewById(R.id.textMessage);
        publishMessage = (Button) findViewById(R.id.publishMessage);

        subscribe = (Button) findViewById(R.id.subscribe);
        unSubscribe = (Button) findViewById(R.id.unSubscribe);

        subscribeTopic = (EditText) findViewById(R.id.subscribeTopic);
        unSubscribeTopic = (EditText) findViewById(R.id.unSubscribeTopic);
        //client = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);

        publishMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String msg = textMessage.getText().toString().trim();
                byte b[]=new byte[4];
                b[0]='0';
                b[1]=4;
                b[2]='S';
                b[3]=0;
                String s=new String(b);
                if (!msg.isEmpty()) {
                    try {
                        pahoMqttClient.publishMessage(client, s, 1, Constants.PUBLISH_TOPIC);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }*/
                myService.subcribe();
            }
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String topic = subscribeTopic.getText().toString().trim();
                if (!topic.isEmpty()) {
                    try {
                        pahoMqttClient.subscribe(client, topic, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }*/
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                //myService.subcribe();
            }
        });
        unSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String topic = unSubscribeTopic.getText().toString().trim();
                if (!topic.isEmpty()) {
                    try {
                        pahoMqttClient.unSubscribe(client, topic);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }*/
                myService.dataAvailable(new byte[]{3,6,8});
            }
        });
        /*MqttMessageService.setInterfase(new MyInterface() {
            @Override
            public void dataAvailable(byte[] data) {
                for (byte aData : data)
                    Log.d(TAG, "dataAvailable: " + String.format("%02X", aData));
            }
        });
        Intent intent = new Intent(MainActivity.this, MqttMessageService.class);
        startService(intent);*/
        bindService();
    }

    private MqttBroadCastReciver mqttBroadCastReciver = new MqttBroadCastReciver(this, new MqttReceiveListener() {
        @Override
        public void onConnect() {
            Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnect() {
            Toast.makeText(MainActivity.this, "disconnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDataAvailable(byte[] data) {
            if (myInterface!=null)
            myInterface.dataAvailable(data);
            Toast.makeText(MainActivity.this, "onDataAvailable"+data[0]+data[1]+data[2], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDelivery() {
            Toast.makeText(MainActivity.this, "data send", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void subscribed() {
            myService.publish(mattSentData);
            Toast.makeText(MainActivity.this, "subscribed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void unableToSubscribe() {
            Toast.makeText(MainActivity.this, "unableToSubscribe", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void unableToPublish() {
            Toast.makeText(MainActivity.this, "unableToPublish", Toast.LENGTH_SHORT).show();
        }
    });
    private void bindService(){
        Intent intent = new Intent(this,MyService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mqttBroadCastReciver,mqttIntentFilet());
    }

    private IntentFilter mqttIntentFilet() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.MQTT_CONNECTED);
        intentFilter.addAction(MyService.MQTT_DISCONNECTED);
        intentFilter.addAction(MyService.MQTT_DATA_AVAILABLE);
        intentFilter.addAction(MyService.MQTT_DELEVIERY_COMPLETED);
        intentFilter.addAction(MyService.MQTT_UNABLE_TO_PUBLISH);
        intentFilter.addAction(MyService.MQTT_UNABLE_TO_SUBSCRIBE);
        intentFilter.addAction(MyService.MQTT_USUBSCRIBE);
        return intentFilter;
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalService localService = (MyService.LocalService) service;
            myService = localService.getService();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind){
            unbindService(mConnection);
            isBind = false;
        }
    }

    @Override
    public void sendData(byte[] data, String subscribeTopic, String publishTopic, MyInterface myInterface) {
        this.myInterface = myInterface;
        myService.subcribe();
        mattSentData = data;
    }
}
