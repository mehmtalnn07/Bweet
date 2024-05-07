package com.mehmetalan.bweet.item_view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mehmetalan.bweet.model.BweetModel
import com.mehmetalan.bweet.model.UserModel
import com.mehmetalan.bweet.navigation.Routes
import com.mehmetalan.bweet.viewmodel.BweetItemViewModel
import kotlinx.coroutines.tasks.await

@Composable
fun BweetItem(
    bweet: BweetModel,
    users: UserModel,
    navController: NavHostController,
    userId: String
) {

    val bweetItemViewModel: BweetItemViewModel = viewModel()

    val formattedTimeStamp = bweetItemViewModel.epochToFormattedTiem(bweet.timeStamp)

    val firestoreDb = FirebaseFirestore.getInstance()
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(0) }

    LaunchedEffect(bweet.bweetId) {
        val likeDoc = firestoreDb.collection("likeBweets").document(bweet.bweetId)
        val data = likeDoc.get().await()
        likeCount = data.getLong("likeCount")?.toInt() ?: 0
        isLiked = (data.get("likedBy") as? List<*>)?.contains(userId) ?: false
    }

    var currentUserId = ""

    if (FirebaseAuth.getInstance().currentUser != null) {
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    }

    Column {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (userImage, userUserName, userName, date, content, bweetImage, likeButton, likeNumber, bookmarkButton) = createRefs()
            Image(
                painter = rememberAsyncImagePainter(model = users.imageUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .constrainAs(userImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(36.dp)
                    .clip(shape = CircleShape)
                    .clickable {
                        if (currentUserId == users.uid) {
                            navController.navigate(route = Routes.Profile.routes)
                        } else {
                            val routes = Routes.OtherUsers.routes.replace("{data}", users.uid)
                            navController.navigate(routes)
                        }
                    },
                contentScale = ContentScale.Crop
            )
            Text(
                text = users.name,
                modifier = Modifier
                    .constrainAs(userName) {
                        top.linkTo(userImage.top)
                        start.linkTo(userImage.end, margin = 10.dp)
                    }
                    .clickable {
                        if (currentUserId == users.uid) {
                            navController.navigate(route = Routes.Profile.routes)
                        } else {
                            val routes = Routes.OtherUsers.routes.replace("{data}", users.uid)
                            navController.navigate(routes)
                        }
                    },
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "@${users.userName}",
                modifier = Modifier
                    .constrainAs(userUserName) {
                        top.linkTo(userName.top)
                        start.linkTo(userName.end, margin = 5.dp)
                    }
                    .clickable {
                        if (currentUserId == users.uid) {
                            navController.navigate(route = Routes.Profile.routes)
                        } else {
                            val routes = Routes.OtherUsers.routes.replace("{data}", users.uid)
                            navController.navigate(routes)
                        }
                    },
                fontWeight = FontWeight.ExtraLight
            )
            Text(
                text = formattedTimeStamp,
                modifier = Modifier
                    .constrainAs(date) {
                        top.linkTo(userUserName.top)
                        start.linkTo(userUserName.end, margin = 10.dp)
                    }
            )
            Text(
                text = bweet.bweetContent,
                fontSize = 18.sp,
                modifier = Modifier
                    .constrainAs(content){
                        start.linkTo(userName.start)
                        top.linkTo(userName.bottom, margin = 10.dp)
                    }
            )
            if (bweet.image != "") {
                Card (
                    modifier = Modifier
                        .constrainAs(bweetImage){
                            top.linkTo(content.bottom, margin = 10.dp)
                            start.linkTo(userName.end)
                            end.linkTo(parent.end)
                        }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = bweet.image),
                        contentDescription = "Bweet Pictuer",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable {
                                val encodedUrl = Uri.encode(bweet.image)
                                val route = "fullScreenImage/${encodedUrl}"
                                navController.navigate(route)
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
            IconButton(
                onClick = {
                          isLiked = !isLiked
                    if (isLiked) {
                        likeCount++
                        firestoreDb.collection("likeBweets").document(bweet.bweetId).update(
                            mapOf(
                                "likeCount" to likeCount,
                                "likedBy" to FieldValue.arrayUnion(userId)
                            )
                        )
                    } else {
                        likeCount--
                        firestoreDb.collection("likeBweets").document(bweet.bweetId).update(
                            mapOf(
                                "likeCount" to likeCount,
                                "likedBy" to FieldValue.arrayRemove(userId)
                            )
                        )
                    }
                },
                modifier = Modifier
                    .constrainAs(likeButton) {
                        start.linkTo(content.start, margin = -5.dp)
                        top.linkTo(
                            if (bweet.image != "") {
                                bweetImage.bottom
                            } else {
                                content.bottom
                            },
                            margin = 8.dp
                        )
                    }
                    .size(18.dp)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like Button",
                    modifier = Modifier
                        .size(15.dp),
                    tint = if (isLiked) Color.Red else Color.Black
                )
            }
            Text(
                text = likeCount.toString(),
                modifier = Modifier
                    .constrainAs(likeNumber) {
                        start.linkTo(likeButton.end, margin = 3.dp)
                        top.linkTo(likeButton.top)
                    },
                fontSize = 18.sp,
            )
            IconButton(
                onClick = {  },
                modifier = Modifier
                    .constrainAs(bookmarkButton){
                        start.linkTo(likeNumber.end, margin = 20.dp)
                        top.linkTo(likeButton.top)
                    }
                    .size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark Button",
                    modifier = Modifier
                        .size(15.dp)
                )
            }
        }
        Divider(
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}

@Composable
fun LikeButton(
    bweetId: String,
    userId: String,
    modifier: Modifier = Modifier
) {
    val firestoreDatabase = FirebaseFirestore.getInstance()
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(0) }

    LaunchedEffect(
        bweetId
    ) {
        val likeDoc = firestoreDatabase.collection("likeBweets").document(bweetId)
        val data = likeDoc.get().await()
        likeCount = data.getLong("likeCount")?.toInt() ?: 0
        isLiked = (data.get("likedBy") as? List<*>)?.contains(userId) ?: false
    }

    Row {
        IconButton(
            onClick = {
                isLiked = !isLiked
                if (isLiked) {
                    likeCount++
                    firestoreDatabase.collection("likeBweets").document(bweetId).update(
                        mapOf(
                            "likeCount" to likeCount,
                            "likedBy" to FieldValue.arrayUnion(userId)
                        )
                    )
                } else {
                    likeCount--
                    firestoreDatabase.collection("likeBweets").document(bweetId).update(
                        mapOf(
                            "likeCount" to likeCount,
                            "likedBy" to FieldValue.arrayRemove(userId)
                        )
                    )
                }
            },
            modifier = Modifier
                .padding(start = 10.dp, bottom = 10.dp)
                .size(20.dp)
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite Button"
            )
        }
        Spacer(
            modifier = Modifier
                .width(10.dp)
        )
        Text(
            text = likeCount.toString(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            modifier = Modifier
                .clickable {  }
        )
    }
}