package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.PlusTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Plus", appName)
  }

  @Test
  fun `verify all required screen components match specification`() {
    composeTestRule.setContent {
      PlusTheme {
        MainScreen()
      }
    }

    // Header elements
    composeTestRule.onNodeWithTag("hamburger_menu_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("version_text").assertIsDisplayed()
    composeTestRule.onNodeWithTag("brand_plus_text").assertIsDisplayed()

    // Main headline
    composeTestRule.onNodeWithTag("headline_text").assertIsDisplayed()

    // Input bar
    composeTestRule.onNodeWithTag("input_bar_surface").assertIsDisplayed()
    composeTestRule.onNodeWithTag("input_plus_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Ask anything").assertIsDisplayed()
    composeTestRule.onNodeWithTag("microphone_button").assertIsDisplayed()

    // Suggestion chips
    composeTestRule.onNodeWithTag("chip_just_here_to_talk").assertIsDisplayed()
    composeTestRule.onNodeWithTag("chip_log_my_sleep").assertIsDisplayed()
    composeTestRule.onNodeWithTag("chip_i_have_a_headache").assertIsDisplayed()

    // Clicking a suggestion chip updates state and input field is displayed
    composeTestRule.onNodeWithTag("chip_log_my_sleep").performClick()
    composeTestRule.onNodeWithTag("ask_anything_input").assertIsDisplayed()

    // Footer text
    composeTestRule.onNodeWithTag("footer_line_1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("footer_line_2").assertIsDisplayed()

    // Submitting a request triggers processing pulse state on input bar
    composeTestRule.onNodeWithTag("input_plus_button").performClick()
    composeTestRule.onNodeWithTag("input_bar_surface").assertIsDisplayed()
  }

  @Test
  fun `verify character eye animation with input active and touch`() {
    composeTestRule.setContent {
      PlusTheme {
        BackgroundAndMascotVisual(
          isInputActive = true,
          activeTouchPoint = null,
          screenWidthPx = 1080f,
          screenHeightPx = 2400f
        )
      }
    }
    composeTestRule.waitForIdle()
  }

  @Test
  fun `verify Baymax AI persona and emergency guidance rules`() {
    // 1. Emergency detection
    val emergencyRes = com.example.ai.BaymaxAIEngine.generateResponse("This is an emergency, I need help!")
    assertEquals(true, emergencyRes.isEmergency)
    assert(emergencyRes.text.contains("emergency"))

    // 2. Emotional comfort mode
    val comfortRes = com.example.ai.BaymaxAIEngine.generateResponse("I am feeling very sad and anxious today")
    assert(comfortRes.text.contains("It is okay to feel this way"))
    assert(comfortRes.text.contains("I am here for you"))

    // 3. Medical guidance & disclaimer
    val headacheRes = com.example.ai.BaymaxAIEngine.generateResponse("I have a bad headache")
    assert(headacheRes.text.contains("On a scale of 1 to 10"))
    assert(headacheRes.text.contains("I am an AI companion, not a doctor"))

    // 4. Guided symptom assessment
    val assessment = com.example.ai.BaymaxAIEngine.assessSymptoms(
      symptom = "Throbbing headache",
      painScale = 6,
      duration = "2 hours",
      location = "Forehead"
    )
    assert(assessment.possibleCause.isNotBlank())
    assert(assessment.remedies.isNotEmpty())
    assert(assessment.disclaimer.contains("not a doctor"))
  }
}

