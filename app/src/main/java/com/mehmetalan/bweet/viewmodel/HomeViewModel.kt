package com.mehmetalan.bweet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.mehmetalan.bweet.model.BweetModel
import com.mehmetalan.bweet.model.UserModel

class HomeViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    val bweet = database.getReference("bweets")
    val userReference = database.getReference("users")

    private val _bweetsAndUsers = MutableLiveData<List<Pair<BweetModel, UserModel>>>()
    val bweetsAndUsers: LiveData<List<Pair<BweetModel, UserModel>>> = _bweetsAndUsers

    init {
        fetchBweetsAndUsers {
            _bweetsAndUsers.value = it
        }
    }

    private fun fetchBweetsAndUsers(onResult: (List<Pair<BweetModel, UserModel>>) -> Unit) {
        bweet.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableListOf<Pair<BweetModel, UserModel>>()
                for (bweetSnapshot in snapshot.children) {
                    val bweet = bweetSnapshot.getValue(BweetModel::class.java)
                    bweet.let {
                        fetchUserFromBweet(it!!) {user ->
                            result.add(0, it to user)
                            if (result.size == snapshot.childrenCount.toInt()) {
                                onResult(result)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun fetchUserFromBweet(bweet: BweetModel, onResult: (UserModel) -> Unit) {
        database.getReference("users").child(bweet.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    user?.let(onResult)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

}