package com.mehmetalan.bweet.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mehmetalan.bweet.model.UserModel

class SearchViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    val users = database.getReference("users")

    private val _users = MutableLiveData<List<UserModel>>()
    val userList: MutableLiveData<List<UserModel>> = _users

    init {
        fetchUsers {
            _users.value = it
        }
    }

    private fun fetchUsers(onResult: (List<UserModel>) -> Unit) {
        users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableListOf<UserModel>()
                for (bweetSnapshot in snapshot.children) {
                    val bweet = bweetSnapshot.getValue(UserModel::class.java)
                    result.add(bweet!!)
                }
                onResult(result)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}