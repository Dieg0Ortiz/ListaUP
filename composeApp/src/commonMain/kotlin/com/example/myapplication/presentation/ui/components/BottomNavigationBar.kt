package com.example.myapplication.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor

data class BottomNavItem(
    val label: String,
    val glyph: String,
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
            BottomNavItem("Inicio", "home", "professor_dashboard"),
            BottomNavItem("Asistencia", "list", "attendance"),
            BottomNavItem("Permisos", "doc", "permissions"),
            BottomNavItem("Reportes", "chart", "reports"),
            BottomNavItem("Perfil", "user", "profile")
        )
    } else {
        listOf(
            BottomNavItem("Inicio", "home", "student_dashboard"),
            BottomNavItem("Materias", "list", "subjects"),
            BottomNavItem("Horario", "cal", "schedule"),
            BottomNavItem("Permisos", "doc", "permissions"),
            BottomNavItem("Perfil", "user", "profile")
        )
    }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.height(72.dp)
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val tint = if (selected) PrimaryColor else MutedColor
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { NavGlyph(item.glyph, tint) },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = tint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun NavGlyph(kind: String, color: Color) {
    Canvas(Modifier.size(22.dp)) {
        val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round)
        when (kind) {
            "home" -> {
                val path = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.48f)
                    lineTo(size.width * 0.5f, size.height * 0.18f)
                    lineTo(size.width * 0.82f, size.height * 0.48f)
                    lineTo(size.width * 0.82f, size.height * 0.84f)
                    lineTo(size.width * 0.18f, size.height * 0.84f)
                    close()
                }
                drawPath(path, color, style = stroke)
            }
            "list" -> {
                val ys = listOf(0.28f, 0.5f, 0.72f)
                ys.forEach { y ->
                    drawLine(color, Offset(size.width * 0.22f, size.height * y), Offset(size.width * 0.78f, size.height * y), strokeWidth = 1.7.dp.toPx(), cap = StrokeCap.Round)
                }
            }
            "doc" -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width * 0.22f, size.height * 0.14f),
                    size = Size(size.width * 0.56f, size.height * 0.72f),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                    style = stroke
                )
            }
            "chart" -> {
                val bars = listOf(0.22f to 0.55f, 0.42f to 0.35f, 0.62f to 0.62f)
                bars.forEach { (x, top) ->
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(size.width * x, size.height * top),
                        size = Size(size.width * 0.14f, size.height * (0.84f - top)),
                        cornerRadius = CornerRadius(2.dp.toPx())
                    )
                }
            }
            "cal" -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width * 0.16f, size.height * 0.22f),
                    size = Size(size.width * 0.68f, size.height * 0.62f),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                    style = stroke
                )
                drawLine(color, Offset(size.width * 0.16f, size.height * 0.4f), Offset(size.width * 0.84f, size.height * 0.4f), strokeWidth = 1.6.dp.toPx())
            }
            else -> {
                drawCircle(color, radius = size.minDimension * 0.16f, center = Offset(size.width / 2f, size.height * 0.34f), style = stroke)
                drawArc(
                    color = color,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.18f, size.height * 0.42f),
                    size = Size(size.width * 0.64f, size.height * 0.5f),
                    style = stroke
                )
            }
        }
    }
}
