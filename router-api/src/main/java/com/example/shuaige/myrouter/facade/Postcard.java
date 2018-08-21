package com.example.shuaige.myrouter.facade;

import android.content.Context;

import com.example.shuaige.myrouter.facade.callback.NavigationCallback;
import com.example.shuaige.myrouter.facade.model.RouteMeta;
import com.example.shuaige.myrouter.launcher.MyRouter;

/**
 * Created by ShuaiGe on 2018-08-20.
 */

public final class Postcard  extends RouteMeta{

    private int flags = -1;         // Flags of route



    public Postcard() {
        this(null,null);
    }

    public Postcard(String path,String group) {
        setPath(path);
        setGroup(group);
    }

    /**
     * Navigation to the route with path in postcard.
     * No param, will be use application context.
     */
    public Object navigation() {
        return navigation(null);
    }

    /**
     * Navigation to the route with path in postcard.
     *
     * @param context Activity and so on.
     */
    public Object navigation(Context context) {
        return navigation(context, null);
    }

    /**
     * Navigation to the route with path in postcard.
     *
     * @param context Activity and so on.
     */
    public Object navigation(Context context, NavigationCallback callback) {
        return MyRouter.getInstance().navigation(context, this, -1, callback);
    }


    public int getFlags() {
        return flags;
    }

    public Postcard withFlags(int flags) {
        this.flags = flags;
        return this;
    }

}
