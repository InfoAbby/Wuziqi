package com.abby.wuziqi.socket;

import android.bluetooth.BluetoothSocket;

import java.util.HashMap;

/**
 * Created by keybo on 2017/12/27 0027.
 */

public class SocketManager {
    //服务端
    public static HashMap<String,BlueServerThread> serverHm = new HashMap<>();
    //客户端
    public static HashMap<String,BlueSocketThread> socketHm = new HashMap<>();
    public static HashMap<String,BluetoothSocket>   blueSocketHm = new HashMap<>();

    public static BlueServerThread getServer(String address) {
        return serverHm.get(address);
    }
    public static BlueSocketThread getSocket(String address){
        return socketHm.get(address);
    }

    public static BluetoothSocket getBlueSocket(String address){
        return blueSocketHm.get(address);
    }

    public static void addServerHm(String address,BlueServerThread blueServerThread){
        serverHm.put(address,blueServerThread);
    }
    public static void addSocketHm(String address,BlueSocketThread blueSocketThread){
        socketHm.put(address,blueSocketThread);
    }
    public static void addBlueSocketHm(String address,BluetoothSocket bluetoothSocket){
        blueSocketHm.put(address,bluetoothSocket);
    }
}
