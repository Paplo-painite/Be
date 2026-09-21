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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.MoodEntry
import com.example.data.MoodRating
import com.example.ui.components.DailyMoodData
import com.example.ui.components.MoodTrendCanvasChart
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextBlack

@Composable
fun MoodTrackerSheet(
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val history = remember {
    mutableStateListOf(
      MoodEntry(rating = MoodRating.OKAY, note = "Tuesday check-in"),
      MoodEntry(rating = MoodRating.GOOD, note = "Wednesday morning"),
      MoodEntry(rating = MoodRating.VERY_LOW, note = "Thursday stressed"),
      MoodEntry(rating = MoodRating.GOOD, note = "Friday evening")
    )
  }

  var selectedMood by remember { mutableStateOf<MoodRating?>(MoodRating.GOOD) }
  var feedbackMessage by remember {
    mutableStateOf("You are doing well. Remember to take small moments to rest and breathe deeply.")
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
        modifier = Modifier.testTag("mood_tracker_back_button")
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
          text = "Daily Mood Check-in",
          fontFamily = PlayfairFontFamily,
          fontSize = 19.sp,
          fontWeight = FontWeight.Medium,
          color = TextBlack
        )
        Text(
          text = "Emotional well-being & trends",
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
      // Check-in Question
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(Color.White)
          .padding(20.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "How are you feeling today?",
            fontFamily = PlayfairFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextBlack
          )
          Spacer(modifier = Modifier.height(16.dp))

          // Emojis row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            MoodRating.values().forEach { rating ->
              val isSelected = selectedMood == rating
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .clip(CircleShape)
                  .clickable {
                    selectedMood = rating
                    history.add(MoodEntry(rating = rating, note = "Today"))
                    feedbackMessage = when (rating) {
                      MoodRating.VERY_LOW, MoodRating.LOW ->
                        "I am here for you. It is okay to not feel okay. We will get through this together."
                      MoodRating.OKAY ->
                        "A steady day. Be gentle with yourself and stay hydrated."
                      MoodRating.GOOD, MoodRating.EXCELLENT ->
                        "It brings me joy that you are feeling good. Keep nourishing your mind and body."
                    }
                  }
                  .padding(8.dp)
                  .testTag("mood_${rating.name.lowercase()}")
              ) {
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFFE0E0E0) else Color(0xFFF3F3F3)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = rating.emoji, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = rating.label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = TextBlack
                )
              }
            }
          }
        }
      }

      // Plus Encouragement Banner
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
              text = "Plus Healthcare Advice",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF616161)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = feedbackMessage,
              fontSize = 14.sp,
              color = TextBlack,
              lineHeight = 20.sp
            )
          }
        }
      }

      // Interactive Custom Canvas Mood Trend Chart
      val weeklyMoodData = remember(selectedMood) {
        listOf(
          DailyMoodData(dayLabel = "Mon", fullDate = "Sep 12", score = 3f, rating = MoodRating.OKAY, note = "Steady pace, routine work"),
          DailyMoodData(dayLabel = "Tue", fullDate = "Sep 13", score = 4f, rating = MoodRating.GOOD, note = "Pleasant evening walk"),
          DailyMoodData(dayLabel = "Wed", fullDate = "Sep 14", score = 2f, rating = MoodRating.LOW, note = "Temporary fatigue & stress"),
          DailyMoodData(dayLabel = "Thu", fullDate = "Sep 15", score = 4f, rating = MoodRating.GOOD, note = "Energized morning focus"),
          DailyMoodData(dayLabel = "Fri", fullDate = "Sep 16", score = 3f, rating = MoodRating.OKAY, note = "Calm wind-down"),
          DailyMoodData(dayLabel = "Sat", fullDate = "Sep 17", score = 5f, rating = MoodRating.EXCELLENT, note = "Joyful social connection"),
          DailyMoodData(
            dayLabel = "Today",
            fullDate = "Sep 18",
            score = (selectedMood?.score ?: 4).toFloat(),
            rating = selectedMood ?: MoodRating.GOOD,
            note = when (selectedMood) {
              MoodRating.VERY_LOW, MoodRating.LOW -> "Gently resting with Plus"
              MoodRating.OKAY -> "Quiet, steady breathing"
              MoodRating.GOOD, MoodRating.EXCELLENT -> "Vibrant clarity & balance"
              null -> "Logged with Plus"
            }
          )
        )
      }

      MoodTrendCanvasChart(
        weeklyData = weeklyMoodData,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}
