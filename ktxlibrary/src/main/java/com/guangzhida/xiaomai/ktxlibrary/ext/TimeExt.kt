package com.guangzhida.xiaomai.ktxlibrary.ext

import java.text.SimpleDateFormat
import java.util.*

fun Long.toTimeStamp(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(format, Locale.CHINA).format(this)
}
