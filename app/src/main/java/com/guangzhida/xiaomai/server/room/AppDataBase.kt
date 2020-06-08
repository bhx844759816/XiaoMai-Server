package com.guangzhida.xiaomai.server.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.guangzhida.xiaomai.server.room.dao.ConversationDao
import com.guangzhida.xiaomai.server.room.entity.ConversationEntity

/**
 * 数据库的管理类
 */
@Database(
    entities = [ConversationEntity::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao?

    companion object {
        @Volatile
        private var instance: AppDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDataBase::class.java, "xiaomai_server.db"
        )
            .allowMainThreadQueries()
            .build()
    }
}