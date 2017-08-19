package com.monke.mrefreshviewdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnRvDemo;
    private Button btnRvDemo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRvDemo = (Button) findViewById(R.id.btn_rv_demo);
        btnRvDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RecyclerViewDemoActivity.class));
            }
        });

        btnRvDemo2 = (Button) findViewById(R.id.btn_rv_demo2);
        btnRvDemo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RecyclerViewDemo2Activity.class));
            }
        });
    }
}
