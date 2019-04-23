package com.a.mqttdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    public MqttDataSendListener dataSendListener;
    public static final String TAG = Main2Activity.class.getSimpleName();
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        dataSendListener = Utils.getDataSendListener();
        send = findViewById(R.id.send);
    }
    public void send(View view){
        dataSendListener.sendData(new byte[]{1, 5,8}, "ji", "jj", new MyInterface() {
            @Override
            public void dataAvailable(byte[] data) {
                showToas(data);
            }
        });
    }

    private void showToas(byte[] data){
        //Toast.makeText(Main2Activity.this, data[0]+data[1]+data[2], Toast.LENGTH_SHORT).show();
        String s = new String(data);
        Log.d(TAG, "showToas: "+s);
        //Log.d(TAG, "dataAvailable: "+data[0]+data[1]+data[2]);
        send.setText("hi");
    }
}
