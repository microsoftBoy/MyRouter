package com.example.shuaige.myrouter.facade.template;

import com.example.shuaige.myrouter.utils.Consts;

/**
 * Created by ShuaiGe on 2018-08-20.
 */

public interface ILogger {
    boolean isShowLog = false;
    boolean isShowStackTrace = false;
    String defaultTag = Consts.TAG;

    void showLog(boolean isShowLog);

    void showStackTrace(boolean isShowStackTrace);

    void debug(String tag, String message);

    void info(String tag, String message);

    void warning(String tag, String message);

    void error(String tag, String message);

    void monitor(String message);

    boolean isMonitorMode();

    String getDefaultTag();
}
