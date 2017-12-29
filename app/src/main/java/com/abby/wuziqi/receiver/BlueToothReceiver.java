package com.abby.wuziqi.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.abby.wuziqi.bean.BlueTooth;

import java.util.List;

/**
 * Created by keybo on 2017/12/26 0026.
 */

public class BlueToothReceiver extends BroadcastReceiver {
    /**
     * BluetoothDevice代表一个远程蓝牙设备，通过这个类可以查询
     * 远程设备的物理地址，名称，连接。
     * 调用BluetoothAdapter的getRemoteDevice(address)方法获取物理地址对应的设备
     * getBoundedDevices()获取已经配对的蓝牙设备集合
     */
    //已配对的设备
    private List<BluetoothDevice> devices;
    private List<BlueTooth> blueToothList;
    //内部接口
    public OnReceiverListener onReceiverListener;

    public BlueToothReceiver(List<BluetoothDevice> devices,List<BlueTooth> blueToothList,OnReceiverListener onReceiverListener){
        this.blueToothList=blueToothList;
        this.onReceiverListener=onReceiverListener;
        this.devices=devices;

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)){
            //获取扫描到的device信息
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String temp = "在线中";
            BlueTooth bluetooth =new BlueTooth(device.getName(),device.getAddress());
            if (!devices.contains(device)){
                //如果不在已配对的设备里 添加进来
                bluetooth.setName(bluetooth.getName()+temp);
                blueToothList.add(bluetooth);
                devices.add(device);
            }else {
                for (int i=0;i<blueToothList.size();i++){
                    if (bluetooth.getAddress().equals(blueToothList.get(i).getAddress())){
                        BlueTooth bt = new BlueTooth(blueToothList.get(i).getName()+temp,blueToothList.get(i).getAddress());
                        blueToothList.set(i,bt);
                    }
                }
            }
        }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            onReceiverListener.showText();
        }

        if (onReceiverListener != null){
            onReceiverListener.setBlueToothList(blueToothList,devices);
        }
    }




    //BluetoothActivity的回调接口
    public interface OnReceiverListener{
        void setBlueToothList(List<BlueTooth> blueTooths,List<BluetoothDevice> bluetoothDevices);
        void showText();
    }
}
