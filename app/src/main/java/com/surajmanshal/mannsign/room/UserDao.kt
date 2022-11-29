package com.surajmanshal.mannsign.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update


@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    fun insertUser(user: UserEntity)

    @Query("delete from userTable where emailId = :email")
    fun deleteUser(email : String)

    @Query("Select * from userTable where emailId = :email")
    fun getUser(email : String) : LiveData<UserEntity>

    @Update(onConflict = REPLACE)
    fun updateUser(user: UserEntity)
}