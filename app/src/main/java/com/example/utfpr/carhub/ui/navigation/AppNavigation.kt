package com.example.utfpr.carhub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.utfpr.carhub.ui.screens.carform.AddEditCarScreen
import com.example.utfpr.carhub.ui.screens.home.HomeScreen
import com.example.utfpr.carhub.ui.screens.login.LoginScreen
import com.example.utfpr.carhub.ui.viewmodel.AddEditCarViewModel
import com.example.utfpr.carhub.ui.viewmodel.HomeViewModel
import com.example.utfpr.carhub.ui.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val cars by homeViewModel.cars.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val startDestination = if (isLoggedIn) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    homeViewModel.loadCars()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val phone = currentUser?.phoneNumber ?: ""

            HomeScreen(
                cars = cars,
                isLoading = isLoading,
                onCarClick = { car -> navController.navigate("add_edit/${car.id}") },
                onAddClick = { navController.navigate("add_edit/new") },
                onRefresh = { homeViewModel.loadCars() },
                currentUserPhone = phone,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    loginViewModel.reset()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("add_edit/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: "new"
            val existingCar = if (carId == "new") null else cars.find { it.id == carId }
            val addEditViewModel: AddEditCarViewModel = viewModel()

            LaunchedEffect(carId) {
                addEditViewModel.initFromCar(existingCar)
            }

            AddEditCarScreen(
                existingCar = existingCar,
                viewModel = addEditViewModel,
                onSave = {
                    homeViewModel.loadCars()
                    navController.popBackStack()
                },
                onDelete = {
                    homeViewModel.loadCars()
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
