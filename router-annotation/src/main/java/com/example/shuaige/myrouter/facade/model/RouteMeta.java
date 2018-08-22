package com.example.shuaige.myrouter.facade.model;

import com.example.shuaige.myrouter.facade.annotation.Route;
import com.example.shuaige.myrouter.facade.enums.RouteType;

import java.util.Map;

import javax.lang.model.element.Element;

/**
 * Created by ShuaiGe on 2018-08-17.
 */

public class RouteMeta {

    private RouteType type; //路由类型
    private Element rawType;//原始路由类型
    private Class<?> destination;//
    private String path; //路由地址

    public RouteMeta() {
    }

    private String group;//分组
    private int priority = -1;//
    private int extra;//额外数据
    private Map<String, Integer> paramType;//参数类型
    private String name;

    @Override
    public String toString() {
        return "RouteMeta{" +
                "type=" + type +
                ", rawType=" + rawType +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", priority=" + priority +
                ", extra=" + extra +
                ", paramType=" + paramType +
                ", name='" + name + '\'' +
                '}';
    }

    public static RouteMeta build(RouteType type, Class<?> destination, String path, String group, Map<String, Integer> paramType, int priority, int extra) {
        return new RouteMeta(type, null, destination, path, group, priority, extra, paramType, null);

    }

    public static RouteMeta build(RouteType type, Class<?> destination, String path, String group, int priority, int extra) {
        return new RouteMeta(type, null, destination, path, group, priority, extra, null, null);

    }

    public RouteMeta(Route route, RouteType type, Class<?> destination) {
        new RouteMeta(type, null, destination, route.path(), route.group(), route.priority(), route.extras(), null, route.name());
    }

    public RouteMeta(Route route, RouteType type, Element rawType, Map<String, Integer> paramType) {
        this(type, rawType, null, route.path(), route.group(), route.priority(), route.extras(), paramType, route.name());
    }

    public RouteType getType() {

        return type;
    }

    public RouteMeta setType(RouteType type) {
        this.type = type;
        return this;
    }

    public Element getRawType() {
        return rawType;
    }

    public RouteMeta setRawType(Element rawType) {
        this.rawType = rawType;
        return this;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public RouteMeta setDestination(Class<?> destination) {
        this.destination = destination;
        return this;
    }

    public String getPath() {
        return path;
    }

    public RouteMeta setPath(String path) {
        this.path = path;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public RouteMeta setGroup(String group) {
        this.group = group;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public RouteMeta setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public int getExtra() {
        return extra;
    }

    public RouteMeta setExtra(int extra) {
        this.extra = extra;
        return this;
    }

    public Map<String, Integer> getParamType() {
        return paramType;
    }

    public RouteMeta setParamType(Map<String, Integer> paramType) {
        this.paramType = paramType;
        return this;
    }

    public String getName() {
        return name;
    }

    public RouteMeta setName(String name) {
        this.name = name;
        return this;
    }


    public RouteMeta(RouteType type, Element rawType, Class<?> destination, String path, String group, int priority, int extra, Map<String, Integer> paramType, String name) {
        this.type = type;
        this.rawType = rawType;
        this.destination = destination;
        this.path = path;
        this.group = group;
        this.priority = priority;
        this.extra = extra;
        this.paramType = paramType;
        this.name = name;
    }

}
