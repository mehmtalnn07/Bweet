package com.mehmetalan.bweet.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mehmetalan.bweet.model.BottomNavigationItem
import com.mehmetalan.bweet.navigation.Routes
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mehmetalan.bweet.navigation.MyBottomBar
import okhttp3.Route


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavigation(
    navController: NavHostController
) {
    Scaffold (
        bottomBar = {
            MyBottomBar(
                navController = navController
            )
        }
    ) {innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home.routes,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            composable(route = Routes.Home.routes) {
                Home(
                    navController = navController
                )
            }
            composable(route = Routes.Notification.routes) {
                Notification(
                    navController = navController
                )
            }
            composable(route = Routes.Search.routes) {
                Search(
                    navController = navController
                )
            }
            composable(route = Routes.AddBweet.routes) {
                AddBweet(
                    navController = navController
                )
            }
            composable(route = Routes.Profile.routes) {
                Profile(
                    navController = navController
                )
            }
        }
    }
}

