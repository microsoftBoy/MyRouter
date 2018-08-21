package com.example.shuaige.myrouter.facade.enums;

/**
 * Created by ShuaiGe on 2018-08-17.
 */

public enum RouteType {
    ACTIVITY(0,"android.app.activity"),
    UNKNOWN(-1,"Unknown route type");

    int id;
    String className;

    public int getId() {
        return id;
    }

    public RouteType setId(int id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public RouteType setClassName(String className) {
        this.className = className;
        return this;
    }

    RouteType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public static RouteType parse(String name){
        for (RouteType routeType:RouteType.values()) {
            if (routeType.getClassName().equals(name)){
                return routeType;
            }
        }

        return UNKNOWN;
    }
}
