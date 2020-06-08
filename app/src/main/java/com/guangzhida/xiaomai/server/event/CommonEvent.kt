package com.guangzhida.xiaomai.server.event

import androidx.lifecycle.MutableLiveData

/**
 * 设备和学校状态切换的时候发送消息
 */
val stateChangeLiveData = MutableLiveData<Boolean>()
/**
 * 消息个数改变的观察者
 */
val messageCountChangeLiveData = MutableLiveData<Boolean>()
