package com.mehmetalan.bweet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mehmetalan.bweet.model.BweetModel
import com.mehmetalan.bweet.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow

class UserViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    val bweetReference = database.getReference("bweets")
    val userReference = database.getReference("users")

    private val _bweets = MutableLiveData(listOf<BweetModel>())
    val bweets : LiveData<List<BweetModel>> get() = _bweets

    private val _users = MutableLiveData(UserModel())
    val users : LiveData<UserModel> get() = _users

    private val _userDetailsList = MutableLiveData<List<UserModel>>()
    val userDetailsList: MutableLiveData<List<UserModel>> = _userDetailsList

    private val _followerList = MutableLiveData(listOf<String>())
    val followerList: LiveData<List<String>> get() = _followerList

    private val _followingList = MutableLiveData(listOf<String>())
    val followingList: LiveData<List<String>> get() = _followingList



    fun fetchUser(uid: String) {
        userReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                _users.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun fetchBweets(uid: String) {
        bweetReference.orderByChild("userId").equalTo(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bweetList = snapshot.children.mapNotNull {
                    it.getValue(BweetModel::class.java)
                }
                _bweets.postValue(bweetList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    val realtimeDatabase = FirebaseDatabase.getInstance()

    fun fetchUserDetails(userIds : List<String>) {
        val userDetails = mutableListOf<UserModel>()
        for (id in userIds) {
            realtimeDatabase.getReference("users").child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(UserModel::class.java)
                        if (user != null) {
                            userDetails.add(user)
                        }
                        if (userDetails.size == userIds.size) {
                            _userDetailsList.postValue(userDetails)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("RealtimeDatabaseError", "Error fetching user details: ${error.message}")
                    }
                })
        }
    }

    val firestoreDatabase = Firebase.firestore

    fun followUser(userId: String, currentUserId: String) {
        val followingReference = firestoreDatabase.collection("following").document(currentUserId)
        val followerReference = firestoreDatabase.collection("followers").document(userId)

        followingReference.update("followingIds", FieldValue.arrayUnion(userId))
        followerReference.update("followerIds", FieldValue.arrayUnion(currentUserId))
    }

    fun unFollowUser(userId: String, currentUserId: String) {
        val followingReference = firestoreDatabase.collection("following").document(currentUserId)
        val followerReference = firestoreDatabase.collection("followers").document(userId)

        followingReference.update("followingIds", FieldValue.arrayRemove(userId))
        followerReference.update("followerIds", FieldValue.arrayRemove(currentUserId))
    }

    fun getFollowers(userId: String) {
        firestoreDatabase.collection("followers").document(userId)
            .addSnapshotListener { value, error ->
                val followerIds = value?.get("followerIds") as? List<String> ?: listOf()
                _followerList.postValue(followerIds)
                if (followerIds.isNotEmpty()) {
                    fetchUserDetails(followerIds)
                }
            }
    }

    fun getFollowing(userId: String) {
        firestoreDatabase.collection("following").document(userId)
            .addSnapshotListener { value, error ->
                val followingIds = value?.get("followingIds") as? List<String> ?: listOf()
                _followingList.postValue(followingIds)
                if (followingIds.isNotEmpty()) {
                    fetchUserDetails(followingIds)
                }
            }
    }

}