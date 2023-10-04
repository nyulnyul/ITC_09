package com.example.itc_football.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserInfo")
data class RegisterEntity(
    @PrimaryKey(autoGenerate = true) var id : Int? = null,
    @ColumnInfo(name = "name") var name : String,
    @ColumnInfo(name = "email") var email : String,
    @ColumnInfo(name = "department") var department : String,
    @ColumnInfo(name = "password") var password : String,
    @ColumnInfo(name = "checkPassword") var checkPassword : String

)
