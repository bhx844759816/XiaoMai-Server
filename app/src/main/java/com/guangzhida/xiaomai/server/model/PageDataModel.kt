package com.guangzhida.xiaomai.server.model

/**
 * 分页的model的对象
 */
data class PageDataModel<T>(
    val total: Int,
    val rows: List<T>
)