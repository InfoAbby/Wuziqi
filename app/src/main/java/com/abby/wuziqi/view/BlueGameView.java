package com.abby.wuziqi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.abby.wuziqi.bean.Pos;
import com.abby.wuziqi.game.BlueActivity;

import java.util.Stack;

/**
 * Created by keybo on 2017/12/26 0026.
 */

public class BlueGameView extends View {
    private static final int CHESS_BLACK = 1;
    private static final int CHESS_WHITE = 2;
    private Paint paint;

    //是否结束
    public boolean isOver = false;
    //画布宽度
    private int panelWidth;
    //行数
    private static final int LINES=15;
    //棋子大小
    private static final float size = 3*1.0f/4;
    //格子高度
    private float gridHeight;
    //轮到谁出手
    private boolean isMe = false;
    //哪方先出手
    private boolean isStart = false;
    //棋子
    private int[][] chess;
    //上一次下棋方，1为黑色 2为白色 0开始
    private int lastColor = 0;

    private Pos pos;
    private TextView textView;
    //上一次落子的位置
    private int upChessX;
    private int upChessY;
    //悔棋栈
    private Stack<Pos> chessBack;
    private BlueActivity gameActivity;

    private String address;

    public BlueGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }



    private void init() {
        isOver = false;
        upChessX = upChessY = -1;
        chess = new int[LINES][LINES];
        chessBack = new Stack<>();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        isMe = isStart;
        if (isStart){
            lastColor = CHESS_WHITE;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        panelWidth = width;
        gridHeight = panelWidth * 1.0f / LINES;
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBoard(canvas);
        drawPiece(canvas);
    }

    private void drawPiece(Canvas canvas) {
        int side = isWin(chess);
        if (side != 0){
            textView.setVisibility(View.VISIBLE);
            if (side == CHESS_BLACK) {
                //BlueActivity.onCommand("black_win");
                textView.setText("黑棋胜利!");
            } else if (side == CHESS_WHITE) {
                //BlueActivity.onCommand("white_win");
                textView.setText("白棋胜利!");
            }
            lastColor = 0;
            isOver = true;
        }
        for (int i = 0; i < LINES; i++) {
            for (int j = 0; j < LINES; j++) {
                if (chess[i][j] == CHESS_BLACK) {
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(gridHeight / 2 + i * gridHeight, gridHeight / 2 + j * gridHeight, gridHeight / 2 - 5, paint);
                }
                if (chess[i][j] == CHESS_WHITE) {
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle(gridHeight / 2 + i * gridHeight, gridHeight / 2 + j * gridHeight, gridHeight / 2 - 5, paint);
                }
                //画上一个棋子的圆环
                if (i == upChessX && j == upChessY) {
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(4);
                    canvas.drawCircle(gridHeight / 2 + i * gridHeight, gridHeight / 2 + j * gridHeight, gridHeight / 2 - 2, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(0);
                }
            }
        }
    }
    public void setGameActivity(BlueActivity activity){
        this.gameActivity = activity;
    }
    public void setIsStart(boolean isStart) {
        this.isStart = isStart;
        isMe = isStart;
        if (isStart) {
            lastColor = CHESS_WHITE;
        }
    }
    private void drawBoard(Canvas canvas) {
        paint.setColor(Color.BLACK);
        for (int i = 0; i < LINES; i++) {
            //横线
            canvas.drawLine(gridHeight / 2, gridHeight / 2 + i * gridHeight, gridHeight / 2 + (LINES - 1) * gridHeight, gridHeight / 2 + i * gridHeight, paint);
            //竖线
            canvas.drawLine(gridHeight / 2 + i * gridHeight, gridHeight / 2, gridHeight / 2 + i * gridHeight, gridHeight / 2 + (LINES - 1) * gridHeight, paint);
        }
    }

    public void putCommand(int x,int y,int lastColor){
        chess[x][y] = lastColor;
        Pos p = new Pos(x,y,lastColor);
        chessBack.push(p);
        String command = "";
        String temp = x + ";" + y + ";" + lastColor;
        command += address +";"+temp;
        gameActivity.onCommand(command+";"+lastColor);

    }



    //判断是否已有结果
    public int isWin(int[][] chess) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                int side = chess[i][j];
                if (side == 0)
                    continue;
                for (int k = 0; k < 5; k++) {
                    //check from vertical
                    if ((i - k >= 0 && i + 4 - k < 15) &&
                            (chess[i - k][j] == side &&
                                    chess[i + 1 - k][j] == side &&
                                    chess[i + 2 - k][j] == side &&
                                    chess[i + 3 - k][j] == side &&
                                    chess[i + 4 - k][j] == side)) {
                        return side;
                    }
                    //check from horizontal
                    if ((j - k >= 0 && j + 4 - k < 15) &&
                            (chess[i][j - k] == side &&
                                    chess[i][j + 1 - k] == side &&
                                    chess[i][j + 2 - k] == side &&
                                    chess[i][j + 3 - k] == side &&
                                    chess[i][j + 4 - k] == side)) {
                        return side;
                    }
                    //check from leftbevel
                    if ((i - k >= 0 && j - k >= 0 && i + 4 - k < 15 && j + 4 - k < 15) &&
                            (chess[i - k][j - k] == side &&
                                    chess[i + 1 - k][j + 1 - k] == side &&
                                    chess[i + 2 - k][j + 2 - k] == side &&
                                    chess[i + 3 - k][j + 3 - k] == side &&
                                    chess[i + 4 - k][j + 4 - k] == side)) {
                        return side;
                    }
                    //check from rightbevel
                    if ((i - k >= 0 && j + k < 15 && i + 4 - k < 15 && j - 4 + k >= 0) &&
                            (chess[i - k][j + k] == side &&
                                    chess[i + 1 - k][j - 1 + k] == side &&
                                    chess[i + 2 - k][j - 2 + k] == side &&
                                    chess[i + 3 - k][j - 3 + k] == side &&
                                    chess[i + 4 - k][j - 4 + k] == side)) {
                        return side;
                    }

                }
            }
        }
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isOver) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (x < -gridHeight / 2 || x > panelWidth - gridHeight / 2 || y < 0 || y > panelWidth - gridHeight / 2) {

                } else {
                    int indexX = (int) (x / gridHeight);
                    int indexY = (int) (y / gridHeight);
                    upChessX = indexX;
                    upChessY = indexY;
                    if (lastColor == CHESS_WHITE && isMe && chess[indexX][indexY] == 0) {
                        putCommand(indexX, indexY, CHESS_BLACK);
                        lastColor = CHESS_BLACK;
                        isMe = false;
                        invalidate();
                    } else if (lastColor == CHESS_BLACK && isMe && chess[indexX][indexY] == 0) {
                        putCommand(indexX, indexY, CHESS_WHITE);
                        lastColor = CHESS_WHITE;
                        isMe = false;
                        invalidate();
                    }
                }
            }
        }
        return true;
    }

    public void retunrnUp(){
        //已经结束
        if (!isOver){
            if (chessBack.size() ==0){

            }else {
                Pos p = chessBack.pop();
                int x = p.getX();
                int y = p.getY();
                int color = p.getColor();
                chess[x][y] = 0;
                if (chessBack.size() == 0){
                    //回到开始状态,更新
                    isMe = isStart;
                    lastColor = CHESS_WHITE;
                    upChessX = -1;
                    upChessY = -1;
                }else {
                    //更新upChess
                    Pos last = chessBack.get(chessBack.size()-1);
                    upChessY = last.getY();
                    upChessX = last.getX();
                    switch (color){
                        case CHESS_BLACK:
                            lastColor=CHESS_WHITE;
                            break;
                        case CHESS_WHITE:
                            lastColor=CHESS_BLACK;
                            break;
                    }
                    isMe = !isMe;

                }
                invalidate();
            }
        }else Toast.makeText(gameActivity, "请重新开始", Toast.LENGTH_LONG).show();
    }

    //接收对方传送信息
    public void getCommand(String command) {
        switch (command){
            case "msg":
                String finalCommand = command.substring(0,4);
                Toast.makeText(gameActivity,finalCommand,Toast.LENGTH_SHORT).show();
                break;
            case "white_win":
                textView.setText("白棋胜利");
                break;
            case "black_win":
                textView.setText("黑棋胜利");
                break;
            case "return":
                Toast.makeText(gameActivity,"对方悔棋",Toast.LENGTH_SHORT).show();
                retunrnUp();
                break;
            case "restart":
                Toast.makeText(gameActivity, "对方重置游戏!", Toast.LENGTH_SHORT).show();
                restartGame();
                break;
            default:
                //游戏继续
                isMe = false;
                //传过来的数据以；分割
                String[] data =command.split(";");
                int x =Integer.parseInt(data[1]);

                int y = Integer.parseInt(data[2]);
                int color = Integer.parseInt(data[3]);
                upChessX = x;
                upChessY = y;
                chess[x][y] = color;
                lastColor =color;
                Pos p = new Pos(x,y,color);
                chessBack.push(p);
                invalidate();
                isMe =true;

        }
    }

    //重置游戏
    public void restartGame() {
        textView.setVisibility(View.INVISIBLE);
        init();
        invalidate();
    }

    //设置回调
    public void setCallBack(BlueActivity blueActivity) {
        this.gameActivity = blueActivity;
    }

    public interface onBluetoothListener {
        void onCommand(String temp);
    }
}
