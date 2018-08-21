package com.example.shuaige.myrouter.core;

import com.example.shuaige.myrouter.facade.model.RouteMeta;
import com.example.shuaige.myrouter.facade.template.IRouteGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ShuaiGe on 2018-08-20.
 */

public class Warehouse {
    //缓存映射
    static Map<String,Class<?extends IRouteGroup>> groupsIndex = new HashMap<>();
    static Map<String,RouteMeta> routes = new HashMap<>();

    static void clear(){
        groupsIndex.clear();
        routes.clear();
    }
}
