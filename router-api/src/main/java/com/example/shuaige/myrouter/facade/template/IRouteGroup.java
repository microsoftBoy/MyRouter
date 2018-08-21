package com.example.shuaige.myrouter.facade.template;

import com.example.shuaige.myrouter.facade.model.RouteMeta;

import java.util.Map;

/**
 * Created by ShuaiGe on 2018-08-20.
 */

public interface IRouteGroup {
    void loadInto(Map<String,RouteMeta> atlas);

}
