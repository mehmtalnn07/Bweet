package com.mehmetalan.bweet.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.mehmetalan.bweet.model.UserModel
import com.mehmetalan.bweet.utils.SharePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AuthViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    val userReference = db.getReference("users")

    private val storageReference = Firebase.storage.reference
    private val imageReference = storageReference.child("users/${UUID.randomUUID()}.jpg")


    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _firebaseUser.value = auth.currentUser
    }

    fun login(
        email: String,
        password: String,
        context: Context
    ) {
        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _firebaseUser.postValue(auth.currentUser)
                getData(auth.currentUser!!.uid, context = context)
            } else {
                _error.postValue(task.exception!!.message)
            }
        }
    }

    private fun getData(
        uid: String,
        context: Context
    ) {
        userReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserModel::class.java)
                SharePreferences.storeData(userData!!.name, userData!!.surName, userData!!.userName, userData!!.bio, userData!!.email,userData!!.imageUrl, userData!!.phoneNumber, context = context)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun register(
        email: String,
        password: String,
        name: String,
        surName: String,
        bio: String,
        userName: String,
        phoneNumber: String,
        imageUri: Uri,
        context: Context
    ) {
        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _firebaseUser.postValue(auth.currentUser)
                saveImage(
                    name = name,
                    surName = surName,
                    userName = userName,
                    bio = bio,
                    email = email,
                    password = password,
                    imageUri = imageUri,
                    phoneNumber = phoneNumber,
                    uid = auth.currentUser?.uid,
                    context = context
                )
            } else {
                _error.postValue("Something went wrong")
            }
        }
    }

    private fun saveImage(
        name: String,
        surName: String,
        userName: String,
        bio: String,
        email: String,
        password: String,
        imageUri: Uri,
        phoneNumber: String,
        uid: String?,
        context: Context
    ) {
        val uploadTask = imageReference.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            imageReference.downloadUrl.addOnSuccessListener {
                saveData(
                    name = name,
                    surName = surName,
                    userName = userName,
                    bio = bio,
                    email = email,
                    password = password,
                    imageUrl = it.toString(),
                    phoneNumber = phoneNumber,
                    uid = uid,
                    context = context
                )
            }
        }
    }

    private fun saveData(
        name: String,
        surName: String,
        userName: String,
        bio: String,
        email: String,
        password: String,
        imageUrl: String,
        phoneNumber: String,
        uid: String?,
        context: Context
    ) {

        val firestoreDatabase = Firebase.firestore
        val followersReference = firestoreDatabase.collection("followers").document(uid!!)
        val followingReference = firestoreDatabase.collection("following").document(uid)
        followersReference.set(mapOf("followingIds" to listOf<String>()))
        followingReference.set(mapOf("followerIds" to listOf<String>()))
        val userData = UserModel(
            email = email,
            password = password,
            name = name,
            surName = surName,
            bio = bio,
            userName = userName,
            phoneNumber = phoneNumber,
            imageUrl = imageUrl,
            uid = uid!!,
        )
        userReference.child(uid).setValue(userData)
            .addOnSuccessListener {
                SharePreferences.storeData(
                    name = name,
                    surName = surName,
                    userName = userName,
                    bio = bio,
                    email = email,
                    imageUrl = imageUrl,
                    phoneNumber = phoneNumber,
                    context = context
                )
            }
    }

    fun logOut() {
        auth.signOut()
        _firebaseUser.postValue(null)
    }

}