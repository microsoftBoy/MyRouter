package com.example.shuaige.myrouter.compiler.utils;

/**
 * Created by ShuaiGe on 2018-08-17.
 */

public class Consts {

    //custom interface
    public static final String SEPARATOR = "$$";
    public static final String FACADE_PACKAGE = "com.example.shuaige.myrouter.facade";
    private static final String TEMPLATE_PACKAGE = ".template";
    public static final String PROJECT = "MyRouter";
    public static final String IROUTE_ROOT = FACADE_PACKAGE+TEMPLATE_PACKAGE+".IRouteRoot";
    public static final String IROUTE_GROUP = FACADE_PACKAGE+TEMPLATE_PACKAGE+".IRouteGroup";
    public static final String METHOD_LOAD_INTO = "loadInto";
    public static final String PACKAGE_OF_GENERATE_FILE = "com.example.shuaige.myrouter.routes";
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root";
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group"+ SEPARATOR;
    public static final String WARNING_TIPS = "DO NOT EDIT THIS FILE!!! IT WAS GENERATED BY MYROUTER.";




    public static final String ANNOTATION_TYPE_ROUTE = FACADE_PACKAGE+".annotation.Route";
    public static final String PACKAGE_OF_GENERATE_DOCS = "com.example.shuaige.myrouter.docs";

    //System interface
    public static final String PARCELABLE = "android.os.Parcelable";
    public static final String ACTIVITY = "android.app.Activity";


    //Java type
    public static final String SERIALIZABLE = "java.io.Serializable";
    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String CHAR = LANG + ".Character";
    public static final String STRING = LANG + ".String";

    // Log
    static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";

    // Options of processor
    public static final String KEY_MODULE_NAME = "MYROUTER_MODULE_NAME";
    public static final String VALUE_ENABLE = "enable";
    public static final String KEY_GENERATE_DOC_NAME = "MYROUTER_GENERATE_DOC";
}
