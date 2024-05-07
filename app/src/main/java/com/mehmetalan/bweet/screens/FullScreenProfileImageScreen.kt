package com.mehmetalan.bweet.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mehmetalan.bweet.viewmodel.EditProfileViewModel
import kotlinx.coroutines.delay

@Composable
fun FullScreenProfileImage(
    imageUrl: String,
    navController: NavHostController
) {

    val editProfileViewModel: EditProfileViewModel = viewModel()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    if (userId == null) {
        Text(
            text = "Kullanıcı oturum açmamış"
        )
        return
    }

    var isDialogOpen by remember { mutableStateOf(false) }

    var shouldResetButtonText by remember { mutableStateOf(false) }

    var buttonText by remember { mutableStateOf("Düzenle") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {uri: Uri? ->
        if (uri != null) {
            editProfileViewModel.selectedImageUri = uri
            editProfileViewModel.buttonText = "Kayıt Et"
        }
    }

    LaunchedEffect(shouldResetButtonText) {
        if (shouldResetButtonText) {
            delay(2000)
            buttonText = "Düzenle"
            shouldResetButtonText = false
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = editProfileViewModel.selectedImageUri ?: imageUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(shape = CircleShape)
                    .clickable {
                        navController.popBackStack()
                    },
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button"
                )
            }
            OutlinedButton(
                onClick = {
                    if (editProfileViewModel.buttonText == "Kayıt Et") {
                        editProfileViewModel.uploadImage(userId, navController)
                        navController.popBackStack()
                    } else {
                        isDialogOpen = true
                    }
                },
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = editProfileViewModel.buttonText
                )
            }
            if (isDialogOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0x80000000))
                        .clickable {
                            isDialogOpen = false
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(color = Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Text(
                                text = "Fotoğraf Çek",
                                modifier = Modifier
                                    .clickable { isDialogOpen = false }
                                    .padding(16.dp)
                            )
                            Divider()
                            Text(
                                text = "Mevcut Fotoğraflardan Seç",
                                modifier = Modifier
                                    .clickable {
                                        galleryLauncher.launch("image/*")
                                        isDialogOpen = false
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}