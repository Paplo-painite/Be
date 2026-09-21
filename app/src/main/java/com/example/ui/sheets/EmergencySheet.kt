package com.example.ui.sheets

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextBlack

@Composable
fun EmergencySheet(
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  fun dialNumber(number: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
      data = Uri.parse("tel:$number")
    }
    context.startActivity(intent)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFFF5F5))
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onClose,
        modifier = Modifier.testTag("emergency_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = TextBlack
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Emergency Mode",
        fontFamily = PlayfairFontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFD32F2F)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Baymax Reassurance Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(Color.White)
        .padding(20.dp)
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFD32F2F),
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Plus Emergency Alert",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color(0xFFD32F2F)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "This sounds like an emergency. Please call your local emergency services immediately. I am here with you.",
          fontSize = 16.sp,
          fontWeight = FontWeight.Medium,
          lineHeight = 23.sp,
          color = TextBlack
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "If you are experiencing severe chest pressure, sudden numbness or confusion, intense difficulty breathing, or severe trauma, do not wait.",
          fontSize = 13.sp,
          color = Color(0xFF616161),
          lineHeight = 18.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Direct Action Buttons
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
      // 911 (US / Canada)
      Button(
        onClick = { dialNumber("911") },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
        shape = CircleShape,
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("dial_911_button")
      ) {
        Icon(Icons.Default.Call, contentDescription = null, tint = PureWhite)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Call 911 (Emergency Services)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PureWhite)
      }

      // 112 (Europe / International)
      Button(
        onClick = { dialNumber("112") },
        colors = ButtonDefaults.buttonColors(containerColor = TextBlack),
        shape = CircleShape,
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
      ) {
        Icon(Icons.Default.Call, contentDescription = null, tint = PureWhite)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Call 112 (European / International)", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = PureWhite)
      }

      // 988 (Crisis / Suicide Lifeline)
      Button(
        onClick = { dialNumber("988") },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
        shape = CircleShape,
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
      ) {
        Icon(Icons.Default.Call, contentDescription = null, tint = PureWhite)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Call 988 (Suicide & Crisis Lifeline)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PureWhite)
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFFEEEEEE))
        .padding(14.dp)
    ) {
      Text(
        text = "I am an AI companion, not an emergency dispatcher. Always prioritize connecting directly with medical professionals.",
        fontSize = 11.5.sp,
        color = Color(0xFF555555),
        lineHeight = 16.sp
      )
    }
  }
}
