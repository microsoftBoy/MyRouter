package com.example.shuaige.myrouter.core;

import android.content.Context;
import android.content.pm.PackageManager;

import com.example.shuaige.myrouter.exception.HandlerException;
import com.example.shuaige.myrouter.exception.NoRouteFoundException;
import com.example.shuaige.myrouter.facade.Postcard;
import com.example.shuaige.myrouter.facade.model.RouteMeta;
import com.example.shuaige.myrouter.facade.template.ILogger;
import com.example.shuaige.myrouter.facade.template.IRouteGroup;
import com.example.shuaige.myrouter.facade.template.IRouteRoot;
import com.example.shuaige.myrouter.utils.ClassUtils;
import com.example.shuaige.myrouter.utils.DefaultLogger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import static com.example.shuaige.myrouter.utils.Consts.AROUTER_SP_CACHE_KEY;
import static com.example.shuaige.myrouter.utils.Consts.AROUTER_SP_KEY_MAP;
import static com.example.shuaige.myrouter.utils.Consts.DOT;
import static com.example.shuaige.myrouter.utils.Consts.ROUTE_ROOT_PAKCAGE;
import static com.example.shuaige.myrouter.utils.Consts.SDK_NAME;
import static com.example.shuaige.myrouter.utils.Consts.SEPARATOR;
import static com.example.shuaige.myrouter.utils.Consts.SUFFIX_ROOT;
import static com.example.shuaige.myrouter.utils.Consts.TAG;

/**
 * Created by ShuaiGe on 2018-08-20.
 */

public class LogisticsCenter {

    private static Context mContext;
    private static ThreadPoolExecutor executor;
    private static boolean registerByPlugin;

    static ILogger logger = new DefaultLogger(TAG); // 日志工具

    public synchronized static void init(Context context, ThreadPoolExecutor tpe) throws HandlerException{
        mContext = context;
        executor = tpe;


        try {
            long startInit = System.currentTimeMillis();
            if (registerByPlugin) {
                logger.info(TAG, "ARouter init success!");
            } else {
                logger.info(TAG, "Run with debug mode or new install, rebuild router map.");
                //通过router_compiler
                Set<String> routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
                if (!routerMap.isEmpty()) {
                    context.getSharedPreferences(AROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).edit().putStringSet(AROUTER_SP_KEY_MAP, routerMap).apply();
                }

                logger.info(TAG, "Find router map finished, map size = " + routerMap.size() + ", cost " + (System.currentTimeMillis() - startInit) + " ms.");
                startInit = System.currentTimeMillis();

                for(String className:routerMap){
                    if (className.startsWith(ROUTE_ROOT_PAKCAGE+DOT+SDK_NAME+SEPARATOR+SUFFIX_ROOT)){
                        ((IRouteRoot)Class.forName(className).getConstructor().newInstance()).loadInto(Warehouse.groupsIndex);
                    }
                }

                logger.info(TAG, "Load root element finished, cost " + (System.currentTimeMillis() - startInit) + " ms.");

                if (Warehouse.groupsIndex.size() == 0) {
                    logger.error(TAG, "No mapping files were found, check your configuration please!");
                }

            }
        } catch (Exception e) {
            throw new HandlerException(TAG + "ARouter init logistics center exception! [" + e.getMessage() + "]");
        }

    }


    public static synchronized void completion(Postcard postcard){
        if ((null == postcard)){
            throw new NoRouteFoundException(TAG + "No postcard!");
        }

        RouteMeta routeMeta = Warehouse.routes.get(postcard.getPath());
        //如果不存在映射
        if (null == routeMeta){
            Class<? extends IRouteGroup> groupMeta = Warehouse.groupsIndex.get(postcard.getGroup());
            if (null == groupMeta){
                throw new NoRouteFoundException(TAG + "There is no route match the path [" + postcard.getPath() + "], in group [" + postcard.getGroup() + "]");
            }
            else {
                try {
                    //加载路由并缓存到内存中，然后删除数据
                    IRouteGroup iRouteGroup = groupMeta.getConstructor().newInstance();

                    iRouteGroup.loadInto(Warehouse.routes);

                    Warehouse.groupsIndex.remove(postcard.getGroup());
                    logger.debug(TAG, String.format(Locale.getDefault(), "The group [%s] has already been loaded, trigger by [%s]", postcard.getGroup(), postcard.getPath()));
                } catch (Exception e) {
                    throw new HandlerException(TAG + "Fatal exception when loading group meta. [" + e.getMessage() + "]");
                }

                completion(postcard);//reload

            }
        }else {
            postcard.setDestination(routeMeta.getDestination());
            postcard.setType(routeMeta.getType());
            postcard.setPriority(routeMeta.getPriority());
            postcard.setExtra(routeMeta.getExtra());

        }

    }
}
