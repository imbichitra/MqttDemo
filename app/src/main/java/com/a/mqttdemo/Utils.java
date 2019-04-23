package com.a.mqttdemo;

public class Utils {
    static MqttDataSendListener dataSendListener;

    public static MqttDataSendListener getDataSendListener() {
        return dataSendListener;
    }

    public static void setDataSendListener(MqttDataSendListener dataSendListener) {
        Utils.dataSendListener = dataSendListener;
    }
}
