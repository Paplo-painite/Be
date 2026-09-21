package com.example.data

import java.util.UUID

enum class Sender {
  USER,
  PLUS
}

data class ChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val sender: Sender,
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isEmergency: Boolean = false
)

enum class MoodRating(val score: Int, val emoji: String, val label: String) {
  VERY_LOW(1, "😔", "Down"),
  LOW(2, "🙁", "Not Great"),
  OKAY(3, "😐", "Okay"),
  GOOD(4, "🙂", "Good"),
  EXCELLENT(5, "😊", "Great")
}

data class MoodEntry(
  val id: String = UUID.randomUUID().toString(),
  val rating: MoodRating,
  val note: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

data class SleepEntry(
  val id: String = UUID.randomUUID().toString(),
  val hours: Float,
  val quality: String,
  val dateLabel: String,
  val tips: String
)

data class Medication(
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val dosage: String,
  val time: String,
  val isTaken: Boolean = false
)

enum class SymptomStep {
  INITIAL,
  PAIN_SCALE,
  DURATION,
  LOCATION,
  ASSESSMENT
}

data class SymptomAssessment(
  val symptom: String = "",
  val painScale: Int = 5,
  val duration: String = "",
  val location: String = "",
  val possibleCause: String = "",
  val remedies: List<String> = emptyList(),
  val disclaimer: String = "I am an AI companion, not a doctor. If symptoms persist or worsen, please consult a medical professional."
)
