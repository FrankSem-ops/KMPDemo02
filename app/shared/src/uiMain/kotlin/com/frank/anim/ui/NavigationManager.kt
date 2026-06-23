package com.frank.anim.ui

import androidx.compose.runtime.*
import com.frank.anim.ui.kmp.KmpFeatureMenuPage
import com.frank.anim.ui.kmp.KmpTestPage

sealed class Screen {
    object MainEntry : Screen()
    object AnimationHome : Screen()
    object LikeAnimationDemo : Screen()
    object AnimatedVisibilityTest : Screen()
    object AnimatedContentTest : Screen()
    object AnimateAsStateTest : Screen()
    object AnimatableTest : Screen()
    object KmpMenu : Screen()
    object KmpTest : Screen()
    object Gallery : Screen()
    object GalleryFromKmp : Screen()
}

@Composable
fun rememberNavigationState(): NavigationState {
    return remember { NavigationState() }
}

class NavigationState {
    private val _currentScreen = mutableStateOf<Screen>(Screen.MainEntry)
    val currentScreen: State<Screen> = _currentScreen
    
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }
    
    fun navigateBack() {
        when (_currentScreen.value) {
            Screen.MainEntry -> Unit
            Screen.AnimationHome -> _currentScreen.value = Screen.MainEntry
            Screen.KmpMenu -> _currentScreen.value = Screen.MainEntry
            Screen.KmpTest -> _currentScreen.value = Screen.KmpMenu
            Screen.GalleryFromKmp -> _currentScreen.value = Screen.KmpMenu
            Screen.Gallery -> _currentScreen.value = Screen.MainEntry
            Screen.LikeAnimationDemo,
            Screen.AnimatedVisibilityTest,
            Screen.AnimatedContentTest,
            Screen.AnimateAsStateTest,
            Screen.AnimatableTest -> _currentScreen.value = Screen.AnimationHome
        }
    }
}

@Composable
fun AppNavigation() {
    val navigationState = rememberNavigationState()
    
    when (navigationState.currentScreen.value) {
        Screen.MainEntry -> {
            MainEntryPage(
                onNavigateToAnimationDemo = {
                    navigationState.navigateTo(Screen.AnimationHome)
                },
                onNavigateToKmpDemo = {
                    navigationState.navigateTo(Screen.KmpMenu)
                }
            )
        }
        Screen.AnimationHome -> {
            HomePage(
                onNavigateBack = { navigationState.navigateBack() },
                onNavigateToDemo = { navigationState.navigateTo(Screen.LikeAnimationDemo) },
                onNavigateToAnimatedVisibility = { navigationState.navigateTo(Screen.AnimatedVisibilityTest) },
                onNavigateToAnimatedContent = { navigationState.navigateTo(Screen.AnimatedContentTest) },
                onNavigateToAnimateAsState = { navigationState.navigateTo(Screen.AnimateAsStateTest) },
                onNavigateToAnimatable = { navigationState.navigateTo(Screen.AnimatableTest) }
            )
        }
        Screen.LikeAnimationDemo -> {
            LikeAnimationDemo(
                onBack = {
                    navigationState.navigateBack()
                }
            )
        }
        Screen.AnimatedVisibilityTest -> {
            AnimatedVisibilityTestPage(
                onBack = {
                    navigationState.navigateBack()
                }
            )
        }
        Screen.AnimatedContentTest -> {
            AnimatedContentTestPage(
                onBack = {
                    navigationState.navigateBack()
                }
            )
        }
        Screen.AnimateAsStateTest -> {
            AnimateAsStateTestPage(
                onNavigateBack = {
                    navigationState.navigateBack()
                }
            )
        }
        Screen.AnimatableTest -> {
            AnimatableTestPage(
                onBack = {
                    navigationState.navigateBack()
                }
            )
        }
        Screen.KmpMenu -> {
            KmpFeatureMenuPage(
                onBack = { navigationState.navigateBack() },
                onNavigateToCommonLibs = { navigationState.navigateTo(Screen.KmpTest) },
                onNavigateToGallery = { navigationState.navigateTo(Screen.GalleryFromKmp) }
            )
        }
        Screen.KmpTest -> {
            KmpTestPage(
                onBack = { navigationState.navigateBack() }
            )
        }
        Screen.Gallery -> {
            GalleryPage(
                onBack = { navigationState.navigateBack() }
            )
        }
        Screen.GalleryFromKmp -> {
            GalleryPage(
                onBack = { navigationState.navigateBack() }
            )
        }
    }
}
