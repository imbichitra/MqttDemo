package com.a.mqttdemo;

public interface MqttDataSendListener {
    void sendData(byte []data,String subscribeTopic,String publishTopic,MyInterface myInterface);
}
