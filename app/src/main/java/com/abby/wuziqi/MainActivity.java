package com.abby.wuziqi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.abby.wuziqi.game.AiActivity;
import com.abby.wuziqi.game.BlueActivity;
import com.abby.wuziqi.game.BlueConnectActivity;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private Button btnAi;
    private Button btnBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        btnAi = (Button) findViewById(R.id.btn_ai);
        btnBlue = (Button) findViewById(R.id.btn_blue);
        btnAi.setOnClickListener(this);
        btnBlue.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ai:
                Intent aiIntent=new Intent(MainActivity.this,AiActivity.class);
                startActivity(aiIntent);
                break;
            case R.id.btn_blue:
                Intent BlueIntent = new Intent(MainActivity.this,BlueConnectActivity.class);
                startActivity(BlueIntent);
                break;
        }
    }
}
