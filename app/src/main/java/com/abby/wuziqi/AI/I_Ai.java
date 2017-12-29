package com.abby.wuziqi.AI;

import com.abby.wuziqi.bean.Pos;

import java.util.List;

/**
 * Created by keybo on 2017/12/26 0026.
 */

public interface I_Ai {
    Pos action(int chess[][], int depth);
    int getLine(int i,int j,int dir,int relapos,int chess[][]);
    int evaluate(int chess[][]);
    int max(int chess[][],int deep,int alpha,int beta);
    int min(int chess[][],int deep,int alpha,int beta);
    int pointValue(int chess[][],int i,int j,int side);
    boolean hasNeighbor(int chess[][],int i ,int j,int d);
    int isWin(int chess[][]);
    List<Pos> generate(int chess[][], int deep);
}
