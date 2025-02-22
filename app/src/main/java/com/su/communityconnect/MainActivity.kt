package com.su.communityconnect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.google.android.libraries.places.api.Places
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.ui.navigation.NavGraph
import com.su.communityconnect.ui.theme.CommunityConnectTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var accountService: AccountService

    @Inject
    lateinit var userService: UserService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.PLACES_API_KEY)
        }
        setContent {
            CommunityConnectTheme {
                AppContent(accountService = accountService, userService = userService)
            }
        }
    }
}

@Composable
fun AppContent(accountService: AccountService, userService: UserService) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box {
            NavGraph(accountService = accountService, userService = userService)
        }
    }
}