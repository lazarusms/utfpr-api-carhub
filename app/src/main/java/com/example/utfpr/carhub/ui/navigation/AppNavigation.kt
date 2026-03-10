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
import com.example.utfpr.carhub.ui.viewmodel.AddEditCarViewModel
import com.example.utfpr.carhub.ui.viewmodel.HomeViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    val cars by homeViewModel.cars.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                cars = cars,
                isLoading = isLoading,
                onCarClick = { car -> navController.navigate("add_edit/${car.id}") },
                onAddClick = { navController.navigate("add_edit/new") }
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
