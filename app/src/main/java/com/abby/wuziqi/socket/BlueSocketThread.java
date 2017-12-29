package com.abby.wuziqi.socket;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;

import com.abby.wuziqi.game.BlueActivity;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by keybo on 2017/12/27 0027.
 */

public class BlueSocketThread extends Thread {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private Activity blueConnectActivity;
    private String address;
    private boolean isConnecting;

    public BlueSocketThread(BluetoothAdapter bluetoothAdapter, BluetoothSocket thisSocket, Activity bleConnectActivity, String adress) {
        this.blueConnectActivity = bleConnectActivity;
        this.bluetoothAdapter = bluetoothAdapter;
        this.socket = thisSocket;
        this.address = adress;
        this.isConnecting = true;
    }

    public void run(){
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        DataInputStream dis = null;
        try{
            socket.connect();//发起连接
            if (!bluetoothAdapter.isEnabled()){
                //若未开启蓝牙 则打开
                bluetoothAdapter.enable();
            }
            blueConnectActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(blueConnectActivity,"正在等待对方接收请求...",Toast.LENGTH_SHORT).show();
                }
            });
            while (isConnecting){
                //连接成功
                dis = new DataInputStream(socket.getInputStream());
                String result = dis.readUTF();
                if ("accept".equals(result)){
                    SocketManager.addBlueSocketHm(address,socket);
                    Intent intent = new Intent(blueConnectActivity, BlueActivity.class);
                    intent.putExtra("address",address);
                    intent.putExtra("isStart",true);
                    blueConnectActivity.startActivity(intent);
                    break;
                }else {
                    blueConnectActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //ui线程发送提示
                            Toast.makeText(blueConnectActivity,"对方退缩了！",Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cancel(){
        isConnecting = false;
        try {
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
