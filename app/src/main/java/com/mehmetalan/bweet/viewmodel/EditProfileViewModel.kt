package com.mehmetalan.bweet.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val userReference = database.getReference("users/$userId")

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var selectedImageUri by mutableStateOf<Uri?>(null)
    var buttonText by mutableStateOf("Düzenle")

    private val storage = FirebaseStorage.getInstance()

    fun updateImage() {
        val storageReference = storage.reference.child("users/$userId.jpg")

        viewModelScope.launch {
            selectedImageUri?.let { uri ->
                storageReference.putFile(uri).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnCompleteListener { downloadTask ->
                            if (downloadTask.isSuccessful) {
                                val downloadUrl = downloadTask.result
                                userReference.child("imageUrl").setValue(downloadUrl.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    fun uploadImage(userId: String?, navController: NavHostController) {
        if (userId == null || selectedImageUri == null) return
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("users/$userId.jpg")
        val database = FirebaseDatabase.getInstance()
        val userDatabaseReference = database.getReference("users/$userId")

        viewModelScope.launch {
            storageReference.putFile(selectedImageUri!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnCompleteListener { downloadTask ->
                            if (downloadTask.isSuccessful) {
                                val downloadUrl = downloadTask.result
                                userDatabaseReference.child("imageUrl").setValue(downloadUrl.toString())
                                navController.popBackStack()
                            }
                        }
                    }
                }
        }
    }

    fun updateUserImage() {

    }

    fun updateUserBio(bio: String) {

        viewModelScope.launch {
            try {
                userReference.child("bio").setValue(bio)
                errorMessage.value = null
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }

    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            try {
                userReference.child("name").setValue(name)
                errorMessage.value = null
            } catch (e: Exception) {
                errorMessage.value = e.message
            }
        }
    }

}