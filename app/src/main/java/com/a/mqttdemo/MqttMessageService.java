package com.a.mqttdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttMessageService extends Service {

    private static final String TAG = "MqttMessageService";
    private PahoMqttClient pahoMqttClient;
    private MqttAndroidClient mqttAndroidClient;

    private static MyInterface myInterface;

    public MqttMessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        pahoMqttClient = new PahoMqttClient();
        mqttAndroidClient = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                //setMessageNotification(s, new String(mqttMessage.getPayload()));
                Log.d(TAG, "messageArrived: ");
                String msg = new String(mqttMessage.getPayload(),"ISO-8859-1");
                byte b[] = mqttMessage.getPayload();
                Log.d(TAG, "messageArrived: 0 "+(char)b[0]);
                Log.d(TAG, "messageArrived: 1 "+(int)b[1]);
                Log.d(TAG, "messageArrived: 2 "+(char)b[2]);
                Log.d(TAG, "messageArrived: 3 "+(int)b[3]);
                Log.d(TAG, "messageArrived: 4 "+(int)b[4]);
                Log.d(TAG, "messageArrived: 5 "+String.format("%02x",b[5]));
                Log.d(TAG, "messageArrived: 6 "+String.format("%02x",b[6]));
                Log.d(TAG, "messageArrived: 7 "+String.format("%02x",b[7]));
                Log.d(TAG, "messageArrived: 8 "+String.format("%02x",b[8]));
                Log.d(TAG, "messageArrived: 9 "+String.format("%02x",b[9]));
                Log.d(TAG, "messageArrived: 10 "+String.format("%02x",b[10]));
                Log.d(TAG, "messageArrived: 11 "+(char)b[11]);
                Log.d(TAG, "messageArrived: 12 "+(char)b[12]);
                Log.d(TAG, "messageArrived: 13 "+(char)b[13]);
                Log.d(TAG, "messageArrived: 14 "+(char)b[14]);
                Log.d(TAG, "messageArrived: 15 "+(char)b[15]);
                Log.d(TAG, "messageArrived: 16 "+(char)b[16]);
                Log.d(TAG, "messageArrived: 17 "+(char)b[17]);

                myInterface.dataAvailable(b);
                getStringFromHex(msg.substring(5,11));
                //Log.d(TAG, "messageArrived: 18 "+getStringFromHex(b,5,11));

               // String ss = (new String(b, "ISO-8859-1")).replaceAll("^\\x00*", "");
                //Log.d(TAG, "messageArrived ss: "+d(ss));
                //processAvailableDevicesPacket(b);
                /*StringBuffer sb = new StringBuffer();
                Log.d(TAG, "messageArrived: "+msg);
                for (int i = 0; i < msg.length(); i++) {
                    Log.d(TAG, "setMessageNotification: " + String.format("%02x", msg.charAt(i)));
                    //sb.append(String.format("%02x", msg.charAt(i)));
                }*/

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void setMessageNotification(@NonNull String topic, @NonNull String msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_message_black_24dp)
                        .setContentTitle(topic)
                        .setContentText(msg);
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
    }

    private void processAvailableDevicesPacket(byte packet[]){
       // Log.d(TAG, "Received Packet "+packet + "Packet Length"+packet.length());
        if(packet[3] >= 6) {
            //int len=Utils.parseInt(packet, RESPONSE_PACKET_LENGTH_POS);
            //Log.d(TAG, "Length:"+len);
            try {
                if ((packet[0] == '8')
                        && packet[1] == 1) {
                    if (packet[2] == 'S') {
                        //boolean insertRouterInfo = new DatabaseHandler(mContext).insertRouterInfo(
                        //appContext.getDoor().getId(), ip, port);
                        //Log.d(TAG, "Inserting Router Info:" + insertRouterInfo);
                        //int numberOfAvailableDevices = Character.getNumericValue(packet.charAt(ScanLockPacket.NUMBER_OF_AVAILABLE_DEVICES_POSITION));
                        int numberOfAvailableDevices = packet[4];
                        Log.d(TAG, "numberOfAvailableDevices:"+numberOfAvailableDevices);
                        if(numberOfAvailableDevices>0){
                            int doorIdIndex=5;
                            int doorNameIndex=11;
                           // routerConfiguredDoors.clear();
                           // doorNames.clear();
                            for(int i=0;i<numberOfAvailableDevices;
                                i++, doorIdIndex+=22,
                                        doorNameIndex=doorIdIndex+6){
                               // String doorId=getStringFromHex(packet,doorIdIndex, doorNameIndex);
                                //String doorName=packet.substring(doorNameIndex/*, doorIdIndex+22*/);
                                //Log.d(TAG, "processAvailableDevicesPacket: doorId :"+doorId+" doorName:"+ doorName);

                            }
                        }
                        else {
                            Toast.makeText(this, "devices not found", Toast.LENGTH_LONG).show();
                        }

                    }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    public static int parseInt(String bytes, int index)
    {
        return (bytes.charAt(index) & 0xFF);
    }
    public static String getStringFromHex(String in)
    {
        String out = null;
        char [] ref = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte [] strBytes;
        try {
            strBytes = in.getBytes("ISO-8859-1");

            Log.d(TAG, "getStringFromHex: 5 "+String.format("%02x",strBytes[5]));
            Log.d(TAG, "getStringFromHex: 6 "+String.format("%02x",strBytes[6]));
            Log.d(TAG, "messageArrived: 7 "+String.format("%02x",strBytes[7]));
            Log.d(TAG, "messageArrived: 8 "+String.format("%02x",strBytes[8]));
            Log.d(TAG, "messageArrived: 9 "+String.format("%02x",strBytes[9]));
            Log.d(TAG, "messageArrived: 10 "+String.format("%02x",strBytes[10]));

            char [] outCh = new char[strBytes.length << 1];
            for(int i = 0; i < strBytes.length; i ++) {
                outCh[(i << 1)] = ref[((strBytes[i]&0xff >> 4) & 0x0F)];
                outCh[(i << 1) + 1] = ref[(strBytes[i] & 0x0F)];
            }
            out = new String(outCh);
        } catch(java.io.UnsupportedEncodingException e) {
            Log.d("Utils", "Unsupported String Decoding Exception");
        }
        return out;
    }

    private String d(String in){
        String out = null;
        char [] ref = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte [] strBytes;
        try {
            strBytes = in.getBytes("ISO-8859-1");
            char [] outCh = new char[strBytes.length << 1];
            for(int i = 0; i < strBytes.length; i ++) {
                outCh[(i << 1)] = ref[((strBytes[i] >> 4) & 0x0F)];
                outCh[(i << 1) + 1] = ref[(strBytes[i] & 0x0F)];
            }
            out = new String(outCh);
        } catch(java.io.UnsupportedEncodingException e) {
            Log.d("Utils", "Unsupported String Decoding Exception");
        }
        return out;
    }

    public static void setInterfase(MyInterface interfase){
        myInterface = interfase;
    }
}
