package com.mehmetalan.bweet.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.bweet.item_view.BweetItem
import com.mehmetalan.bweet.model.UserModel
import com.mehmetalan.bweet.navigation.Routes
import com.mehmetalan.bweet.utils.SharePreferences
import com.mehmetalan.bweet.viewmodel.AuthViewModel
import com.mehmetalan.bweet.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Profile(
    navController: NavHostController
) {
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)

    val context = LocalContext.current

    val userViewModel: UserViewModel = viewModel()
    val bweets by userViewModel.bweets.observeAsState(null)

    val followerList by userViewModel.followerList.observeAsState(null)
    val followingList by userViewModel.followingList.observeAsState(null)

    var currentUserId = ""
    if (FirebaseAuth.getInstance().currentUser != null) {
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    }
    if (currentUserId != "") {
        userViewModel.getFollowers(currentUserId)
        userViewModel.getFollowing(currentUserId)
    }

    val user = UserModel(
        name = SharePreferences.getName(context),
        surName = SharePreferences.getSurName(context),
        userName = SharePreferences.getUserName(context),
        bio = SharePreferences.getBio(context),
        imageUrl = SharePreferences.getImage(context)
    )

    if (firebaseUser != null) {
        userViewModel.fetchBweets(uid = firebaseUser!!.uid)
    }

    LaunchedEffect(
        firebaseUser
    ) {
        if (firebaseUser == null) {
            navController.navigate(route = Routes.Login.routes){
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(route = Routes.Home.routes){
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {  }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search Button"
                        )
                    }
                    IconButton(
                        onClick = {  }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "More Button"
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
        LazyColumn (
            modifier = Modifier
                .padding(innerPadding)
        ) {
            item {
                ConstraintLayout (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    val (userName, userSurName, userUserName, userBio, userImage, userFollowersNumber, userFollowingsNumber, likeButton, editButton) = createRefs()
                    Image(
                        painter = rememberAsyncImagePainter(model = SharePreferences.getImage(context)),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .constrainAs(userImage) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top, margin = 25.dp)
                            }
                            .size(64.dp)
                            .clip(shape = CircleShape)
                            .clickable {
                                       val encodedUrl = Uri.encode(user.imageUrl)
                                val route = "fullScreenProfileImage/${encodedUrl}"
                                navController.navigate(route)
                            },
                        contentScale = ContentScale.Crop
                    )
                    OutlinedButton(
                        onClick = {
                                  navController.navigate(route = "editScreen")
                        },
                        modifier = Modifier
                            .constrainAs(editButton){
                                end.linkTo(parent.end)
                                bottom.linkTo(userImage.bottom)
                            }
                    ) {
                        Text(
                            text = "Profili düzenle"
                        )
                    }
                    Text(
                        text = SharePreferences.getName(context),
                        modifier = Modifier
                            .constrainAs(userName){
                                top.linkTo(userImage.bottom, margin = 10.dp)
                                start.linkTo(userImage.start)
                            },
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = SharePreferences.getSurName(context),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .constrainAs(userSurName){
                                top.linkTo(userName.top)
                                start.linkTo(userName.end, margin = 5.dp)
                            }
                    )
                    Text(
                        text = "@${SharePreferences.getUserName(context)}",
                        fontWeight = FontWeight.ExtraLight,
                        modifier = Modifier
                            .constrainAs(userUserName){
                                start.linkTo(userName.start)
                                top.linkTo(userName.bottom, margin = 5.dp)
                            }
                    )
                    Text(
                        text = SharePreferences.getBio(context),
                        modifier = Modifier
                            .constrainAs(userBio){
                                start.linkTo(userUserName.start)
                                top.linkTo(userUserName.bottom, margin = 10.dp)
                            }
                    )
                    Text(
                        text = "${followingList!!.size} Takip edilen",
                        modifier = Modifier
                            .constrainAs(userFollowingsNumber){
                                start.linkTo(userBio.start)
                                top.linkTo(userBio.bottom, margin = 20.dp)
                            }
                            .clickable {
                                navController.navigate(route = "${Routes.FollowingsScreen.routes}/$currentUserId")
                            }
                    )
                    Text(
                        text = "${followerList!!.size} Takipçi",
                        modifier = Modifier
                            .constrainAs(userFollowersNumber){
                                start.linkTo(userFollowingsNumber.end, margin = 10.dp)
                                top.linkTo(userFollowingsNumber.top)
                            }
                            .clickable {
                                navController.navigate(route = "${Routes.FollowersScreen.routes}/$currentUserId")
                            }
                    )
                }
            }
            items(bweets ?: emptyList()) { pair ->
                BweetItem(
                    bweet = pair,
                    users = user,
                    navController = navController,
                    userId = SharePreferences.getUserName(context)
                )
            }
        }
    }
}

fun getUserModel(context: Context): UserModel {
    val sharedPreferences = context.getSharedPreferences("users", Context.MODE_PRIVATE)
    return UserModel(
        name = sharedPreferences.getString("name", "") ?: "",
        surName = sharedPreferences.getString("surName", "") ?: "",
        userName = sharedPreferences.getString("userName", "") ?: "",
        bio = sharedPreferences.getString("bio", "") ?: "",
        imageUrl = sharedPreferences.getString("imageUrl", "") ?: ""
    )
}