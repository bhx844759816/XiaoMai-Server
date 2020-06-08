package com.guangzhida.xiaomai.ktxlibrary.ext

import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by luyao
 * on 2019/7/23 9:25
 */

/**
 * if [String.isNullOrEmpty], invoke f()
 * otherwise invoke t()
 */
fun <T> String?.notNull(f: () -> T, t: () -> T): T {

    return if (isNullOrEmpty()) f() else t()
}

/**
 * whether string only contains digits
 */
fun String.areDigitsOnly() = matches(Regex("[0-9]+"))


/**
 *手机号号段校验，
 *第1位：1；
 *第2位：{3、4、5、6、7、8}任意数字；
 *第3—11位：0—9任意数字
 */
fun String.isPhone(): Boolean {
    if (this.length == 11) {
        val compiler = Pattern.compile("^1[3|4|5|6|7|8|9][0-9]\\d{8}$")
        val matcher = compiler.matcher(this)
        return matcher.matches()
    }
    return false
}

/**
 *
 */

