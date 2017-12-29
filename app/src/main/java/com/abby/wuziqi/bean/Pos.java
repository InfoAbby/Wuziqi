package com.abby.wuziqi.bean;

import android.graphics.Color;

/**
 * Created by keybo on 2017/12/26 0026.
 */

public class Pos {
    private int x;
    private int y;
    private int color;

    public Pos(int x, int y, int color) {
        this.x=x;
        this.y=y;
        this.color=color;
    }

    public Pos() {
        
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
