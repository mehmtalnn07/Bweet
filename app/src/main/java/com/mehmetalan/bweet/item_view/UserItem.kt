package com.mehmetalan.bweet.item_view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.mehmetalan.bweet.model.UserModel
import com.mehmetalan.bweet.navigation.Routes

@Composable
fun UserItem(
    users: UserModel,
    navController: NavHostController
) {
    Column {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    val routes = Routes.OtherUsers.routes.replace("{data}", users.uid)
                    navController.navigate(routes)
                }
        ) {
            val (userImage, userName, userUserName, userSurName) = createRefs()
            Image(
                painter = rememberAsyncImagePainter(model = users.imageUrl),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .constrainAs(userImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(45.dp)
                    .clip(shape = CircleShape),
                contentScale = ContentScale.Crop
            )
            Text(
                text = users.name,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                modifier = Modifier
                    .constrainAs(userName){
                        top.linkTo(userImage.top)
                        start.linkTo(userImage.end, margin = 10.dp)
                    }
            )
            Text(
                text = users.surName,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                modifier = Modifier
                    .constrainAs(userSurName){
                        top.linkTo(userName.top)
                        start.linkTo(userName.end, margin = 3.dp)
                    }
            )
            Text(
                text = "@${users.userName}",
                fontWeight = FontWeight.ExtraLight,
                fontSize = 18.sp,
                modifier = Modifier
                    .constrainAs(userUserName){
                        start.linkTo(userName.start)
                        top.linkTo(userName.bottom, margin = 5.dp)
                    }
            )
        }
        Divider(
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}