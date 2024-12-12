package org.dreamerslab.rc3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.dreamerslab.rc3.app.Destination
import org.dreamerslab.rc3.app.Home
import org.dreamerslab.rc3.app.Login
import org.dreamerslab.rc3.app.Navigation
import org.dreamerslab.rc3.design_system.theme.RC3Theme
import org.dreamerslab.rc3.ui.auth.login.LoginScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityState by mutableStateOf(MainActivityState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .onEach { uiState = it }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            uiState is MainActivityState.Loading
        }

        enableEdgeToEdge()
        setContent {
            RC3Theme {
                when (val state = uiState) {
                    MainActivityState.Loading -> Unit
                    is MainActivityState.Success -> {
                        Navigation(
                            startDestination = getStartDestination(state)
                        )
                    }
                }
            }
        }
    }
}

private fun getStartDestination(
    state: MainActivityState.Success
): Destination = when {
    state.firebaseUser == null -> Login
    else -> Home
}
