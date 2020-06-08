package com.guangzhida.xiaomai.server.event

object LiveDataBusKey {

    const val LOGIN_KEY = "login"
    const val EXIT_LOGIN_KEY = "login"
    const val LOGIN_STATUS_CHANGE = "login_status_change"//登录状态改变
    const val USER_INFO_MODIFY = "user_info_modify" //用户信息改变
    const val NETWORK_CHANGE_KEY = "net_work_change" //网络状态改变
    const val SCHOOL_MODEL_CHANGE_KEY = "school_model_change" //选择学校改变
    const val MESSAGE_COUNT_CHANGE_KEY = "message_count_change" //消息个数改变
    const val ADD_FRIEND_RESULT_KEY = "add_friend_result"//添加好友的结果
    const val CMD_MESSAGE_APPEND_KEY = "cmd_message_append_key"//接收到网络诊断的透传消息
    const val DEVICE_STATE_UPDATE = "device_state_update"//增加设备更新设备
}