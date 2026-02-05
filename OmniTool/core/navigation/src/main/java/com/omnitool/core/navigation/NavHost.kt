package com.omnitool.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.screens.HomeScreen
import com.omnitool.ui.screens.FavoritesScreen
import com.omnitool.ui.screens.VaultScreen
import com.omnitool.ui.screens.SettingsScreen

/**
 * Bottom navigation items
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Route.Home.route,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Route.Favorites.route,
        label = "Favorites",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    BottomNavItem(
        route = Route.Vault.route,
        label = "Vault",
        selectedIcon = Icons.Filled.Lock,
        unselectedIcon = Icons.Outlined.Lock
    ),
    BottomNavItem(
        route = Route.Settings.route,
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

@Composable
fun OmniToolNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Show bottom bar only on main tabs
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }
    
    Scaffold(
        containerColor = OmniToolTheme.colors.background,
        bottomBar = {
            if (showBottomBar) {
                OmniToolBottomBar(
                    navController = navController,
                    currentRoute = currentDestination?.route
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Main tabs
            composable(Route.Home.route) {
                HomeScreen(
                    onToolClick = { route -> navController.navigate(route) }
                )
            }
            composable(Route.Favorites.route) {
                FavoritesScreen(
                    onToolClick = { route -> navController.navigate(route) }
                )
            }
            composable(Route.Vault.route) {
                VaultScreen()
            }
            composable(Route.Settings.route) {
                SettingsScreen()
            }
            
            // Tool screens will be added here as they are implemented
        }
    }
}

@Composable
private fun OmniToolBottomBar(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = OmniToolTheme.colors.surface,
        contentColor = OmniToolTheme.colors.textPrimary
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = OmniToolTheme.typography.caption
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OmniToolTheme.colors.primaryLime,
                    selectedTextColor = OmniToolTheme.colors.primaryLime,
                    unselectedIconColor = OmniToolTheme.colors.textMuted,
                    unselectedTextColor = OmniToolTheme.colors.textMuted,
                    indicatorColor = OmniToolTheme.colors.primaryLime.copy(alpha = 0.15f)
                )
            )
        }
    }
}
