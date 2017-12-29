package com.abby.wuziqi.socket;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.support.v4.app.ActivityCompat;

import com.abby.wuziqi.view.BlueGameView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by keybo on 2017/12/27 0027.
 */

public class ConnectThread extends Thread {
    private final BluetoothSocket socket;
    private BlueGameView gameView;
    private boolean isMe = false;

    public ConnectThread(BluetoothSocket socket,BlueGameView gameView,boolean isMe){
        this.socket =socket;
        this.gameView=gameView;
        this.isMe=isMe;
    }

    @Override
    public void run() {
        while (true){
            if (socket!=null){
                DataInputStream dis = null;
                String data = null;
                try{
                    dis = new DataInputStream(socket.getInputStream());
                    data=dis.readUTF();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if (data!=null){
                    final String finalData = data;
                    gameView.post(new Runnable() {
                        @Override
                        public void run() {
                            gameView.getCommand(finalData);
                        }
                    });
                }else {
                    Activity activity = (Activity) gameView.getContext();
                    activity.finish();
                    break;
                }
            }
        }
    }

    public void write(byte[] bytes){
        DataOutputStream dos = null;
        try{
            dos = new DataOutputStream(socket.getOutputStream());
            String temp = new String(bytes,"utf-8");
            dos.writeUTF(temp);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void cancel(){
        try {
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
