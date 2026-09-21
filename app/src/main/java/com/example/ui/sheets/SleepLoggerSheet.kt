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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SleepEntry
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextBlack

@Composable
fun SleepLoggerSheet(
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  var sleepHours by remember { mutableFloatStateOf(7.5f) }
  var selectedQuality by remember { mutableStateOf("Good & Restful") }
  val qualities = listOf("Restless", "Fair", "Good & Restful", "Deep & Rejuvenating")

  val sleepHistory = remember {
    mutableStateListOf(
      SleepEntry(hours = 7.0f, quality = "Good & Restful", dateLabel = "Yesterday", tips = "Maintain consistent bedtime."),
      SleepEntry(hours = 6.2f, quality = "Restless", dateLabel = "2 days ago", tips = "Limit caffeine in afternoon.")
    )
  }

  var currentAdvice by remember {
    mutableStateOf("7.5 hours is an optimal duration for cellular repair and mental clarity. Keep up the consistent routine.")
  }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            PureWhite,
            Color(0xFFF7F7F7),
            Color(0xFFECECEC)
          )
        )
      )
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onClose,
        modifier = Modifier.testTag("sleep_logger_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = TextBlack
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "Sleep Logger",
          fontFamily = PlayfairFontFamily,
          fontSize = 19.sp,
          fontWeight = FontWeight.Medium,
          color = TextBlack
        )
        Text(
          text = "Restorative health telemetry",
          fontSize = 12.sp,
          color = Color(0xFF757575)
        )
      }
    }

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Hours Slider
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(Color.White)
          .padding(20.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Duration of Sleep:",
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp,
              color = TextBlack
            )
            Text(
              text = String.format("%.1f hrs", sleepHours),
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = TextBlack
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Slider(
            value = sleepHours,
            onValueChange = {
              sleepHours = it
              currentAdvice = when {
                sleepHours < 6.0f ->
                  "Less than 6 hours of sleep can impair cognitive focus and immune defense. Try going to bed 30 minutes earlier tonight."
                sleepHours in 6.0f..8.5f ->
                  "This duration is within healthy restorative parameters for adults. Well done."
                else ->
                  "Over 9 hours may indicate fatigue recovery or oversleeping. Ensure your waking environment receives morning sunlight."
              }
            },
            valueRange = 3.0f..12.0f,
            steps = 17,
            colors = SliderDefaults.colors(
              thumbColor = TextBlack,
              activeTrackColor = TextBlack
            ),
            modifier = Modifier.testTag("sleep_hours_slider")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("3h", fontSize = 12.sp, color = Color.Gray)
            Text("7.5h (Target)", fontSize = 12.sp, color = Color.Gray)
            Text("12h", fontSize = 12.sp, color = Color.Gray)
          }
        }
      }

      // Quality Selector
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(Color.White)
          .padding(20.dp)
      ) {
        Column {
          Text(
            text = "Sleep Quality:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = TextBlack
          )
          Spacer(modifier = Modifier.height(12.dp))

          qualities.forEach { quality ->
            val isSelected = selectedQuality == quality
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) TextBlack else Color(0xFFF3F3F3))
                .clickable { selectedQuality = quality }
                .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
              Text(
                text = quality,
                color = if (isSelected) PureWhite else TextBlack,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 14.sp
              )
            }
          }
        }
      }

      // Save Log Button
      Button(
        onClick = {
          sleepHistory.add(
            0,
            SleepEntry(
              hours = sleepHours,
              quality = selectedQuality,
              dateLabel = "Today",
              tips = currentAdvice
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = TextBlack),
        shape = CircleShape,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("save_sleep_button")
      ) {
        Text("Record Sleep Log", color = PureWhite, fontSize = 15.sp)
      }

      // Plus Sleep Advice
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White)
          .padding(18.dp)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Text("●—● ", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "Plus Restorative Tip",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF616161)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = currentAdvice,
              fontSize = 14.sp,
              color = TextBlack,
              lineHeight = 20.sp
            )
          }
        }
      }
    }
  }
}
