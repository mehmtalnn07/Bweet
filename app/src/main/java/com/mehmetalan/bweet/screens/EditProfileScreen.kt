package com.mehmetalan.bweet.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.MaterialTheme.colors
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mehmetalan.bweet.R
import com.mehmetalan.bweet.model.UserModel
import com.mehmetalan.bweet.utils.SharePreferences
import com.mehmetalan.bweet.viewmodel.AuthViewModel
import com.mehmetalan.bweet.viewmodel.EditProfileViewModel

@Composable
fun EditScreen(
    navController: NavHostController
) {

    val editProfileViewModel: EditProfileViewModel = viewModel()

    var updateUserName by remember { mutableStateOf("") }
    var updateUserBio by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val currentUser = FirebaseAuth.getInstance().currentUser
    var userId = currentUser?.uid

    if (userId == null) {
        Text(
            text = stringResource(id = R.string.null_user)
        )
        return
    }

    val storage = FirebaseStorage.getInstance()
    val database = FirebaseDatabase.getInstance()
    val userDatabaseReference = database.getReference("users/$userId")

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            editProfileViewModel.selectedImageUri = uri
        }
    }

    val user = UserModel(
        imageUrl = SharePreferences.getImage(context)
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Image(
            painter = if (imageUri == null) rememberAsyncImagePainter(model = user.imageUrl)
            else rememberAsyncImagePainter(model = imageUri),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(96.dp)
                .clip(shape = CircleShape)
                .background(color = Color.LightGray)
                .clickable { launcher.launch("image/*") }
        )
        OutlinedTextField(
            value = updateUserName,
            onValueChange = { updateUserName = it },
            label = {
                Text(
                    text = stringResource(id = R.string.name)
                )
            },
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = updateUserBio,
            onValueChange = { updateUserBio = it },
            label = {
                Text(
                    text = stringResource(id = R.string.bio)
                )
            },
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
        )
        ElevatedButton(
            onClick = {
                editProfileViewModel.updateUserName(updateUserName)
                editProfileViewModel.updateUserBio(updateUserBio)
                editProfileViewModel.updateUserImage()
                navController.popBackStack()
            },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.save),
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}