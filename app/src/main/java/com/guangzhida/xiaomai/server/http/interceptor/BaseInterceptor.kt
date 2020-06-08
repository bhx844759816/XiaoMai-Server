package com.guangzhida.xiaomai.server.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 添加通用请求头
 */

class BaseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request().newBuilder().run {
//            if (BaseApplication.instance().mUserModel != null) {
//                LogUtils.i("token=${BaseApplication.instance().mUserModel!!.token}")
//                addHeader("Authorization", BaseApplication.instance().mUserModel!!.token).build()
//            }
            build()
        })
    }
}