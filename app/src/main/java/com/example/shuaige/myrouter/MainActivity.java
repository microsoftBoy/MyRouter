package com.example.shuaige.myrouter;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

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

    }
}
