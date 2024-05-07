package com.mehmetalan.bweet.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.MaterialTheme.colors
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.bweet.navigation.Routes
import com.mehmetalan.bweet.utils.SharePreferences
import com.mehmetalan.bweet.viewmodel.AddBweetViewModel
import com.mehmetalan.bweet.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddBweet(
    navController: NavHostController
) {

    val addBweetViewModel: AddBweetViewModel = viewModel()
    val isPosted by addBweetViewModel.isPosted.observeAsState(false)

    var imageUris by remember { mutableStateOf(emptyList<Uri>()) }

    val context = LocalContext.current

    val contentResolver = context.contentResolver

    val focusRequester = remember { FocusRequester() }

    var postContent by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {uri: Uri? ->
        imageUri = uri
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
            isGranted: Boolean ->
        if (isGranted) {

        } else {

        }
    }

    LaunchedEffect(
        isPosted
    ) {
        if (isPosted!!) {
            postContent = ""
            imageUri = null
            Toast.makeText(context, "Bweet Added", Toast.LENGTH_SHORT).show()

            navController.navigate(route = Routes.Home.routes) {
                popUpTo(Routes.AddBweet.routes){
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        imageUris = getGalleryImages(contentResolver)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        //keyboardController?.showSoftwareKeyboard()
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {  },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(route = Routes.Home.routes)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Button"
                        )
                    }
                },
                actions = {
                    ElevatedButton(
                        onClick = {
                                  if (imageUri == null) {
                                      addBweetViewModel.saveData(postContent, FirebaseAuth.getInstance().currentUser!!.uid, "")
                                  } else {
                                      addBweetViewModel.saveImage(postContent, FirebaseAuth.getInstance().currentUser!!.uid, imageUri!!)
                                  }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground
                        ),
                        enabled = if (postContent.isEmpty() && imageUri == null) {false} else {true}
                    ) {
                        Text(
                            text = "Gönderi",
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 15.dp, top = 20.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp)
                ) {
                    Column {
                        Image(
                            painter = rememberAsyncImagePainter(model = SharePreferences.getImage(context)),
                            contentDescription = "User Picture",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(shape = CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                val isGranted = ContextCompat.checkSelfPermission(
                                    context, permissionToRequest
                                ) == PackageManager.PERMISSION_GRANTED
                                if (isGranted) {
                                    launcher.launch("image/*")
                                } else {
                                    permissionLauncher.launch(permissionToRequest)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AttachFile,
                                contentDescription = "Select Picture"
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Column (
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        Column{
                            OutlinedTextField(
                                value = postContent,
                                onValueChange = { postContent = it },
                                placeholder = {
                                    Text(
                                        text = "Postunuzu Giriniz"
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                keyboardOptions = KeyboardOptions.Default,
                                keyboardActions = KeyboardActions.Default,
                            )
                            if (imageUri != null) {
                                Box(
                                    modifier = Modifier
                                        .background(color = Color.Transparent)
                                        .padding(1.dp)
                                        .height(250.dp)
                                        .fillMaxWidth()
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = imageUri),
                                        contentDescription = "Post Picture",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Image",
                                        modifier = Modifier
                                            .align(alignment = Alignment.TopEnd)
                                            .clickable {
                                                imageUri = null
                                            }
                                    )
                                }
                            }
                        }
                        if (imageUris.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(top = 450.dp)
                            ) {
                                items(imageUris.take(20)) { uri ->
                                    Image(
                                        painter = rememberAsyncImagePainter(model = uri),
                                        contentDescription = "Gallery Image",
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                imageUri = uri // Tıklanan resmi imageUri'ye atıyoruz
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                item {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .background(Color.Transparent)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                galleryLauncher.launch(arrayOf("image/*"))
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Photo,
                                            contentDescription = "Gallery Button",
                                            modifier = Modifier
                                                .size(120.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text("Galeride resim yok veya erişim izni verilmedi.")
                        }
                    }
                }
            }
        }
    }
}

fun getGalleryImages(contentResolver: ContentResolver): List<Uri> {
    val uriList = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Images.Media._ID)

    val cursor = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        "${MediaStore.Images.Media.DATE_TAKEN} DESC"
    )

    cursor?.use {
        val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (it.moveToNext()) {
            val id = it.getLong(idIndex)
            val imageUri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )
            uriList.add(imageUri)
        }
    }

    // İlk 20 öğeyi alıyoruz
    return uriList.take(20)
}
