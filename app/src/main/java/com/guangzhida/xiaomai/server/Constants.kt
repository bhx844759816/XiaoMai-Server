package com.guangzhida.xiaomai.server


/**
 * 保存用户登录的账号
 */
const val USER_MODEL_KEY = "user_model_key"//
const val SCHOOL_NETWORK_SERVER = "school_network_server"//校园网服务器地址
const val SCHOOL_NETWORK_ACCOUNT = "school_network_account"//校园网账号
const val SCHOOL_NETWORK_PASSWORD = "school_network_password"//校园网密码
const val MESSAGE_SETTING_SWITCH = "message_setting_switch"//消息配置显示周期的开关
const val MESSAGE_SETTING_DAY = "message_setting_day"//消息配置显示周期的开关


const val DEFAULT_PAGE_SIZE = 20
const val DEFAULT_CUR_PAGE = 1


//服务器 IP 122.51.167.92

//绑定校园卡账号
//http://yonghu.guangzhida.cn/lfradius/api.php

// 账号
//15093344111  000000

//客服账号
//18736084084  123456

//账户充值
//mAgentWeb?.urlLoader?.postUrl("http://yonghu.guangzhida.cn/lfradius/home.php?a=userlogin&c=login","username=15093344111&password=000000".toByteArray());

//套餐修改
//"success":"1",[0],"id":"16","servername":"促销30M-8折","proxy_type":"","price":"32.00","current_use":"0",[1],"id":"2","servername":"30天（20M网速）","proxy_type":"","price":"30.00","current_use":"0",[2],"id":"7","servername":"15天（20M网速）","proxy_type":"","price":"20.00","current_use":"0",[3],"id":"23","servername":"10天（20M）","proxy_type":"","price":"15.00","current_use":"0",[4],"id":"6","servername":"5天（20M网速）","proxy_type":"","price":"10.00","current_use":"0",[5],"id":"8","servername":"1元体验","proxy_type":"","price":"1.00","current_use":"0",[6],"id":"24","servername":"测试","proxy_type":"",
//"price":"0.10","current_use":"0",[7],"id":"21","servername":"免费用户（1M）","proxy_type":"","price":"0.10","current_use":"0",


/**
 * 拉取会话列表的时候同时开启后台拉取用户信息的任务
 * 每次接收消息得时候拉取用户的信息
 */
