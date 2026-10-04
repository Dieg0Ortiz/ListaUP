package com.example.myapplication.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myapplication.core.navigation.Screen
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.presentation.theme.AppTheme
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.ui.alerts.AlertsScreen
import com.example.myapplication.presentation.ui.attendance.AttendanceScreen
import com.example.myapplication.presentation.ui.components.AppBottomNavigationBar
import com.example.myapplication.presentation.ui.dashboard.professor.ProfessorClassesScreen
import com.example.myapplication.presentation.ui.dashboard.professor.ProfessorDashboardScreen
import com.example.myapplication.presentation.ui.dashboard.student.StudentDashboardScreen
import com.example.myapplication.presentation.ui.documents.DocumentPreviewScreen
import com.example.myapplication.presentation.ui.login.LoginScreen
import com.example.myapplication.presentation.ui.notifications.NotificationsScreen
import com.example.myapplication.presentation.ui.permissions.PermissionDetailScreen
import com.example.myapplication.presentation.ui.permissions.PermissionsScreen
import com.example.myapplication.presentation.ui.profile.ProfileScreen
import com.example.myapplication.presentation.ui.reports.ReportsScreen
import com.example.myapplication.presentation.ui.schedule.ScheduleScreen
import com.example.myapplication.presentation.ui.settings.AttendanceSettingsScreen
import com.example.myapplication.presentation.ui.subjects.SubjectDetailScreen
import com.example.myapplication.presentation.ui.subjects.SubjectsScreen
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
        val isProfessor = loginState.user?.role == UserRole.PROFESSOR
        val showBottomBar = shouldShowBottomBar(currentRoute)

        Scaffold(
            containerColor = BackgroundColor,
            bottomBar = {
                if (showBottomBar) {
                    AppBottomNavigationBar(
                        currentRoute = tabFor(currentRoute, isProfessor),
                        onNavigate = { route -> navController.openTab(route) },
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
                            val destination = if (loginViewModel.uiState.value.user?.role == UserRole.PROFESSOR) {
                                Screen.ProfessorDashboard
                            } else {
                                Screen.StudentDashboard
                            }
                            navController.navigate(destination) {
                                popUpTo(Screen.Login) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.ProfessorDashboard> {
                    ProfessorDashboardScreen(
                        onTakeAttendance = { grupoId, materia ->
                            navController.navigate(Screen.Attendance(grupoId, materia))
                        },
                        onOpenSchedule = { navController.navigate(Screen.Schedule) },
                        onOpenAlerts = { navController.navigate(Screen.Alerts) },
                        onOpenNotifications = { navController.navigate(Screen.Alerts) }
                    )
                }

                composable<Screen.StudentDashboard> {
                    StudentDashboardScreen(
                        onOpenNotifications = { navController.navigate(Screen.Notifications) },
                        onRequestPermission = { navController.openTab("permissions") },
                        onOpenSubjects = { navController.openTab("subjects") },
                        onOpenSchedule = { navController.openTab("schedule") },
                        onOpenSubject = { item ->
                            navController.navigate(
                                Screen.SubjectDetail(item.materia, item.docente, item.percent, item.detail)
                            )
                        },
                        onOpenPermission = { name, motivo, canReview ->
                            navController.navigate(
                                Screen.PermissionDetail(
                                    studentName = name,
                                    motivo = motivo,
                                    canReview = canReview,
                                    status = if (motivo.contains("Familiar")) "Aprobado" else "En Directorio"
                                )
                            )
                        }
                    )
                }

                composable<Screen.Attendance> { entry ->
                    val attendance = entry.toRoute<Screen.Attendance>()
                    AttendanceScreen(
                        grupoId = attendance.grupoId,
                        materia = attendance.materia,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<Screen.Alerts> {
                    AlertsScreen(
                        onBack = { navController.popBackStack() },
                        onReview = { request ->
                            navController.navigate(
                                Screen.PermissionDetail(
                                    studentName = request.name,
                                    studentId = request.studentId,
                                    career = request.career,
                                    motivo = request.motivo,
                                    start = request.start,
                                    end = request.end,
                                    days = request.days,
                                    canReview = true
                                )
                            )
                        }
                    )
                }

                composable<Screen.Permissions> {
                    PermissionsScreen(
                        isProfessor = isProfessor,
                        onReview = { request ->
                            navController.navigate(
                                Screen.PermissionDetail(
                                    studentName = request.name,
                                    studentId = request.studentId,
                                    career = request.career,
                                    motivo = request.motivo,
                                    start = request.start,
                                    end = request.end,
                                    days = request.days
                                )
                            )
                        }
                    )
                }

                composable<Screen.Reports> {
                    ReportsScreen()
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
                        onOpenSubject = { card ->
                            navController.navigate(
                                Screen.SubjectDetail(card.materia, card.docente, card.percent, card.schedule)
                            )
                        }
                    )
                }

                composable<Screen.SubjectDetail> { entry ->
                    val detail = entry.toRoute<Screen.SubjectDetail>()
                    SubjectDetailScreen(
                        materia = detail.materia,
                        docente = detail.docente,
                        percent = detail.percent,
                        aula = detail.aula,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<Screen.Schedule> {
                    ScheduleScreen(isProfessor = isProfessor)
                }

                composable<Screen.Notifications> {
                    NotificationsScreen(onBack = { navController.popBackStack() })
                }

                composable<Screen.ProfessorClasses> {
                    ProfessorClassesScreen(
                        onTakeAttendance = { grupoId, materia ->
                            navController.navigate(Screen.Attendance(grupoId, materia))
                        }
                    )
                }

                composable<Screen.PermissionDetail> { entry ->
                    val detail = entry.toRoute<Screen.PermissionDetail>()
                    PermissionDetailScreen(
                        studentName = detail.studentName,
                        studentId = detail.studentId,
                        career = detail.career,
                        motivo = detail.motivo,
                        start = detail.start,
                        end = detail.end,
                        days = detail.days,
                        status = detail.status,
                        canReview = detail.canReview,
                        onBack = { navController.popBackStack() },
                        onOpenDocument = { navController.navigate(Screen.DocumentPreview) }
                    )
                }

                composable<Screen.DocumentPreview> {
                    DocumentPreviewScreen(onBack = { navController.popBackStack() })
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
    if (route.contains("Login")) return false
    if (route.contains("Notifications")) return false
    if (route.contains("PermissionDetail")) return false
    if (route.contains("DocumentPreview")) return false
    if (route.contains("AttendanceSettings")) return false
    if (route.contains("SubjectDetail")) return false
    if (route.contains("Attendance") && !route.contains("Settings")) return false
    return true
}

private fun tabFor(route: String?, isProfessor: Boolean): String {
    val value = route ?: return ""
    return when {
        value.contains("ProfessorDashboard") -> "professor_dashboard"
        value.contains("StudentDashboard") -> "student_dashboard"
        value.contains("ProfessorClasses") -> "attendance"
        value.contains("Subjects") -> "subjects"
        value.contains("Schedule") -> "schedule"
        value.contains("Permissions") || value.contains("Permission") -> "permissions"
        value.contains("Reports") -> "reports"
        value.contains("Profile") || value.contains("AttendanceSettings") -> "profile"
        else -> if (isProfessor) "" else ""
    }
}

private fun androidx.navigation.NavHostController.openTab(route: String) {
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
