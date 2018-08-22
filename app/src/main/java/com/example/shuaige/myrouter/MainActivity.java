package com.example.shuaige.myrouter;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.shuaige.myrouter.launcher.MyRouter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = this.getPackageManager().getApplicationInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.i("_zs_", "getSourcePaths: applicationInfo.sourceDir = "+applicationInfo.sourceDir);
        Log.i("_zs_", "getSourcePaths: applicationInfo.dataDir = "+applicationInfo.dataDir);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.button1:
                MyRouter.getInstance().build("/modulea/moduleamain").navigation();
                break;
            case R.id.button2:
                MyRouter.getInstance().build("/moduleb/modulebmain").navigation();
                break;
        }
    }
}
