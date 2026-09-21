package com.example.ai

import com.example.data.ChatMessage
import com.example.data.Sender
import com.example.data.SymptomAssessment

object BaymaxAIEngine {

  const val DISCLAIMER =
    "I am an AI companion, not a doctor. If symptoms persist or worsen, please consult a medical professional."

  const val GREETING =
    "Hello. I am Plus. I am your personal healthcare companion. How are you feeling, really?"

  private val emergencyKeywords = setOf(
    "emergency", "help", "call 911", "ambulance", "chest pain", "cannot breathe",
    "can't breathe", "heart attack", "stroke", "bleeding heavily", "suicide", "overdose"
  )

  private val sadnessKeywords = setOf(
    "sad", "depressed", "unhappy", "crying", "anxious", "anxiety", "scared", "fear",
    "lonely", "alone", "overwhelmed", "hopeless", "stressed", "grief", "hurting inside"
  )

  private val headacheKeywords = setOf(
    "headache", "head hurt", "migraine", "head pain", "temples"
  )

  private val sleepKeywords = setOf(
    "insomnia", "cannot sleep", "can't sleep", "tired", "exhausted", "sleep", "nightmare"
  )

  private val painKeywords = setOf(
    "pain", "hurt", "ache", "sore", "injury", "sprain", "stomach", "fever", "cough"
  )

  fun isEmergency(text: String): Boolean {
    val lower = text.lowercase()
    return emergencyKeywords.any { lower.contains(it) }
  }

  fun generateResponse(userMessage: String, userName: String = ""): ChatMessage {
    val lower = userMessage.trim().lowercase()

    // 1. Emergency Detection
    if (isEmergency(lower)) {
      return ChatMessage(
        sender = Sender.PLUS,
        text = "This sounds like an emergency. Please call your local emergency number immediately (911 in the US or 112 in Europe). I am here with you. Do not hesitate to seek immediate emergency care.",
        isEmergency = true
      )
    }

    // 2. Emotional Support / Comfort Mode
    if (sadnessKeywords.any { lower.contains(it) } || lower == "just here to talk") {
      val namePrefix = if (userName.isNotBlank()) "$userName, " else ""
      return ChatMessage(
        sender = Sender.PLUS,
        text = "${namePrefix}It is okay to feel this way. I am here for you. I am not going anywhere. We will get through this together. Would you like to tell me what is on your mind?"
      )
    }

    // 3. Headache symptoms
    if (headacheKeywords.any { lower.contains(it) }) {
      return ChatMessage(
        sender = Sender.PLUS,
        text = "You mentioned a headache. On a scale of 1 to 10, how would you rate your pain? How long have you had it? Resting in a dimly lit, quiet room and drinking cold water may provide relief. $DISCLAIMER"
      )
    }

    // 4. Sleep issues
    if (sleepKeywords.any { lower.contains(it) }) {
      return ChatMessage(
        sender = Sender.PLUS,
        text = "Restorative sleep is essential for your body and immune system. I recommend limiting blue light 30 minutes before rest and keeping your room cool and quiet. Would you like to log your sleep with me?"
      )
    }

    // 5. Pain & General Symptoms
    if (painKeywords.any { lower.contains(it) }) {
      return ChatMessage(
        sender = Sender.PLUS,
        text = "I have scanned your description. On a scale of 1 to 10, how would you rate your discomfort? Please rest the affected area and keep hydrated. $DISCLAIMER"
      )
    }

    // 6. Greetings & Inquiries
    if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey")) {
      val nameGreeting = if (userName.isNotBlank()) " $userName." else "."
      return ChatMessage(
        sender = Sender.PLUS,
        text = "Hello$nameGreeting I am Plus, your personal healthcare companion. How are you feeling today?"
      )
    }

    // 7. General Caretaking Response in Baymax's style
    return ChatMessage(
      sender = Sender.PLUS,
      text = "I understand. I am listening attentively. My sensors are monitoring your well-being. Please tell me more about how your body and mind are feeling today. $DISCLAIMER"
    )
  }

  fun assessSymptoms(
    symptom: String,
    painScale: Int,
    duration: String,
    location: String
  ): SymptomAssessment {
    val lower = symptom.lowercase()
    val possibleCause: String
    val remedies: List<String>

    when {
      lower.contains("headache") || lower.contains("migraine") -> {
        possibleCause = if (painScale > 6) "Tension or migraine headache with elevated discomfort"
        else "Mild tension headache, possibly related to screen fatigue or dehydration"
        remedies = listOf(
          "Rest in a quiet, dark room for 30-45 minutes.",
          "Drink 500ml of fresh water.",
          "Apply a cool cloth or gentle compress across your forehead.",
          "Gentle neck and shoulder stretching."
        )
      }
      lower.contains("stomach") || lower.contains("nausea") -> {
        possibleCause = "Mild gastrointestinal irritation or dietary sensitivity"
        remedies = listOf(
          "Sip warm ginger or peppermint tea.",
          "Avoid heavy, greasy, or spicy foods.",
          "Rest in an elevated position rather than lying flat.",
          "Stay hydrated with small, frequent sips of water."
        )
      }
      lower.contains("sleep") || lower.contains("tired") || lower.contains("fatigue") -> {
        possibleCause = "Circadian rhythm disruption or accumulated physical fatigue"
        remedies = listOf(
          "Maintain a consistent sleep schedule.",
          "Avoid caffeine after 2:00 PM.",
          "Ensure your bedroom temperature is cool (approximately 18°C / 65°F).",
          "Practice 5 minutes of slow diaphragmatic breathing."
        )
      }
      else -> {
        possibleCause = "General bodily discomfort or strain"
        remedies = listOf(
          "Allow your body adequate rest.",
          "Maintain steady hydration throughout the day.",
          "Monitor your symptoms closely over the next 12 to 24 hours.",
          "Avoid strenuous physical exertion."
        )
      }
    }

    return SymptomAssessment(
      symptom = symptom,
      painScale = painScale,
      duration = duration,
      location = location,
      possibleCause = possibleCause,
      remedies = remedies,
      disclaimer = DISCLAIMER
    )
  }
}
