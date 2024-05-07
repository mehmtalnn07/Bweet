package com.mehmetalan.bweet.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.mehmetalan.bweet.R
import com.mehmetalan.bweet.item_view.BweetItem
import com.mehmetalan.bweet.navigation.Routes
import com.mehmetalan.bweet.utils.SharePreferences
import com.mehmetalan.bweet.viewmodel.AuthViewModel
import com.mehmetalan.bweet.viewmodel.HomeViewModel
import com.mehmetalan.bweet.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Home(
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val authViewModel: AuthViewModel = viewModel()

    val homeViewModel: HomeViewModel = viewModel()
    val bweetAndUsers by homeViewModel.bweetsAndUsers.observeAsState(null)

    val context = LocalContext.current

    val userViewModel: UserViewModel = viewModel()
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp)
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate(route = Routes.Profile.routes)
                        }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = SharePreferences.getImage(context)),
                            contentDescription = "User Picture",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(shape = CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(13.dp))
                    Column (
                        modifier = Modifier
                            .clickable {
                                navController.navigate(route = Routes.Profile.routes)
                            }
                    ) {
                        Text(
                            text = SharePreferences.getName(context) + " " + SharePreferences.getSurName(context),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "@${SharePreferences.getUserName(context)}",
                            fontWeight = FontWeight.ExtraLight,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "${followingList!!.size} Takip edilen",
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(route = "${Routes.FollowingsScreen.routes}/$currentUserId")
                                }
                        )
                        Text(
                            text = "${followerList!!.size} Takipçi",
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(route = "${Routes.FollowersScreen.routes}/$currentUserId")
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(35.dp))
                    Divider()
                }
                Column (
                    modifier = Modifier
                        .padding(start = 15.dp)
                ) {
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Profil",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 25.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        selected = false,
                        onClick = {
                            navController.navigate(route = Routes.Profile.routes) {
                                navController.graph.startDestinationId
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.PersonOutline,
                                contentDescription = "Home Button",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        },
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Yer işaretleri",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 25.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        selected = false,
                        onClick = {
                                  navController.navigate("bookmarkScreen")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark Button",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Takipçi İstekleri",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 25.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        selected = false,
                        onClick = {  },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.PersonAdd,
                                contentDescription = "Follower Request",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Çıkış Yap",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 25.sp,
                                color = Color.Red
                            )
                        },
                        selected = false,
                        onClick = {
                            authViewModel.logOut()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Logout,
                                contentDescription = "Logout Button",
                                tint = Color.Red,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    )
                }
            }
        }
    ) {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.twitter),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(48.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = SharePreferences.getImage(context)),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(shape = CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {  }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings Button",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(route = Routes.AddBweet.routes)
                    },
                    modifier = Modifier
                        .padding(bottom = 20.dp, end = 8.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PostAdd,
                        contentDescription = "Post Button",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        ) {innerPadding ->
            Column (
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn{
                    items(bweetAndUsers ?: emptyList()) { pairs ->
                        BweetItem(
                            bweet = pairs.first,
                            users = pairs.second,
                            navController = navController,
                            userId = FirebaseAuth.getInstance().currentUser!!.uid
                        )
                    }
                }
            }
        }
    }
}