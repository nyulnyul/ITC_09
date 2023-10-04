package com.example.itc_football.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegisterDao {

    @Query("SELECT email FROM userInfo")
    fun getEmailList(): List<String>    // 등록된 회원인지 확인

    @Query("SELECT password FROM userinfo WHERE email = :email")    // 이메일에 따른 비밀번호 반환
    fun getPasswordByEmail(email: String): String

    @Insert
    fun insertUser(userInfo: RegisterEntity)    // 회원 등록

    @Query("DELETE FROM userinfo WHERE email = :email AND password = :password")
    fun deleteUser(email: String, password: String)    // 회원 삭제
}