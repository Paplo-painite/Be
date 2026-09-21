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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ai.BaymaxAIEngine
import com.example.data.SymptomAssessment
import com.example.data.SymptomStep
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextBlack

@Composable
fun SymptomCheckerSheet(
  initialSymptom: String = "",
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  var currentStep by remember { mutableStateOf(SymptomStep.INITIAL) }
  var symptomText by remember { mutableStateOf(initialSymptom) }
  var painScale by remember { mutableFloatStateOf(5f) }
  var durationText by remember { mutableStateOf("A few hours") }
  var locationText by remember { mutableStateOf("Head / Forehead") }
  var assessmentResult by remember { mutableStateOf<SymptomAssessment?>(null) }

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
        modifier = Modifier.testTag("symptom_checker_back_button")
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
          text = "Symptom Assessment",
          fontFamily = PlayfairFontFamily,
          fontSize = 19.sp,
          fontWeight = FontWeight.Medium,
          color = TextBlack
        )
        Text(
          text = "Baymax Guided Health Triage",
          fontSize = 12.sp,
          color = Color(0xFF757575)
        )
      }
    }

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Step 1: Symptom description
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White)
          .padding(18.dp)
      ) {
        Column {
          Text(
            text = "1. What symptoms are you experiencing?",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = TextBlack
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = symptomText,
            onValueChange = { symptomText = it },
            placeholder = { Text("e.g. throbbing headache, mild fever, sore throat") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("symptom_input")
          )
        }
      }

      // Step 2: Baymax Pain Scale (1 to 10)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White)
          .padding(18.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "2. On a scale of 1 to 10, rate your pain:",
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp,
              color = TextBlack
            )
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (painScale > 6) Color(0xFFFF5252) else TextBlack),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${painScale.toInt()}",
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Slider(
            value = painScale,
            onValueChange = { painScale = it },
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
              thumbColor = TextBlack,
              activeTrackColor = TextBlack
            ),
            modifier = Modifier.testTag("pain_scale_slider")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("1 (Mild)", fontSize = 12.sp, color = Color.Gray)
            Text("5 (Moderate)", fontSize = 12.sp, color = Color.Gray)
            Text("10 (Severe)", fontSize = 12.sp, color = Color.Gray)
          }
        }
      }

      // Step 3: Duration & Location
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White)
          .padding(18.dp)
      ) {
        Column {
          Text(
            text = "3. Duration & Location",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = TextBlack
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = durationText,
            onValueChange = { durationText = it },
            label = { Text("How long has this lasted?") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = locationText,
            onValueChange = { locationText = it },
            label = { Text("Where is it located?") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      // Button: Scan and Assess
      Button(
        onClick = {
          assessmentResult = BaymaxAIEngine.assessSymptoms(
            symptom = if (symptomText.isBlank()) "Headache" else symptomText,
            painScale = painScale.toInt(),
            duration = durationText,
            location = locationText
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = TextBlack),
        shape = CircleShape,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("run_assessment_button")
      ) {
        Text(
          text = "Scan & Assess with Plus",
          color = PureWhite,
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium
        )
      }

      // Assessment Result Display
      assessmentResult?.let { result ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(20.dp)
            .testTag("assessment_result_box")
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF2E7D32)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Baymax Scan Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlack
              )
            }

            Text(
              text = "Possible Indication: ${result.possibleCause}",
              fontSize = 14.sp,
              color = TextBlack,
              fontWeight = FontWeight.Medium
            )

            Text(
              text = "Recommended Care Steps:",
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp,
              color = TextBlack
            )

            result.remedies.forEach { remedy ->
              Row(modifier = Modifier.padding(start = 4.dp)) {
                Text("• ", fontWeight = FontWeight.Bold, color = TextBlack)
                Text(
                  text = remedy,
                  fontSize = 13.5.sp,
                  color = Color(0xFF424242)
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Mandatory Disclaimer Box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFFF9E6))
                .padding(12.dp)
            ) {
              Row(verticalAlignment = Alignment.Top) {
                Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = null,
                  tint = Color(0xFFF57F17),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = result.disclaimer,
                  fontSize = 12.sp,
                  color = Color(0xFF5D4037),
                  lineHeight = 16.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
