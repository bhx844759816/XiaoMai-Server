package com.guangzhida.xiaomai.server

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.guangzhida.xiaomai.server.utils.LogUtils

/**
 * 每隔2分钟拉取
 */
class TaskService(name: String ="taskService") : IntentService(name) {
    override fun onHandleIntent(intent: Intent?) {
        //方案一
        //登录注册环信
        //当有设备出现故障的时候通过环信发送透传消息

        //方案二
        //开启前台Service每隔5分钟拉取一次服务器的数据
        //当有数据异常的时候，获取是那个学校那个设备出现异常然后推送通知给用户
        //尽可能的保证App的进程一直存活
          //将Service改为前台Service
          //在Service的Destroy的时候发送广播重新拉取Service
          //注册开机或者充电等系统广播，启动Service
          //00000000000000000000000000000000


        //拉取当前账户管理的设备信息
        //首先拉取学校信息 检测是否有异常设备
        //有异常设备在通过学校ID查询是哪一个设备出现故障
        //将故障信息推送通知的方式推送到通知栏
        try {
            Thread.sleep(1000)
            LogUtils.i("TaskService is exec")
        } catch (e:Exception){
        }

    }
}