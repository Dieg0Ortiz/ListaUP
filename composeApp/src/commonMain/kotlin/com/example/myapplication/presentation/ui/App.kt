package com.example.myapplication.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myapplication.core.navigation.Screen
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.presentation.theme.AppTheme
import com.example.myapplication.presentation.ui.alerts.AlertsScreen
import com.example.myapplication.presentation.ui.attendance.AttendanceScreen
import com.example.myapplication.presentation.ui.components.AppBottomNavigationBar
import com.example.myapplication.presentation.ui.dashboard.professor.ProfessorDashboardScreen
import com.example.myapplication.presentation.ui.dashboard.student.StudentDashboardScreen
import com.example.myapplication.presentation.ui.login.LoginScreen
import com.example.myapplication.presentation.ui.profile.ProfileScreen
import com.example.myapplication.presentation.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()
        val loginViewModel: LoginViewModel = koinViewModel()
        val loginState by loginViewModel.uiState.collectAsState()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showBottomBar = currentRoute != null &&
                !currentRoute.contains("Login") &&
                !currentRoute.contains("Attendance")

        val isProfessor = loginState.user?.role == UserRole.PROFESSOR

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    AppBottomNavigationBar(
                        currentRoute = when {
                            currentRoute?.contains("ProfessorDashboard") == true -> "professor_dashboard"
                            currentRoute?.contains("StudentDashboard") == true -> "student_dashboard"
                            currentRoute?.contains("Alerts") == true -> "attendance"
                            currentRoute?.contains("Permissions") == true -> "permissions"
                            currentRoute?.contains("Reports") == true -> "reports"
                            currentRoute?.contains("Profile") == true -> "profile"
                            else -> ""
                        },
                        onNavigate = { route ->
                            when (route) {
                                "professor_dashboard" -> navController.navigate(Screen.ProfessorDashboard) {
                                    popUpTo(0) { inclusive = true }
                                }
                                "student_dashboard" -> navController.navigate(Screen.StudentDashboard) {
                                    popUpTo(0) { inclusive = true }
                                }
                                "attendance" -> navController.navigate(Screen.Alerts)
                                "permissions" -> navController.navigate(Screen.Permissions)
                                "reports" -> navController.navigate(Screen.Reports)
                                "profile" -> navController.navigate(Screen.Profile)
                            }
                        },
                        isProfessor = isProfessor
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Login,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable<Screen.Login> {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            val user = loginState.user
                            if (user?.role == UserRole.PROFESSOR) {
                                navController.navigate(Screen.ProfessorDashboard) {
                                    popUpTo(Screen.Login) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.StudentDashboard) {
                                    popUpTo(Screen.Login) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable<Screen.ProfessorDashboard> {
                    ProfessorDashboardScreen(
                        onTakeAttendance = { grupoId, materia ->
                            navController.navigate(Screen.Attendance(grupoId, materia))
                        }
                    )
                }

                composable<Screen.StudentDashboard> {
                    StudentDashboardScreen()
                }

                composable<Screen.Attendance> { backStackEntry ->
                    val attendance = backStackEntry.toRoute<Screen.Attendance>()
                    AttendanceScreen(
                        grupoId = attendance.grupoId,
                        materia = attendance.materia,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<Screen.Alerts> {
                    AlertsScreen()
                }

                composable<Screen.Permissions> {
                    PermissionsPlaceholder()
                }

                composable<Screen.Reports> {
                    ReportsPlaceholder()
                }

                composable<Screen.Profile> {
                    ProfileScreen(
                        onLogout = {
                            navController.navigate(Screen.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionsPlaceholder() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .then(androidx.compose.ui.Modifier.fillMaxSize()),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(text = "📝", fontSize = androidx.compose.ui.unit.TextUnit(48f, androidx.compose.ui.unit.TextUnitType.Sp))
            androidx.compose.material3.Text(
                text = "Permisos",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            androidx.compose.material3.Text(
                text = "Próximamente",
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }
    }
}

@Composable
fun ReportsPlaceholder() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .then(androidx.compose.ui.Modifier.fillMaxSize()),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(text = "📊", fontSize = androidx.compose.ui.unit.TextUnit(48f, androidx.compose.ui.unit.TextUnitType.Sp))
            androidx.compose.material3.Text(
                text = "Reportes",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            androidx.compose.material3.Text(
                text = "Próximamente",
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }
    }
}
