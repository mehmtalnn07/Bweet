package com.mehmetalan.bweet.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.More
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.NotificationAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.mehmetalan.bweet.navigation.Routes
import com.mehmetalan.bweet.utils.SharePreferences
import com.mehmetalan.bweet.viewmodel.AuthViewModel
import com.mehmetalan.bweet.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OtherUser(
    navController: NavHostController,
    uid: String
) {
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)

    val context = LocalContext.current

    val userViewModel: UserViewModel = viewModel()
    val bweets by userViewModel.bweets.observeAsState(null)
    val users by userViewModel.users.observeAsState(null)
    val followerList by userViewModel.followerList.observeAsState(null)
    val followingList by userViewModel.followingList.observeAsState(null)

    userViewModel.fetchBweets(uid)
    userViewModel.fetchUser(uid)
    userViewModel.getFollowing(uid)
    userViewModel.getFollowers(uid)

    var currentUserId = ""

    if (FirebaseAuth.getInstance().currentUser != null) {
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
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
                            imageVector = Icons.Default.MoreVert,
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
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    val (userSurName, userImage, userName, userUserName, bio, followButton, notificationButton, messageButton, followingsNumber, followersNumber) = createRefs()
                    Image(
                        painter = rememberAsyncImagePainter(model = users!!.imageUrl),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .constrainAs(userImage) {
                                top.linkTo(parent.top, margin = 25.dp)
                                start.linkTo(parent.start)
                            }
                            .size(64.dp)
                            .clip(shape = CircleShape)
                            .clickable {
                                val encodedUrl = Uri.encode(users!!.imageUrl)
                                val route = "fullScreenProfileImageOtherUsers/${encodedUrl}"
                                navController.navigate(route)
                            },
                        contentScale = ContentScale.Crop
                    )
                    if (uid != currentUserId) {
                        OutlinedButton(
                            onClick = {
                                if (currentUserId.isNotEmpty()) {
                                    if (followerList != null && followerList!!.contains(currentUserId)) {
                                        userViewModel.unFollowUser(userId = uid, currentUserId = currentUserId)
                                    } else {
                                        userViewModel.followUser(userId = uid, currentUserId = currentUserId)
                                    }
                                }
                            },
                            modifier = Modifier
                                .constrainAs(followButton){
                                    end.linkTo(parent.end)
                                    bottom.linkTo(userImage.bottom)
                                }
                        ) {
                            Text(
                                text = if (followerList != null && followerList!!.contains(currentUserId)) {
                                    "Takipten çıkar"
                                } else {
                                    "Takip et"
                                }
                            )
                        }
                        OutlinedButton(
                            onClick = {  },
                            modifier = Modifier
                                .constrainAs(notificationButton){
                                    end.linkTo(followButton.start, margin = 3.dp)
                                    top.linkTo(followButton.top)
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NotificationAdd,
                                contentDescription = "Notification Button",
                            )
                        }
                        OutlinedButton(
                            onClick = {  },
                            modifier = Modifier
                                .constrainAs(messageButton){
                                    end.linkTo(notificationButton.start)
                                    top.linkTo(notificationButton.top)
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Message,
                                contentDescription = "Message Button",
                            )
                        }
                    }
                    Text(
                        text = users!!.name,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .constrainAs(userName){
                                top.linkTo(userImage.bottom, margin = 10.dp)
                                start.linkTo(userImage.start)
                            }
                    )
                    Text(
                        text = users!!.surName,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .constrainAs(userSurName){
                                start.linkTo(userName.end)
                                top.linkTo(userName.top)
                            }
                    )
                    Text(
                        text = "@${users!!.userName}",
                        fontWeight = FontWeight.ExtraLight,
                        modifier = Modifier
                            .constrainAs(userUserName){
                                start.linkTo(userName.start)
                                top.linkTo(userName.bottom)
                            }
                    )
                    Text(
                        text = users!!.bio,
                        modifier = Modifier
                            .constrainAs(bio){
                                start.linkTo(userName.start)
                                top.linkTo(userUserName.bottom, margin = 20.dp)
                            }
                    )
                    Text(
                        text = "${followingList!!.size} Takip edilen",
                        modifier = Modifier
                            .constrainAs(followingsNumber) {
                                start.linkTo(userUserName.start)
                                top.linkTo(bio.bottom, margin = 15.dp)
                            }
                            .clickable {
                                navController.navigate(route = "${Routes.FollowingsScreen.routes}/$uid")
                            }
                    )
                    Text(
                        text = "${followerList!!.size} Takipçi",
                        modifier = Modifier
                            .constrainAs(followersNumber) {
                                start.linkTo(followingsNumber.end, margin = 10.dp)
                                top.linkTo(followingsNumber.top)
                            }
                            .clickable {
                                navController.navigate("${Routes.FollowersScreen.routes}/$uid")
                            }
                    )
                }
            }
            if (bweets != null && users != null) {
                items(bweets ?: emptyList()) { pair ->
                    BweetItem(
                        bweet = pair,
                        users = users!!,
                        navController = navController,
                        userId = SharePreferences.getUserName(context)
                    )
                }
            }
        }
    }

}