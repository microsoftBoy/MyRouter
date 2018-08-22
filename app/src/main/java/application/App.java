package application;

import android.app.Application;

import com.example.shuaige.myrouter.launcher.MyRouter;

/**
 * Created by ShuaiGe on 2018-08-21.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MyRouter.init(this);
    }
}
