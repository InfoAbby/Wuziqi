package com.abby.wuziqi.game;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.abby.wuziqi.Adapter.BluetoothDevicesAdapter;
import com.abby.wuziqi.R;
import com.abby.wuziqi.bean.BlueTooth;
import com.abby.wuziqi.config.ConfigData;
import com.abby.wuziqi.receiver.BlueToothReceiver;
import com.abby.wuziqi.socket.BlueServerThread;
import com.abby.wuziqi.socket.BlueSocketThread;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by keybo on 2017/12/26 0026.
 */

public class BlueConnectActivity extends Activity {
    private Button search;
    private ListView listView;
    private BluetoothDevicesAdapter adapter;
    private BlueToothReceiver receiver;
    private BluetoothAdapter bluetoothAdapter;
    private String name,address;
    private List<BlueTooth> mbluetooths;
    private List<BluetoothDevice> mdevices;
    private BlueServerThread blueServerThread;
    private BlueSocketThread blueSocketThread;
    private BluetoothSocket bluetoothSocket;
    private BluetoothSocket thisSocket;
    private BluetoothDevice thisDevice;
    private boolean isAccept = false;
    private Activity activity;


    private final static int REQUEST_BLUE = 123;

    public BlueConnectActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.blue_connect);
        //一个用来放权限名字字符串的list
        List<String> permissionList = new ArrayList<>();
        //检查权限是否已获得
        if (ContextCompat.checkSelfPermission(BlueConnectActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.BLUETOOTH);
        }
        if (ContextCompat.checkSelfPermission(BlueConnectActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        if (!permissionList.isEmpty()){
            //如果有其中一个没有获得就申请权限
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(BlueConnectActivity.this, permissions, 1);
        }
        search = (Button) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.bluetooth);
        init();//初始化
        initSocket();//初始化Socket

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findBluetooth();
            }
        });
    }

    //查找设备
    private void findBluetooth() {
        if (bluetoothAdapter.isDiscovering()){
            //中断搜索
            bluetoothAdapter.cancelDiscovery();
        }
        mbluetooths.clear();
        mdevices.clear();

        //获取已配对的设备
        Set<BluetoothDevice> devices_m = bluetoothAdapter.getBondedDevices();
        if (mdevices.size() > 0){
            for (BluetoothDevice device : devices_m){
                if (!mdevices.contains(device)){
                    mbluetooths.add(new BlueTooth(device.getName(),device.getAddress()));
                    mdevices.add(device);
                }
            }
        }
        adapter.setDevices(mbluetooths);
        adapter.notifyDataSetChanged();
        //直接调用startDiscovery扫描
        bluetoothAdapter.startDiscovery();
    }

    private void initSocket() {
        //开启子线程服务端
        blueServerThread = new BlueServerThread(bluetoothSocket,name,address,activity,bluetoothAdapter,isAccept);
        blueServerThread.start();
    }

    private void init() {

        mbluetooths = new ArrayList<>();
        mdevices = new ArrayList<>();
        activity = this;
        //初始化适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //检测是否开启蓝牙可见性
        if (!bluetoothAdapter.isEnabled())
            openBluetooth();

        name = bluetoothAdapter.getName();
        address = bluetoothAdapter.getAddress();

        //初始化ListView
        try{
            adapter = new BluetoothDevicesAdapter(mbluetooths,BlueConnectActivity.this);
            listView.setAdapter(adapter);

        }catch (Exception e){
            e.printStackTrace();
        }

        //实例化广播
        receiver = new BlueToothReceiver(mdevices, mbluetooths, new BlueToothReceiver.OnReceiverListener() {
            @Override
            public void setBlueToothList(List<BlueTooth> bluetooths, List<BluetoothDevice> devices) {
                //
                mbluetooths =bluetooths;
                mdevices = devices;
                adapter.setDevices(mbluetooths);
                //
                adapter.notifyDataSetChanged();

            }

            @Override
            public void showText() {
                Toast.makeText(BlueConnectActivity.this,"搜索完成！",Toast.LENGTH_SHORT).show();
            }
        });

        //注册广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver,filter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectBluetooth(position);
            }
        });
    }

    /**
     *
     * 先获取设备的Device，然后调用
     * device.createRfcommSocketToServiceRecord
     * 获取后再调用bluetoothSocket.connect()就可以连接
     *
     * 开始连接对方
     * @param position
     */
    private void connectBluetooth(int position) {
        //
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mbluetooths.get(position).getAddress());
        try{
            //
            if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                Method method = BluetoothDevice.class.getMethod("createBond");
                method.invoke(device);
            }else if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                thisSocket = device.createRfcommSocketToServiceRecord(ConfigData.UUID);
                AlertDialog dialog = new AlertDialog.Builder(BlueConnectActivity.this)
                        .setTitle("发起挑战")
                        .setMessage("确认挑战玩家："+ mbluetooths.get(position).getName())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                blueSocketThread = new BlueSocketThread(bluetoothAdapter,thisSocket,activity,address);
                                blueSocketThread.start();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
            }
        }catch (Exception e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BlueConnectActivity.this,"连接失败",Toast.LENGTH_SHORT).show();

                }
            });
            e.printStackTrace();
        }
    }


    private void openBluetooth() {
        Intent openIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        openIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,0);//可见性时间 0是一直可见
        startActivity(openIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                return;
            }
            //申请权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_BLUE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_BLUE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {

                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (blueServerThread!=null)
            blueServerThread.cancel();
        if (blueSocketThread!=null)
            blueSocketThread.cancel();
    }
}
