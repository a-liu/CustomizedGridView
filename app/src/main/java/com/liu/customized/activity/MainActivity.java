package com.liu.customized.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.liu.customized.R;



public class MainActivity extends AppCompatActivity {
    private Button dynamicGridBtn;
    private Button wrapGridBtn;
    private Button tableViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dynamicGridBtn = (Button) findViewById(R.id.dynamic_grid_btn);
        wrapGridBtn = (Button) findViewById(R.id.wrap_grid_btn);
        tableViewBtn = (Button) findViewById(R.id.table_view_btn);
        dynamicGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GridViewActivity.class));
            }
        });

        wrapGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WrapGridViewActivity.class));
            }
        });
        tableViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TableViewActivity.class));
            }
        });
    }

}
