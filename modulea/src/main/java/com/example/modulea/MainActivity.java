package com.example.modulea;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shuaige.myrouter.facade.annotation.Route;
import com.example.shuaige.myrouter.facade.annotation.RouteTest;

@RouteTest(path = "/RouteTest/RouteTest",group = "RouteTest")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
