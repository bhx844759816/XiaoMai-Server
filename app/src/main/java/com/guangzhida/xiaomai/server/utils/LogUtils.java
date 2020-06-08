package com.guangzhida.xiaomai.server.utils;


import androidx.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class LogUtils {
    /**
     * 全局log日志的Tag
     */
    public static final String TAG = "bhx_Tag";

    private LogUtils() {
        throw new RuntimeException("LogUtils is not instantiate ");
    }

    /**
     * 初始化Log日志
     */
    public static void init() {
        Logger.t(TAG);
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return true;
            }
        });
    }

    /**
     * 打印Log.i日志
     *
     * @param message
     * @param args
     */
    public static void i(String message, Object... args) {
        Logger.i(message, args);
    }

    /**
     * 打印Log.d日志
     *
     * @param message
     * @param args
     */
    public static void d(String message, Object... args) {
        Logger.d(message, args);
    }

    /**
     * 打印Log.e日志
     *
     * @param message
     * @param args
     */
    public static void e(String message, Object... args) {
        Logger.e(message, args);
    }


}
