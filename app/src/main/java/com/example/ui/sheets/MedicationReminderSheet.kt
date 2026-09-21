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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.Medication
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextBlack

@Composable
fun MedicationReminderSheet(
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val medications = remember {
    mutableStateListOf(
      Medication(name = "Vitamin D3", dosage = "2000 IU", time = "08:00 AM", isTaken = true),
      Medication(name = "Omega-3 Fish Oil", dosage = "1000 mg", time = "12:30 PM", isTaken = false),
      Medication(name = "Magnesium Glycinate", dosage = "200 mg", time = "09:00 PM", isTaken = false)
    )
  }

  var showAddDialog by remember { mutableStateOf(false) }
  var newName by remember { mutableStateOf("") }
  var newDosage by remember { mutableStateOf("") }
  var newTime by remember { mutableStateOf("08:00 AM") }

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
        modifier = Modifier.testTag("med_reminder_back_button")
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
          text = "Medication Schedule",
          fontFamily = PlayfairFontFamily,
          fontSize = 19.sp,
          fontWeight = FontWeight.Medium,
          color = TextBlack
        )
        Text(
          text = "Gentle dosage reminders",
          fontSize = 12.sp,
          color = Color(0xFF757575)
        )
      }
    }

    // Baymax Reminder Banner
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .padding(16.dp)
    ) {
      Row(verticalAlignment = Alignment.Top) {
        Text("●—● ", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "It is time for your medication. I am here to remind you. Adherence to your regimen maintains physical balance.",
          fontSize = 13.5.sp,
          color = TextBlack,
          lineHeight = 19.sp
        )
      }
    }

    // Medication List
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(medications) { med ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = med.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = TextBlack
              )
              Text(
                text = "${med.dosage} • ${med.time}",
                fontSize = 13.sp,
                color = Color.Gray
              )
            }

            Checkbox(
              checked = med.isTaken,
              onCheckedChange = { checked ->
                val index = medications.indexOf(med)
                if (index != -1) {
                  medications[index] = med.copy(isTaken = checked)
                }
              },
              colors = CheckboxDefaults.colors(
                checkedColor = TextBlack,
                checkmarkColor = PureWhite
              )
            )
          }
        }
      }

      item {
        Button(
          onClick = { showAddDialog = !showAddDialog },
          colors = ButtonDefaults.buttonColors(containerColor = TextBlack),
          shape = CircleShape,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("add_medication_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = PureWhite)
          Spacer(modifier = Modifier.width(8.dp))
          Text(if (showAddDialog) "Cancel" else "Add Medication", color = PureWhite, fontSize = 15.sp)
        }
      }

      if (showAddDialog) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color.White)
              .padding(18.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "New Prescription / Supplement",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextBlack
              )
              OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Medication Name") },
                modifier = Modifier.fillMaxWidth()
              )
              OutlinedTextField(
                value = newDosage,
                onValueChange = { newDosage = it },
                label = { Text("Dosage (e.g. 500mg)") },
                modifier = Modifier.fillMaxWidth()
              )
              OutlinedTextField(
                value = newTime,
                onValueChange = { newTime = it },
                label = { Text("Scheduled Time") },
                modifier = Modifier.fillMaxWidth()
              )
              Button(
                onClick = {
                  if (newName.isNotBlank()) {
                    medications.add(
                      Medication(name = newName, dosage = newDosage, time = newTime)
                    )
                    newName = ""
                    newDosage = ""
                    showAddDialog = false
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TextBlack),
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Save to Plus Reminders", color = PureWhite)
              }
            }
          }
        }
      }
    }
  }
}
