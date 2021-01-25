package com.lc.demo;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lc.demo.view.MyViewA;
import com.lc.demo.view.MyViewB;

public class ViewActivity extends AppCompatActivity {

    private MyViewA myViewA;
    private MyViewB myViewB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_view);
        myViewA = findViewById(R.id.viewA);
        myViewB = findViewById(R.id.viewB);
    }

    public void BtnClick(View view) {

        myViewA.setLabel("Click ...");

    }
}
