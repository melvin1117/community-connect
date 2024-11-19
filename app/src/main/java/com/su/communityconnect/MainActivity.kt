package com.su.communityconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.su.communityconnect.navigation.NavGraph
import com.su.communityconnect.ui.theme.CommunityConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommunityConnectTheme {
                NavGraph() // Integrate navigation graph
            }
        }
    }
}
