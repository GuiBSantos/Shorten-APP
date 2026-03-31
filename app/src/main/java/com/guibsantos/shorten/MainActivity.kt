package com.guibsantos.shorten

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.guibsantos.shorten.data.local.TokenManager
import com.guibsantos.shorten.ui.screens.home.EncurtadorScreen
import com.guibsantos.shorten.ui.screens.login.LoginScreen
import com.guibsantos.shorten.ui.screens.profile.ProfileScreen
import com.guibsantos.shorten.ui.screens.register.RegisterScreen
import com.guibsantos.shorten.ui.screens.Screen
import com.guibsantos.shorten.ui.screens.splash.SplashScreen
import com.guibsantos.shorten.ui.screens.login.LoginViewModel
import com.guibsantos.shorten.ui.screens.profile.ProfileViewModel
import com.guibsantos.shorten.ui.screens.register.RegisterViewModel
import com.guibsantos.shorten.ui.screens.home.ShortenerViewModel
import com.guibsantos.shorten.ui.theme.ShortenerAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val shortenerViewModel: ShortenerViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val tokenManager = TokenManager(this)

        if (!tokenManager.isRememberMe()) {
            tokenManager.clearToken()
        }

        val startScreen = if (tokenManager.getToken() != null) Screen.HOME else Screen.LOGIN

        setContent {
            var showSplash by remember { mutableStateOf(true) }
            var currentScreen by remember { mutableStateOf(startScreen) }

            ShortenerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSplash) {
                        SplashScreen(
                            onNavigateToNext = { showSplash = false },
                            isAppDarkTheme = true
                        )
                    } else {
                        AnimatedContent(
                            targetState = currentScreen,
                            label = "Screen Transition",
                            transitionSpec = {
                                slideInHorizontally(
                                    animationSpec = tween(500),
                                    initialOffsetX = { fullWidth -> fullWidth }
                                ) + fadeIn(animationSpec = tween(500)) togetherWith
                                        slideOutHorizontally(
                                            animationSpec = tween(500),
                                            targetOffsetX = { fullWidth -> -fullWidth }
                                        ) + fadeOut(animationSpec = tween(500))
                            }
                        ) { targetScreen ->
                            when (targetScreen) {
                                Screen.LOGIN -> LoginScreen(
                                    viewModel = loginViewModel,
                                    onLoginSuccess = {
                                        loginViewModel.resetState()
                                        currentScreen = Screen.HOME
                                    },
                                    onNavigateToRegister = {
                                        loginViewModel.resetState()
                                        registerViewModel.resetState()
                                        currentScreen = Screen.REGISTER
                                    }
                                )

                                Screen.REGISTER -> RegisterScreen(
                                    viewModel = registerViewModel,
                                    onRegisterSuccess = {
                                        if (tokenManager.getToken() != null) {
                                            registerViewModel.resetState()
                                            currentScreen = Screen.HOME
                                        } else {
                                            registerViewModel.resetState()
                                            currentScreen = Screen.LOGIN
                                        }
                                    },
                                    onBackToLogin = {
                                        registerViewModel.resetState()
                                        currentScreen = Screen.LOGIN
                                    }
                                )

                                Screen.HOME -> EncurtadorScreen(
                                    viewModel = shortenerViewModel,
                                    onNavigateToProfile = { currentScreen = Screen.PROFILE },
                                    isDarkTheme = true
                                )

                                Screen.PROFILE -> ProfileScreen(
                                    viewModel = profileViewModel,
                                    onBack = { currentScreen = Screen.HOME },
                                    onLogout = {
                                        profileViewModel.logout()
                                        loginViewModel.resetState()
                                        shortenerViewModel.clearState()
                                        currentScreen = Screen.LOGIN
                                    },
                                    isDarkTheme = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
