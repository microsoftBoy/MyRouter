package com.example.moduleb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shuaige.myrouter.facade.annotation.Route;

@Route(path = "/moduleb/modulebmain")
public class ModuleBMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_bmain);
    }
}
