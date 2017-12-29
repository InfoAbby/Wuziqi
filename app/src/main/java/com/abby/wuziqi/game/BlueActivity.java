package com.abby.wuziqi.game;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.abby.wuziqi.R;
import com.abby.wuziqi.socket.ConnectThread;
import com.abby.wuziqi.socket.SocketManager;
import com.abby.wuziqi.view.BlueGameView;

import java.util.Arrays;
import java.util.List;

/**
 * 连接设备需要同时实现服务端和客户端，其中一台设备必须开放服务器套接字，
 * 另一台设备发起连接，当服务器和客户端在同一RFCOMM通道上分别拥有已连接
 * 的BluetoothSocket时 就是彼此连接
 * Created by keybo on 2017/12/25 0025.
 */

public class BlueActivity extends Activity implements BlueGameView.onBluetoothListener{


    private TextView isOver;
    private BlueGameView gameView;
    private Button returnBtn;
    private Button restartBtn;
    private Button msgBtn;

    private ConnectThread connectThread;
    private BluetoothSocket socket;
    private ListPopupWindow windowCompat;
    private List<String> message;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.blue);
        isOver = findViewById(R.id.hint);
        gameView = findViewById(R.id.gameView);
        returnBtn = findViewById(R.id._return);
        restartBtn = findViewById(R.id.restart);
        msgBtn = findViewById(R.id.msg);
        init();

        //悔棋事件
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "return";
                connectThread.write(command.getBytes());
                gameView.retunrnUp();

            }
        });

        //快捷回复事件
        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowCompat.show();

            }
        });

        //重置游戏事件
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "restart";
                connectThread.write(command.getBytes());
                gameView.restartGame();
            }
        });
    }

    private void init() {
        String[] msg = getResources().getStringArray(R.array.array_message);
        message = Arrays.asList(msg);

        gameView.setTextView(isOver);
        gameView.setCallBack(this);
        //获取信息
        Intent intent = getIntent();
        String address= intent.getStringExtra("address");
        boolean isStart = intent.getBooleanExtra("isStart",false);
        if (address!=null){
            socket = SocketManager.getBlueSocket(address);
            manageClientSocket(isStart);
            gameView.setAddress(address);
            gameView.setIsStart(isStart);
        }
        popupWindow();
    }

    //快捷回复窗口
    private void popupWindow() {
        windowCompat = new ListPopupWindow(this);
        windowCompat.setAdapter(new ArrayAdapter<String >(this,android.R.layout.simple_list_item_2,message));
        windowCompat.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        windowCompat.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        windowCompat.setAnchorView(msgBtn);
        windowCompat.setModal(true);
        windowCompat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = "msg;" + message.get(position);
                connectThread.write(msg.getBytes());
                windowCompat.dismiss();
            }
        });
    }

    //开启连接线程
    private void manageClientSocket(boolean isStart) {
        connectThread = new ConnectThread(socket,gameView,isStart);
        connectThread.start();
    }

    //发送信息
    @Override
    public void onCommand(String temp) {
        connectThread.write(temp.getBytes());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectThread.cancel();
    }
}
