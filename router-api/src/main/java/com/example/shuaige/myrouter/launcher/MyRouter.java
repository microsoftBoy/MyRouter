package com.example.shuaige.myrouter.launcher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.shuaige.myrouter.core.LogisticsCenter;
import com.example.shuaige.myrouter.exception.HandlerException;
import com.example.shuaige.myrouter.exception.InitException;
import com.example.shuaige.myrouter.exception.NoRouteFoundException;
import com.example.shuaige.myrouter.facade.Postcard;
import com.example.shuaige.myrouter.facade.callback.NavigationCallback;
import com.example.shuaige.myrouter.facade.template.ILogger;
import com.example.shuaige.myrouter.thread.DefaultPoolExecutor;
import com.example.shuaige.myrouter.utils.Consts;
import com.example.shuaige.myrouter.utils.DefaultLogger;
import com.example.shuaige.myrouter.utils.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

/**
 * Created by ShuaiGe on 2018-08-20.
 */

public final class MyRouter {

    private static volatile MyRouter instance;

    private static Context mContext;
    public static ILogger logger = new DefaultLogger(Consts.TAG);
    private volatile static boolean hasInit;
    private static Handler mHandler;
    private volatile static ThreadPoolExecutor executor = DefaultPoolExecutor.getInstance();

    protected static synchronized boolean init(Application application) {
        logger.info(Consts.TAG, "ARouter init start!");
        mContext = application;
        LogisticsCenter.init(application, executor);
        logger.info(Consts.TAG, "ARouter init success!");
        hasInit = true;
        mHandler = new Handler(Looper.getMainLooper());
        logger.info(Consts.TAG, "ARouter init end!");
        return true;
    }

    public static MyRouter getInstance() {
        if (!hasInit) {
            throw new InitException("ARouter::Init::Invoke init(context) first!");
        } else {
            if (instance == null) {
                synchronized (MyRouter.class) {
                    if (instance == null) {
                        instance = new MyRouter();
                    }
                }
            }
        }

        return instance;
    }


    public Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new HandlerException(Consts.TAG + "Parameter invalid!");
        } else {
            return build(path, extractGroup(path));
        }
    }

    public Postcard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new HandlerException(Consts.TAG + "Parameter is invalid!");
        } else {

            return new Postcard(path, group);
        }
    }


    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new HandlerException(Consts.TAG + "Extract the default group failed, the path must be start with '/' and contain more than 2 '/'!");
        }

        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));

            if (TextUtils.isEmpty(defaultGroup)) {
                throw new HandlerException(Consts.TAG + "Extract the default group failed! There's nothing between 2 '/'!");
            } else {
                return defaultGroup;
            }
        } catch (HandlerException e) {
            logger.warning(Consts.TAG, "Failed to extract default group! " + e.getMessage());
            return null;
        }
    }


    public Object navigation(Context context, Postcard postcard, int requestCode, NavigationCallback callback){

        try {
            LogisticsCenter.completion(postcard);
        } catch (NoRouteFoundException ex) {
            logger.warning(Consts.TAG, ex.getMessage());
            Toast.makeText(mContext, "There's no route matched!\n" +
                    " Path = [" + postcard.getPath() + "]\n" +
                    " Group = [" + postcard.getGroup() + "]", Toast.LENGTH_LONG).show();
            if (null != callback){
                callback.onLost(postcard);
            }

            return _navigation(context, postcard, requestCode, callback);
        }

        return null;

    }

    private Object _navigation(Context context, final Postcard postcard, final int requestCode, final NavigationCallback callback) {

        final Context currentContext = null == context ? mContext : context;

        switch (postcard.getType()){
            case ACTIVITY:
                final Intent intent = new Intent(currentContext, postcard.getDestination());

                //set flags
                int flags = postcard.getFlags();
                if (-1 != flags){
                    intent.setFlags(flags);
                }else if (!(currentContext instanceof Activity)){
                    //如果上下文不是activity类型的，那么最少需要一个flag
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                //navigation in main looper
                if (Looper.getMainLooper().getThread() != Thread.currentThread()){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(requestCode,currentContext,intent,postcard,callback);
                        }
                    });
                }else {
                    startActivity(requestCode,currentContext,intent,postcard,callback);
                }

                break;

        }

        return null;
    }


    private void startActivity(int requestCode,Context currentContext,Intent intent,Postcard postcard,NavigationCallback callback){
        if (requestCode >= 0){
            ActivityCompat.startActivityForResult((Activity) currentContext,intent,requestCode,null);
        }
        else {
            ActivityCompat.startActivity(currentContext,intent,null);
        }

        if (null != callback){
            callback.onArrival(postcard);
        }

    }


}
