package com.abby.wuziqi.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.abby.wuziqi.R;
import com.abby.wuziqi.view.AiGameView;

/**
 * Created by keybo on 2017/12/26 0026.
 */

public class AiActivity extends Activity{
    private Button again,level;
    private TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ai);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Log.v("AiActivity","onCreat()");
        init();
    }

    private void init() {
        final AiGameView gameView = findViewById(R.id.gameView);
        textView = findViewById(R.id.hint);
        gameView.setTextView(textView);
        again = findViewById(R.id.again);
        level = findViewById(R.id.level);

        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.isOver=false;
                gameView.restartGame();
            }
        });

        level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (gameView.getAI_DIFFICULT()){
                    case 2:
                        gameView.setAI_DIFFICULT(3);
                        level.setText("难度:一般");
                        break;
                    case 3:
                        gameView.setAI_DIFFICULT(2);
                        level.setText("难度:简单");
                        break;
                }
                gameView.isOver = false;
                gameView.restartGame();
            }
        });
    }
}
