package com.abby.wuziqi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.abby.wuziqi.AI.Ai;
import com.abby.wuziqi.bean.Pos;

/**
 * Created by keybo on 2017/12/26 0026.
 */

public class AiGameView extends View{
    private static final int CHESS_BLACK = 1;
    private static final int CHESS_WHITE = 2;
    private Paint paint;
    //难度,初始等级2
    private int AI_DIFFICULT = 2;
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
    //白棋出手
    private boolean isWhite = true;
    private int[][] chess;
    private int lastPos = 0;//上一次下棋位置，1为黑色 2为白色 0开始
    private Pos pos;
    Ai ai = new Ai();
    private TextView textView;
    private int upChessX;
    private int upChessY;

    public AiGameView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    public AiGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        chess = new int[LINES][LINES];
        upChessX = upChessY = LINES / 2;

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        if (lastPos == 0) {        //黑棋先下
            chess[Math.round(LINES / 2)][Math.round(LINES / 2)] = CHESS_BLACK;
            lastPos = CHESS_BLACK;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = Math.min(widthSize, heightSize);
        //未指定
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        panelWidth = width;
        gridHeight = panelWidth * 1.0f / LINES;
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
            drawBoard(canvas);
            drawPiece(canvas);
    }

    /**
     * 画棋子
     * @param canvas
     */
    private void drawPiece(Canvas canvas) {
        int side = ai.isWin(chess);
        if(side !=0){
            textView.setVisibility(View.VISIBLE);
            if (side == CHESS_BLACK) {
                textView.setText("你输了哦!");
            } else if (side == CHESS_WHITE) {
                textView.setText("你赢了哦!");
            }
            lastPos = 0;
            isOver = true;
        }
        for (int i=0;i<LINES;i++){
            for(int j=0;j<LINES;j++){
                if (chess[i][j] == CHESS_BLACK) {
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(gridHeight / 2 + i * gridHeight, gridHeight / 2 + j * gridHeight, gridHeight / 2 - 5, paint);
                }
                if (chess[i][j] == CHESS_WHITE) {
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle(gridHeight / 2 + i * gridHeight, gridHeight / 2 + j * gridHeight, gridHeight / 2 - 5, paint);
                }
                //标志ai落子的位置
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

    /**
     * 画棋盘
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        paint.setColor(Color.BLACK);
        for(int i=0;i<LINES;i++){
            canvas.drawLine(gridHeight / 2, gridHeight / 2 + i * gridHeight, gridHeight / 2 + (LINES - 1) * gridHeight, gridHeight / 2 + i * gridHeight, paint);
            canvas.drawLine(gridHeight / 2 + i * gridHeight, gridHeight / 2, gridHeight / 2 + i * gridHeight, gridHeight / 2 + (LINES - 1) * gridHeight, paint);

        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Pos ps = (Pos) msg.obj;
                    chess[ps.getX()][ps.getY()] = CHESS_BLACK;
                    upChessX = ps.getX();
                    upChessY = ps.getY();
                    lastPos = CHESS_BLACK;
                    invalidate();
                    break;
            }
        }
    };
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP){
            if (!isOver){
                int x = (int) event.getX();
                int y = (int) event.getY();
                Log.i("AiGameView",x+","+y);
                if (x<-gridHeight/2 || x > panelWidth - gridHeight / 2 || y < 0 || y > panelWidth - gridHeight/2){
                    Log.d("AiGameView",panelWidth-gridHeight/2+"");

                }else {
                    int indexX = (int) (x/gridHeight);
                    int indexY = (int) (y/gridHeight);
                    if (lastPos==CHESS_BLACK && chess[indexX][indexY] == 0){
                        chess[indexX][indexY] = CHESS_WHITE;
                        lastPos = CHESS_WHITE;
                        invalidate();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(50);
                                    pos = ai.action(chess,AI_DIFFICULT);
                                    Log.d("AiGameView","AIrun");
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.obj = pos;
                                        handler.sendMessage(msg);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        return false;
                    }
                }
            }

        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int onlyWidth = (int) (gridHeight * size);
    }


    public void restartGame(){
        textView.setVisibility(View.INVISIBLE);
        chess = new int[LINES][LINES];
        lastPos = 0;
        init();
        invalidate();
    }

    public void setTextView(TextView textView) {

        this.textView = textView;
    }

    public int getAI_DIFFICULT() {

        return AI_DIFFICULT;
    }

    public void setAI_DIFFICULT(int AI_DIFFICULT) {

        this.AI_DIFFICULT = AI_DIFFICULT;
    }
}
