package com.example.shuaige.myrouter.facade.template;

import java.util.Map;

/**
 * Created by ShuaiGe on 2018-08-20.
 */

public interface IRouteRoot {
    void loadInto(Map<String,Class<?extends IRouteGroup>> routers);
}
