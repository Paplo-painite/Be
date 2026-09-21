package com.example.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextBlack

enum class AppDestination {
  HOME,
  CHAT,
  SYMPTOM_CHECKER,
  MOOD_TRACKER,
  SLEEP_LOGGER,
  MEDICATION_REMINDER,
  EMERGENCY
}

@Composable
fun NavigationDrawerSheet(
  currentDestination: AppDestination,
  onNavigate: (AppDestination) -> Unit,
  ttsEnabled: Boolean,
  onToggleTts: (Boolean) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            PureWhite,
            Color(0xFFF7F7F7),
            Color(0xFFEDEDED)
          )
        )
      )
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Plus",
          fontFamily = PlayfairFontFamily,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = TextBlack
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "0.2",
          fontSize = 12.sp,
          color = Color(0xFFAAAAAA),
          fontWeight = FontWeight.Medium
        )
      }

      IconButton(
        onClick = onClose,
        modifier = Modifier.testTag("nav_close_button")
      ) {
        Icon(Icons.Default.Close, contentDescription = "Close Menu", tint = TextBlack)
      }
    }

    Text(
      text = "Personal Healthcare Companion",
      fontSize = 12.5.sp,
      color = Color(0xFF757575),
      fontFamily = FontFamily.SansSerif
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Navigation Items
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      NavItem(
        title = "Healthcare Chat",
        subtitle = "Talk with Plus (Baymax AI)",
        icon = Icons.AutoMirrored.Filled.Chat,
        onClick = { onNavigate(AppDestination.CHAT) }
      )
      NavItem(
        title = "Symptom Checker",
        subtitle = "Guided pain scale & triage",
        icon = Icons.Default.HealthAndSafety,
        onClick = { onNavigate(AppDestination.SYMPTOM_CHECKER) }
      )
      NavItem(
        title = "Mood Tracker",
        subtitle = "Daily check-in & emotional trends",
        icon = Icons.Default.Mood,
        onClick = { onNavigate(AppDestination.MOOD_TRACKER) }
      )
      NavItem(
        title = "Sleep Logger",
        subtitle = "Record hours & recovery advice",
        icon = Icons.Default.Bedtime,
        onClick = { onNavigate(AppDestination.SLEEP_LOGGER) }
      )
      NavItem(
        title = "Medication Reminders",
        subtitle = "Daily prescription alerts",
        icon = Icons.Default.MedicalServices,
        onClick = { onNavigate(AppDestination.MEDICATION_REMINDER) }
      )
      NavItem(
        title = "Emergency Protocol",
        subtitle = "Immediate medical assistance & dialer",
        icon = Icons.Default.Warning,
        isAlert = true,
        onClick = { onNavigate(AppDestination.EMERGENCY) }
      )
    }

    Spacer(modifier = Modifier.weight(1f))

    // Baymax Voice Toggle (Text To Speech)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.VolumeUp, contentDescription = null, tint = TextBlack)
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text("Baymax Voice (TTS)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextBlack)
            Text("Gentle voice readout", fontSize = 11.5.sp, color = Color.Gray)
          }
        }
        Switch(
          checked = ttsEnabled,
          onCheckedChange = onToggleTts,
          colors = SwitchDefaults.colors(checkedThumbColor = PureWhite, checkedTrackColor = TextBlack)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))
  }
}

@Composable
fun NavItem(
  title: String,
  subtitle: String,
  icon: ImageVector,
  isAlert: Boolean = false,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(if (isAlert) Color(0xFFFFEEEE) else Color.White)
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isAlert) Color(0xFFD32F2F) else TextBlack,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(14.dp))
      Column {
        Text(
          text = title,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.5.sp,
          color = if (isAlert) Color(0xFFD32F2F) else TextBlack
        )
        Text(
          text = subtitle,
          fontSize = 12.sp,
          color = if (isAlert) Color(0xFFC62828) else Color.Gray
        )
      }
    }
  }
}
