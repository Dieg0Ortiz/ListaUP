package com.example.myapplication.presentation.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.theme.AmberSoft
import com.example.myapplication.presentation.theme.BackgroundColor
import com.example.myapplication.presentation.theme.GreenSoft
import com.example.myapplication.presentation.theme.GreenText
import com.example.myapplication.presentation.theme.InkColor
import com.example.myapplication.presentation.theme.MutedColor
import com.example.myapplication.presentation.theme.PrimaryColor
import com.example.myapplication.presentation.theme.RoseSoft
import com.example.myapplication.presentation.theme.RoseText
import com.example.myapplication.presentation.theme.SoftPurple
import com.example.myapplication.presentation.ui.design.NoticeTint
import com.example.myapplication.presentation.ui.design.SampleData
import com.example.myapplication.presentation.ui.design.StudentNotice
import com.example.myapplication.presentation.ui.design.TopBar

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    var notices by remember { mutableStateOf(SampleData.studentNotices) }
    var unreadOnly by remember { mutableStateOf(false) }
    val visible = if (unreadOnly) notices.filter { it.unread } else notices
    val sections = listOf("Hoy", "Ayer", "Esta semana")

    Column(Modifier.fillMaxSize().background(Color.White)) {
        TopBar(
            title = "Notificaciones",
            onBack = onBack,
            action = "Marcar todo",
            onAction = { notices = notices.map { it.copy(unread = false) } }
        )
        Row(Modifier.padding(horizontal = 20.dp)) {
            TabLabel("Todas", !unreadOnly) { unreadOnly = false }
            Spacer(Modifier.width(18.dp))
            TabLabel("No leídas", unreadOnly) { unreadOnly = true }
        }
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            sections.forEach { section ->
                val rows = visible.filter { it.section == section }
                if (rows.isNotEmpty()) {
                    item {
                        Text(
                            section,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            fontWeight = FontWeight.Bold,
                            color = InkColor
                        )
                    }
                    items(rows, key = { it.id }) { notice ->
                        NoticeRow(notice) {
                            notices = notices.map { if (it.id == notice.id) it.copy(unread = false) else it }
                        }
                    }
                }
            }
            if (visible.isEmpty()) {
                item {
                    Text(
                        "No hay notificaciones sin leer.",
                        modifier = Modifier.padding(20.dp),
                        color = MutedColor
                    )
                }
            }
        }
    }
}

@Composable
private fun TabLabel(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(Modifier.clickable(onClick = onClick).padding(vertical = 8.dp)) {
        Text(text, color = if (selected) PrimaryColor else MutedColor, fontWeight = FontWeight.Bold)
        if (selected) {
            Box(Modifier.padding(top = 6.dp).size(width = 36.dp, height = 3.dp).clip(RoundedCornerShape(2.dp)).background(PrimaryColor))
        }
    }
}

@Composable
private fun NoticeRow(notice: StudentNotice, onClick: () -> Unit) {
    val bg = when (notice.tint) {
        NoticeTint.Purple -> SoftPurple
        NoticeTint.Green -> GreenSoft
        NoticeTint.Amber -> AmberSoft
        NoticeTint.Rose -> RoseSoft
    }
    val fg = when (notice.tint) {
        NoticeTint.Purple -> PrimaryColor
        NoticeTint.Green -> GreenText
        NoticeTint.Amber -> Color(0xFFB45309)
        NoticeTint.Rose -> RoseText
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(bg), contentAlignment = Alignment.Center) {
            Text(notice.icon, color = fg, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(notice.title, fontWeight = FontWeight.Bold, color = InkColor, modifier = Modifier.weight(1f))
                if (notice.unread) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(PrimaryColor))
                }
            }
            Text(notice.body, color = MutedColor, fontSize = 13.sp)
        }
        Text(notice.time, color = MutedColor, fontSize = 12.sp)
    }
}
