package com.omnitool.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.app.navigation.OmniToolNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity - Single activity architecture.
 * All navigation handled via Compose Navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            OmniToolTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = OmniToolTheme.colors.background
                ) {
                    OmniToolNavHost()
                }
            }
        }
    }
}
