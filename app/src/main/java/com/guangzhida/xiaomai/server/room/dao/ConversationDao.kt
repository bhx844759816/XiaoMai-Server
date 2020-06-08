package com.guangzhida.xiaomai.server.room.dao

import androidx.room.*
import com.guangzhida.xiaomai.server.room.entity.ConversationEntity

@Dao
interface ConversationDao {
    /**
     * 查询全部的会话
     */
    @Query("SELECT * FROM ConversationEntity")
    fun queryAll(): List<ConversationEntity>

    @Query("SELECT * FROM ConversationEntity where userName=:userName")
    fun queryByUserName(userName: String): ConversationEntity?

    /**
     * 插入消息列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: ConversationEntity?)

    /**
     * 更新消息列表
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg users: ConversationEntity?)

    /**
     * 删除消息列表
     */
    @Delete
    fun delete(vararg users: ConversationEntity?)
}