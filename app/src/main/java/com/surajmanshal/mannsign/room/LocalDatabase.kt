package com.surajmanshal.mannsign.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.surajmanshal.mannsign.room.user.UserDao
import com.surajmanshal.mannsign.room.user.UserEntity
import com.surajmanshal.mannsign.room.wishlist.WishListDao
import com.surajmanshal.mannsign.room.wishlist.WishListItem


@Database(entities = [UserEntity::class,WishListItem::class], version = 1)
abstract class LocalDatabase : RoomDatabase(){

    abstract fun userDao() : UserDao
    abstract fun wishListDao() : WishListDao

    companion object{

        @Volatile
        private var INSTANCE : LocalDatabase? = null

        fun getDatabase(context: Context) : LocalDatabase {

            if(INSTANCE ==null)
            {
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java,
                        "local_db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}