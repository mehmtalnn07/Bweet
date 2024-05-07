package com.mehmetalan.bweet.navigation

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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mehmetalan.bweet.model.BottomNavigationItem
import com.mehmetalan.bweet.screens.AddBweet
import com.mehmetalan.bweet.screens.BookmarkScreen
import com.mehmetalan.bweet.screens.BottomNavigation
import com.mehmetalan.bweet.screens.EditScreen
import com.mehmetalan.bweet.screens.FollowersScreen
import com.mehmetalan.bweet.screens.FollowingsScreen
import com.mehmetalan.bweet.screens.FullScreenImage
import com.mehmetalan.bweet.screens.FullScreenImageOtherUsers
import com.mehmetalan.bweet.screens.FullScreenProfileImage
import com.mehmetalan.bweet.screens.Home
import com.mehmetalan.bweet.screens.LoginScreen
import com.mehmetalan.bweet.screens.Notification
import com.mehmetalan.bweet.screens.OtherUser
import com.mehmetalan.bweet.screens.Profile
import com.mehmetalan.bweet.screens.Register
import com.mehmetalan.bweet.screens.Search
import com.mehmetalan.bweet.screens.Splash
import okhttp3.Route

@Composable
fun NavGraph(
    navController: NavHostController
) {
    // Scaffold içindeki ana yapıyı tanımlayın
    val navController = rememberNavController()

    // Scaffold içindeki ana yapıyı tanımlayın
    Scaffold(
        bottomBar = {
            // Ana NavHostController kullanılarak BottomNavigation ekleyin
            MyBottomBar(navController = navController)
        }
    ) { innerPadding ->
        // Tek NavHostController kullanarak NavHost oluşturun
        NavHost(
            navController = navController,
            startDestination = Routes.Splash.routes,
            modifier = Modifier.padding(innerPadding)  // Padding ekleyin
        ) {
            composable(route = Routes.Splash.routes) {
                Splash(navController = navController)
            }
            composable(route = Routes.Login.routes) {
                LoginScreen(navController = navController)
            }
            composable(route = Routes.Register.routes) {
                Register(navController = navController)
            }
            composable(route = Routes.BottomNavigation.routes) {
                // Bu route'da tekrar scaffold eklenmez, sadece yönlendirme yapılır
                navController.navigate(Routes.Home.routes)  // Varsayılan Home ekranına yönlendirin
            }
            composable(route = Routes.Home.routes) {
                Home(navController = navController)
            }
            composable(route = Routes.Notification.routes) {
                Notification(navController = navController)
            }
            composable(route = Routes.Search.routes) {
                Search(navController = navController)
            }
            composable(route = Routes.AddBweet.routes) {
                AddBweet(navController = navController)
            }
            composable(route = Routes.Profile.routes) {
                Profile(navController = navController)
            }
            composable(route = Routes.OtherUsers.routes){
                val data = it.arguments!!.getString("data")
                OtherUser(
                    navController = navController,
                    data!!
                )
            }
            composable(route = "${Routes.FollowersScreen.routes}/{userId}",
                arguments = listOf(navArgument("userId") {
                    type = NavType.StringType })
            ) {
                val userId = it.arguments?.getString("userId")
                FollowersScreen(
                    userId = userId.orEmpty(),
                    navController = navController
                )
            }
            composable(route = "${Routes.FollowingsScreen.routes}/{userId}",
                arguments = listOf(navArgument("userId") {
                    type = NavType.StringType
                })
            ) {
                val userId = it.arguments?.getString("userId")
                FollowingsScreen(
                    userId = userId.orEmpty(),
                    navController = navController
                )
            }
            composable(route = "fullScreenProfileImage/{imageUrl}",
                arguments = listOf(navArgument("imageUrl") {type = NavType.StringType})
            ) {
                val imageUrl = it.arguments?.getString("imageUrl") ?: ""
                FullScreenProfileImage(
                    imageUrl = imageUrl,
                    navController = navController
                )
            }
            composable(route = "fullScreenProfileImageOtherUsers/{imageUrl}",
                arguments = listOf(navArgument("imageUrl"){ type = NavType.StringType })
            ) {
                val imageUrl = it.arguments?.getString("imageUrl") ?: ""
                FullScreenImageOtherUsers(
                    imageUrl = imageUrl,
                    navController = navController
                )
            }
            composable(route = "fullScreenImage/{imageUrl}",
                arguments = listOf(navArgument("imageUrl"){ type = NavType.StringType })
            ) {
                val imageUrl = it.arguments?.getString("imageUrl") ?: ""
                FullScreenImage(
                    imageUrl,
                    navController
                )
            }
            composable(route = "editScreen") {
                EditScreen(
                    navController = navController
                )
            }
            composable(route = "bookmarkScreen"){
                BookmarkScreen(
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun MyBottomBar(
    navController: NavHostController
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val list = listOf(
        BottomNavigationItem(
            title = "Home",
            route = Routes.Home.routes,
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home
        ),
        BottomNavigationItem(
            title = "Search",
            route = Routes.Search.routes,
            selectedIcon = Icons.Filled.Search,
            unSelectedIcon = Icons.Outlined.Search
        ),
        BottomNavigationItem(
            title = "Add Bweet",
            route = Routes.AddBweet.routes,
            selectedIcon = Icons.Filled.Add,
            unSelectedIcon = Icons.Outlined.Add
        ),
        BottomNavigationItem(
            title = "Notification",
            route = Routes.Notification.routes,
            selectedIcon = Icons.Filled.Notifications,
            unSelectedIcon = Icons.Outlined.Notifications
        ),
        BottomNavigationItem(
            title = "Profile",
            route = Routes.Profile.routes,
            selectedIcon = Icons.Filled.Person,
            unSelectedIcon = Icons.Outlined.Person
        )
    )
    BottomAppBar {
        list.forEach {
            val selected: Boolean = it.route == backStackEntry?.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) it.selectedIcon else it.unSelectedIcon,
                        contentDescription = it.title
                    )
                }
            )
        }
    }
}