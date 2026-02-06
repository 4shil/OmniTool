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

// Type aliases for cleaner route usage
typealias TextToolRoute = Route
typealias ConverterToolRoute = Route

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
            
            // Text Tools
            composable(TextToolRoute.JsonFormatter.route) {
                com.omnitool.features.text.json.JsonFormatterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.Base64.route) {
                com.omnitool.features.text.base64.Base64Screen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.HashGenerator.route) {
                com.omnitool.features.text.hash.HashGeneratorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.CaseConverter.route) {
                com.omnitool.features.text.caseconverter.CaseConverterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.WordCounter.route) {
                com.omnitool.features.text.wordcount.WordCountScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.LoremIpsum.route) {
                com.omnitool.features.text.lorem.LoremIpsumScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.DuplicateRemover.route) {
                com.omnitool.features.text.duplicateremover.DuplicateRemoverScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.WhitespaceCleaner.route) {
                com.omnitool.features.text.whitespace.WhitespaceCleanerScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.RegexTester.route) {
                com.omnitool.features.text.regex.RegexTesterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.RandomString.route) {
                com.omnitool.features.text.randomstring.RandomStringScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.TextDiff.route) {
                com.omnitool.features.text.textdiff.TextDiffScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.XmlFormatter.route) {
                com.omnitool.features.text.xml.XmlFormatterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.MarkdownPreview.route) {
                com.omnitool.features.text.markdown.MarkdownPreviewScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.Scratchpad.route) {
                com.omnitool.features.text.scratchpad.ScratchpadScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.HtmlPreview.route) {
                com.omnitool.features.text.html.HtmlPreviewScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.CsvViewer.route) {
                com.omnitool.features.text.csv.CsvViewerScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(TextToolRoute.ClipboardHistory.route) {
                com.omnitool.features.text.clipboard.ClipboardHistoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Converter Tools
            composable(ConverterToolRoute.UnitConverter.route) {
                com.omnitool.features.converter.unit.UnitConverterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ConverterToolRoute.TimezoneConverter.route) {
                com.omnitool.features.converter.timezone.TimezoneConverterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ConverterToolRoute.DateCalculator.route) {
                com.omnitool.features.converter.date.DateCalculatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ConverterToolRoute.AgeCalculator.route) {
                com.omnitool.features.converter.age.AgeCalculatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ConverterToolRoute.BaseConverter.route) {
                com.omnitool.features.converter.base.BaseConverterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ConverterToolRoute.ColorConverter.route) {
                com.omnitool.features.converter.color.ColorConverterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ConverterToolRoute.StorageConverter.route) {
                com.omnitool.features.converter.storage.StorageConverterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ConverterToolRoute.TemperatureConverter.route) {
                com.omnitool.features.converter.temperature.TemperatureConverterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Security Tools
            composable(Route.PasswordGenerator.route) {
                com.omnitool.features.security.password.PasswordGeneratorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.PasswordStrength.route) {
                com.omnitool.features.security.strength.PasswordStrengthScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.RandomNumber.route) {
                com.omnitool.features.security.random.RandomNumberScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.OtpGenerator.route) {
                com.omnitool.features.security.otp.OtpGeneratorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Utility Tools
            composable(Route.Stopwatch.route) {
                com.omnitool.features.utilities.stopwatch.StopwatchScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.Timer.route) {
                com.omnitool.features.utilities.timer.TimerScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.Calculator.route) {
                com.omnitool.features.utilities.calculator.CalculatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.DiceRoller.route) {
                com.omnitool.features.utilities.dice.DiceRollerScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.ColorPicker.route) {
                com.omnitool.features.utilities.colorpicker.ColorPickerScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.TipCalculator.route) {
                com.omnitool.features.utilities.tip.TipCalculatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // File & Media Tools
            composable(Route.QrGenerator.route) {
                com.omnitool.features.file.qrcode.QrGeneratorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.ImageCompress.route) {
                com.omnitool.features.file.image.ImageCompressScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Utility Tools
            composable(Route.QuickNotes.route) {
                com.omnitool.features.utilities.notes.QuickNotesScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.ExifViewer.route) {
                com.omnitool.features.file.exif.ExifViewerScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.PdfViewer.route) {
                com.omnitool.features.file.pdf.PdfViewerScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.Flashlight.route) {
                com.omnitool.features.utilities.flashlight.FlashlightScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Route.SoundMeter.route) {
                com.omnitool.features.utilities.soundmeter.SoundMeterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
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
