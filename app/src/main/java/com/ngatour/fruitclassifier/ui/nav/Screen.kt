package com.ngatour.fruitclassifier.ui.nav

sealed class Screen(val route: String) {
    object Classify : Screen("classify")
    object History : Screen("history")
    object Live : Screen("live")
    object Result : Screen("results")
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Register : Screen("register")
    object Splash : Screen("splash")
    object AboutApp : Screen("about_app")
    object AboutDeveloper : Screen("about_developer")
}
