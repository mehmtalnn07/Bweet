package com.mehmetalan.bweet.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE

object SharePreferences {

    @SuppressLint("CommitPrefEdits")
    fun storeData(
        name: String,
        surName: String,
        userName: String,
        bio: String,
        email: String,
        imageUrl: String,
        phoneNumber: String,
        context: Context
    ) {
        val sharedPreferences = context.getSharedPreferences("users", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.putString("surName", surName)
        editor.putString("userName", userName)
        editor.putString("bio", bio)
        editor.putString("email", email)
        editor.putString("imageUrl", imageUrl)
        editor.putString("phoneNumber", phoneNumber)
        editor.apply()
    }

    fun getName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("users", MODE_PRIVATE)
        return sharedPreferences.getString("name", "")!!
    }

    fun getSurName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("users", MODE_PRIVATE)
        return sharedPreferences.getString("surName", "")!!
    }

    fun getUserName(context: Context): String{
        val sharedPreferences = context.getSharedPreferences("users", MODE_PRIVATE)
        return sharedPreferences.getString("userName", "")!!
    }

    fun getBio(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("users", MODE_PRIVATE)
        return sharedPreferences.getString("bio", "")!!
    }

    fun getEmail(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("users", MODE_PRIVATE)
        return sharedPreferences.getString("email", "")!!
    }

    fun getImage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("users", MODE_PRIVATE)
        return sharedPreferences.getString("imageUrl","")!!
    }

    fun getPhoneNumber(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("users", MODE_PRIVATE)
        return sharedPreferences.getString("phoneNumber", "")!!
    }

}