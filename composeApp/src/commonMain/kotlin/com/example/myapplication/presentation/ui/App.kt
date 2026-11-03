package com.example.myapplication.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myapplication.core.navigation.Screen
import com.example.myapplication.data.local.SessionManager
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.presentation.theme.AppTheme
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.ui.alerts.AlertsScreen
import com.example.myapplication.presentation.ui.attendance.AttendanceScreen
import com.example.myapplication.presentation.ui.components.AppBottomNavigationBar
import com.example.myapplication.presentation.ui.dashboard.professor.ProfessorClassesScreen
import com.example.myapplication.presentation.ui.dashboard.professor.ProfessorDashboardScreen
import com.example.myapplication.presentation.ui.dashboard.student.StudentDashboardScreen
import com.example.myapplication.presentation.ui.login.LoginScreen
import com.example.myapplication.presentation.ui.notifications.NotificationsScreen
import com.example.myapplication.presentation.ui.permissions.PermissionDetailScreen
import com.example.myapplication.presentation.ui.permissions.PermissionRequestScreen
import com.example.myapplication.presentation.ui.permissions.PermissionsScreen
import com.example.myapplication.presentation.ui.profile.ProfileScreen
import com.example.myapplication.presentation.ui.reports.ReportsScreen
import com.example.myapplication.presentation.ui.schedule.ScheduleScreen
import com.example.myapplication.presentation.ui.settings.AttendanceSettingsScreen
import com.example.myapplication.presentation.ui.subjects.SubjectDetailScreen
import com.example.myapplication.presentation.ui.subjects.SubjectsScreen
import com.example.myapplication.presentation.viewmodel.LoginViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()
        val loginViewModel: LoginViewModel = koinViewModel()
        val loginState by loginViewModel.uiState.collectAsState()
        val sessionManager: SessionManager = koinInject()

        // Con una sesión guardada se abre directo el tablero, sin pasar por el login
        val startDestination: Screen = remember {
            when (sessionManager.getUser()?.role) {
                UserRole.PROFESSOR -> Screen.ProfessorDashboard
                UserRole.STUDENT -> Screen.StudentDashboard
                null -> Screen.Login
            }
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val isProfessor = loginState.user?.role == UserRole.PROFESSOR
        val showBottomBar = shouldShowBottomBar(currentRoute)

        Scaffold(
            containerColor = BackgroundColor,
            bottomBar = {
                if (showBottomBar) {
                    AppBottomNavigationBar(
                        currentRoute = tabFor(currentRoute),
                        onNavigate = { route -> navController.openTab(route) },
                        isProfessor = isProfessor
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable<Screen.Login> {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            val destination = if (loginViewModel.uiState.value.user?.role == UserRole.PROFESSOR) {
                                Screen.ProfessorDashboard
                            } else {
                                Screen.StudentDashboard
                            }
                            navController.navigate(destination) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.ProfessorDashboard> {
                    ProfessorDashboardScreen(
                        onTakeAttendance = { grupoId, materia ->
                            navController.navigate(Screen.Attendance(grupoId, materia))
                        },
                        onOpenSchedule = { navController.openTab("schedule") },
                        onOpenAlerts = { navController.navigate(Screen.Alerts) },
                        onOpenNotifications = { navController.navigate(Screen.Notifications) },
                        onOpenPermissions = { navController.openTab("permissions") },
                        onOpenReports = { navController.openTab("reports") }
                    )
                }

                composable<Screen.StudentDashboard> {
                    StudentDashboardScreen(
                        onOpenNotifications = { navController.navigate(Screen.Notifications) },
                        onRequestPermission = { navController.navigate(Screen.PermissionRequest) },
                        onOpenPermissions = { navController.openTab("permissions") },
                        onOpenSubjects = { navController.openTab("subjects") },
                        onOpenSchedule = { navController.openTab("schedule") },
                        onOpenPermission = { id -> navController.navigate(Screen.PermissionDetail(id)) }
                    )
                }

                composable<Screen.Attendance> { entry ->
                    val attendance = entry.toRoute<Screen.Attendance>()
                    AttendanceScreen(
                        grupoId = attendance.grupoId,
                        materia = attendance.materia,
                        onBack = { navController.popBackStack() },
                        onFinished = { navController.openTab("professor_dashboard") }
                    )
                }

                composable<Screen.Alerts> {
                    AlertsScreen(
                        onBack = { navController.popBackStack() },
                        onReview = { id -> navController.navigate(Screen.PermissionDetail(id)) },
                        onOpenSettings = { navController.navigate(Screen.AttendanceSettings) }
                    )
                }

                composable<Screen.Permissions> {
                    PermissionsScreen(
                        onOpen = { id -> navController.navigate(Screen.PermissionDetail(id)) },
                        onNewRequest = { navController.navigate(Screen.PermissionRequest) }
                    )
                }

                composable<Screen.PermissionRequest> {
                    PermissionRequestScreen(
                        onBack = { navController.popBackStack() },
                        onCreated = { id ->
                            navController.navigate(Screen.PermissionDetail(id)) {
                                popUpTo<Screen.PermissionRequest> { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.PermissionDetail> { entry ->
                    val detail = entry.toRoute<Screen.PermissionDetail>()
                    PermissionDetailScreen(
                        permissionId = detail.permissionId,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<Screen.Reports> {
                    ReportsScreen(onOpenSettings = { navController.navigate(Screen.AttendanceSettings) })
                }

                composable<Screen.Profile> {
                    ProfileScreen(
                        onLogout = {
                            loginViewModel.resetAfterLogout()
                            navController.navigate(Screen.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onOpenSettings = { navController.navigate(Screen.AttendanceSettings) }
                    )
                }

                composable<Screen.Subjects> {
                    SubjectsScreen(
                        onOpenNotifications = { navController.navigate(Screen.Notifications) },
                        onOpenSubject = { group ->
                            navController.navigate(
                                Screen.SubjectDetail(
                                    grupoId = group.grupoId,
                                    materia = group.materia,
                                    docente = group.docenteNombre,
                                    aula = group.aula,
                                    horario = group.horario,
                                    grupo = group.nombre
                                )
                            )
                        }
                    )
                }

                composable<Screen.SubjectDetail> { entry ->
                    val detail = entry.toRoute<Screen.SubjectDetail>()
                    SubjectDetailScreen(
                        detail = detail,
                        onBack = { navController.popBackStack() },
                        onRequestPermission = { navController.navigate(Screen.PermissionRequest) }
                    )
                }

                composable<Screen.Schedule> {
                    ScheduleScreen(isProfessor = isProfessor)
                }

                composable<Screen.Notifications> {
                    NotificationsScreen(
                        onBack = { navController.popBackStack() },
                        onOpenPermission = { id -> navController.navigate(Screen.PermissionDetail(id)) }
                    )
                }

                composable<Screen.ProfessorClasses> {
                    ProfessorClassesScreen(
                        onTakeAttendance = { grupoId, materia ->
                            navController.navigate(Screen.Attendance(grupoId, materia))
                        }
                    )
                }

                composable<Screen.AttendanceSettings> {
                    AttendanceSettingsScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

private fun shouldShowBottomBar(route: String?): Boolean {
    if (route == null) return false
    val hidden = listOf(
        "Login", "Notifications", "PermissionDetail", "PermissionRequest",
        "AttendanceSettings", "SubjectDetail", "Alerts"
    )
    if (hidden.any { route.contains(it) }) return false
    if (route.contains("Attendance")) return false
    return true
}

private fun tabFor(route: String?): String {
    val value = route ?: return ""
    return when {
        value.contains("ProfessorDashboard") -> "professor_dashboard"
        value.contains("StudentDashboard") -> "student_dashboard"
        value.contains("ProfessorClasses") -> "attendance"
        value.contains("Subjects") -> "subjects"
        value.contains("Schedule") -> "schedule"
        value.contains("Permission") -> "permissions"
        value.contains("Reports") -> "reports"
        value.contains("Profile") || value.contains("AttendanceSettings") -> "profile"
        else -> ""
    }
}

private fun NavHostController.openTab(route: String) {
    val destination = when (route) {
        "professor_dashboard" -> Screen.ProfessorDashboard
        "student_dashboard" -> Screen.StudentDashboard
        "attendance" -> Screen.ProfessorClasses
        "permissions" -> Screen.Permissions
        "reports" -> Screen.Reports
        "profile" -> Screen.Profile
        "subjects" -> Screen.Subjects
        "schedule" -> Screen.Schedule
        else -> return
    }
    navigate(destination) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}
