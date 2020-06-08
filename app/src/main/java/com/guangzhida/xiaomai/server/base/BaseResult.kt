package com.guangzhida.xiaomai.server.base

class BaseResult<T> {
    val status: Int = 0
    val message: String = ""
    val msg: String = ""
    val data: T? = null
    override fun toString(): String {
        return "BaseResult(status=$status, message='$message', msg='$msg', data=$data)"
    }


}