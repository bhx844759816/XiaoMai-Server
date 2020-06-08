package com.guangzhida.xiaomai.server.data

import com.guangzhida.xiaomai.server.data.chat.ChatRepository
import com.guangzhida.xiaomai.server.data.common.CommonRepository
import com.guangzhida.xiaomai.server.data.home.HomeRepository
import com.guangzhida.xiaomai.server.data.login.LoginRepository
import com.guangzhida.xiaomai.server.data.user.UserRepository

/**
 * 注入Repository
 */
object InjectorUtils {
    /**
     * 获取公共的Repository
     */
    fun getCommonRepository() = CommonRepository.getInstance()

    /**
     * 登录的Repository
     */
    fun getLoginRepository() = LoginRepository.getInstance()

    /**
     * 获取校园网的Repository
     */
    fun getHomeRepository() = HomeRepository.getInstance()

    /**
     * 获取互动的Repository
     */
    fun getChatRepository() = ChatRepository.getInstance()

    /**
     * 获取操作用户对象的Repository
     */
    fun getUserRepository() = UserRepository.getInstance()

}