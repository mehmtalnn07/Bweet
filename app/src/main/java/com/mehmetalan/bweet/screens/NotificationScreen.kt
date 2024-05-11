package com.mehmetalan.bweet.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mehmetalan.bweet.R

@Composable
fun Notification(
    navController: NavHostController
) {
    Text(
        text = stringResource(id = R.string.notification_screen)
    )
}