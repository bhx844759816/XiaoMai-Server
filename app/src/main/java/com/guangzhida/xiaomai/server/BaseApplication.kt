package com.guangzhida.xiaomai.server

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.gson.Gson
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.getSpValue
import com.guangzhida.xiaomai.server.chat.ChatHelper
import com.guangzhida.xiaomai.server.model.UserModel
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.guangzhida.xiaomai.server.utils.ToastUtils
import com.tencent.bugly.crashreport.CrashReport

class BaseApplication : Application() {
    var mUserModel: UserModel? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        CrashReport.initCrashReport(instance, "a9dde3c5a6", false);
        LogUtils.init()
        ToastUtils.init(applicationContext)
        mUserModel =
            Gson().fromJson<UserModel>(getSpValue(USER_MODEL_KEY, ""), UserModel::class.java)
        ChatHelper.init(applicationContext, packageName)
    }
    //15937128521

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


    /**
     * 获取MyApplication得单例
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: BaseApplication? = null

        fun instance() = instance!!
    }
}