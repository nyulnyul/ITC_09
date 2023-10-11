package com.example.itc_football

data class Chat(
    val username: String,
    val text: String,
    val isSelf: Boolean = false
) {
}