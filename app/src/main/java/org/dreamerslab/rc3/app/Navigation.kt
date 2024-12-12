package org.dreamerslab.rc3.app

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.dreamerslab.rc3.ui.auth.forgotpassword.ResetPasswordPage
import org.dreamerslab.rc3.ui.auth.login.LoginScreen
import org.dreamerslab.rc3.ui.auth.signup.SignupScreen
import org.dreamerslab.rc3.ui.cart.CartPage
import org.dreamerslab.rc3.ui.home.HomePage
import org.dreamerslab.rc3.ui.menu.MenuPage
import org.dreamerslab.rc3.ui.search.SearchPage

@Serializable
sealed interface Destination

@Serializable
sealed interface NavigationBarDestination : Destination

@Serializable
data object Login : Destination

@Serializable
data object Signup : Destination

@Serializable
data class ResetPassword(
    val email: String? = null
) : Destination

@Serializable
data object Home : Destination

@Serializable
data object Search : Destination

@Serializable
data class Menu(
    val id: String,
    val title: String,
) : Destination

@Serializable
data object Cart : Destination

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    startDestination: Destination = Login,
    controller: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        navController = controller,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween()
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween()
            )
        },
        popEnterTransition = {
            EnterTransition.None
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween()
            )
        }
    ) {
        authGraph(controller)

        composable<Home> {
            HomePage(
                onSearchClick = {
                    controller.navigate(Search)
                },
                onRestaurantClick = {
                    controller.navigate(Menu(it.id, it.name))
                }
            )
        }

        composable<Search> {
            SearchPage(
                onRestaurantClick = {
                    controller.navigate(Menu(it.id, it.name))
                }
            )
        }

        composable<Menu> {
            MenuPage(
                onNavigateUp = controller::navigateUp,
                onCartClick = { controller.navigate(Cart) },
                title = it.toRoute<Menu>().title
            )
        }

        composable<Cart> {
            CartPage(
                onNavigateUp = controller::navigateUp
            )
        }
    }
}

private fun NavGraphBuilder.authGraph(
    controller: NavHostController
) {
    composable<Login> {
        LoginScreen(
            onSignupClick = {
                controller.navigate(Signup)
            },
            onResetPasswordClick = { email ->
                controller.navigate(ResetPassword(email))
            }
        )
    }

    composable<Signup> {
        SignupScreen(
            onLoginClick = controller::navigateUp
        )
    }

    composable<ResetPassword> {
        ResetPasswordPage(
            onNavigateUp = controller::navigateUp
        )
    }
}
