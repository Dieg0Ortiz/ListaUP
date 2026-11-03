package com.example.myapplication.presentation.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.AppIcons

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun AppBottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    isProfessor: Boolean
) {
    val items = if (isProfessor) {
        listOf(
            BottomNavItem("Inicio", AppIcons.Home, "professor_dashboard"),
            BottomNavItem("Asistencia", AppIcons.List, "attendance"),
            BottomNavItem("Permisos", AppIcons.Document, "permissions"),
            BottomNavItem("Reportes", AppIcons.Chart, "reports"),
            BottomNavItem("Perfil", AppIcons.User, "profile")
        )
    } else {
        listOf(
            BottomNavItem("Inicio", AppIcons.Home, "student_dashboard"),
            BottomNavItem("Materias", AppIcons.List, "subjects"),
            BottomNavItem("Horario", AppIcons.Calendar, "schedule"),
            BottomNavItem("Permisos", AppIcons.Document, "permissions"),
            BottomNavItem("Perfil", AppIcons.User, "profile")
        )
    }

    // Sin altura fija: NavigationBar ya respeta la barra de gestos del sistema
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = null) },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryColor,
                    selectedTextColor = PrimaryColor,
                    indicatorColor = SoftPurple,
                    unselectedIconColor = MutedColor,
                    unselectedTextColor = MutedColor
                )
            )
        }
    }
}
