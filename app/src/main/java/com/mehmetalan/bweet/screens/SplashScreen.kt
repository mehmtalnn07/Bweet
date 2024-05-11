package com.mehmetalan.bweet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.bweet.R
import com.mehmetalan.bweet.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun Splash(
    navController: NavHostController
) {

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.twitter),
            contentDescription = "App Logo"
        )
    }

    LaunchedEffect(true) {
        delay(1000)
        if (FirebaseAuth.getInstance().currentUser != null) {
            navController.navigate(route = Routes.BottomNavigation.routes) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        } else {
            navController.navigate(route = Routes.Login.routes) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }
}