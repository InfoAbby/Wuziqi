package com.abby.wuziqi.bean;

/**
 * Created by keybo on 2017/12/26 0026.
 */

public class BlueTooth {
    private String name;
    private String address;

    public BlueTooth(String name,String address){
        this.name = name;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }
}
