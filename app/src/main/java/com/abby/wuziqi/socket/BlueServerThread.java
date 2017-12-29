package com.abby.wuziqi.socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.abby.wuziqi.bean.BlueTooth;
import com.abby.wuziqi.config.ConfigData;
import com.abby.wuziqi.game.BlueActivity;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 开启服务器子线程
 * Created by keybo on 2017/12/27 0027.
 */

public class BlueServerThread extends Thread{

    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private String name,address;
    private Activity blueConnectActivity;
    private BluetoothAdapter bluetoothAdapter;
    private boolean isConnecting = true;//服务端开放
    private boolean isAccept = false;

    public BlueServerThread(BluetoothSocket socket,String name,String address, Activity blueConnectActivity, BluetoothAdapter bluetoothAdapter, boolean isAccept) {
        this.socket=socket;
        this.name=name;
        this.address=address;
        this.blueConnectActivity = blueConnectActivity;
        this.bluetoothAdapter = bluetoothAdapter;
        this.isAccept = isAccept;
        this.isConnecting=true;
    }

    public void run(){
        DataOutputStream dos=null;
        try {
            //通过调用listenUsingRfcommWithServiceRecord获取BluetoothServerSocket
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, ConfigData.UUID);
            while (isConnecting){

                isAccept = false;
                socket = serverSocket.accept();//监听
                if (bluetoothAdapter.isDiscovering()){
                    //释放资源
                    bluetoothAdapter.cancelDiscovery();
                }
                blueConnectActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(blueConnectActivity)
                                .setTitle("消息")
                                .setMessage("是否接收挑战?")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isAccept = true;
                                        Toast.makeText(blueConnectActivity,"连接成功",Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isAccept=false;
                                    }
                                })
                                .show();
                    }
                });
                while (true){
                    if (isAccept){
                        String result = "accept";
                        dos = new DataOutputStream(socket.getOutputStream());
                        dos.writeUTF(result);
                        SocketManager.addBlueSocketHm(address,socket);
                        Intent intent = new Intent(blueConnectActivity, BlueActivity.class);
                        intent.putExtra("address",address);
                        intent.putExtra("isStart",false);
                        blueConnectActivity.startActivity(intent);
                        break;
                    }else {

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cancel(){
        isConnecting =false;
        try {
            if (serverSocket !=null){
                serverSocket.close();
            }
            if (socket!=null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
