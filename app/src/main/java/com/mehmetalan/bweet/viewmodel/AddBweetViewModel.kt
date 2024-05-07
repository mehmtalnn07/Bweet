package com.mehmetalan.bweet.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.storage
import com.mehmetalan.bweet.model.BweetModel
import java.util.UUID

class AddBweetViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    val userReference = database.getReference("bweets")
    private val storageReference = Firebase.storage.reference
    private val imageReference = storageReference.child("bweets/${UUID.randomUUID()}.jpg")
    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted

    fun saveImage(
        bweet: String,
        userId: String,
        imageUri: Uri
    ) {
        val uploadTask = imageReference.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            imageReference.downloadUrl.addOnSuccessListener {
                saveData(
                    bweet = bweet,
                    userId = userId,
                    image = it.toString()
                )
            }
        }
    }

    fun saveData(
        bweet: String,
        userId: String,
        image: String,
    ) {
        val firestoreDatabase = FirebaseFirestore.getInstance()
        val bweetId = userReference.push().key ?: UUID.randomUUID().toString()
        val bweetData = BweetModel(
            bweetContent = bweet,
            image = image,
            userId = userId,
            timeStamp = System.currentTimeMillis().toString(),
            bweetId = bweetId
        )

        userReference.child(bweetId).setValue(bweetData)
            .addOnSuccessListener {
                _isPosted.postValue(true)
                val likeData = hashMapOf(
                    "likeCount" to 0,
                    "likedBy" to emptyList<String>()
                )
                firestoreDatabase.collection("likeBweets").document(bweetId).set(likeData, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("AddBweetViewModel", "likeBweets koleksiyonuna belge eklendi")
                    }
                    .addOnFailureListener { e ->
                        Log.e("AddThreadViewModel", "likeThreads belgesi eklenemedi: ", e)
                    }
            }.addOnFailureListener {
                _isPosted.postValue(false)
            }
    }
}