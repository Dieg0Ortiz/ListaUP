package com.example.myapplication.presentation.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor

data class BottomNavItem(
    val label: String,
    val icon: String,
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
            BottomNavItem("Inicio", "🏠", "professor_dashboard"),
            BottomNavItem("Asistencia", "📋", "attendance"),
            BottomNavItem("Permisos", "📝", "permissions"),
            BottomNavItem("Reportes", "📊", "reports"),
            BottomNavItem("Perfil", "👤", "profile")
        )
    } else {
        listOf(
            BottomNavItem("Inicio", "🏠", "student_dashboard"),
            BottomNavItem("Materias", "📚", "subjects"),
            BottomNavItem("Horario", "📅", "schedule"),
            BottomNavItem("Permisos", "📝", "permissions"),
            BottomNavItem("Perfil", "👤", "profile")
        )
    }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(78.dp)
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Text(text = item.icon, fontSize = 18.sp) },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) PrimaryColor else MutedColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = PrimaryColor.copy(alpha = 0.12f)
                )
            )
        }
    }
}
