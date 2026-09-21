package com.example

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.mutableStateListOf
import com.example.ai.BaymaxAIEngine
import com.example.data.ChatMessage
import com.example.data.Sender
import com.example.ui.sheets.AppDestination
import com.example.ui.sheets.ChatSheet
import com.example.ui.sheets.EmergencySheet
import com.example.ui.sheets.MedicationReminderSheet
import com.example.ui.sheets.MoodTrackerSheet
import com.example.ui.sheets.NavigationDrawerSheet
import com.example.ui.sheets.SleepLoggerSheet
import com.example.ui.sheets.SymptomCheckerSheet
import com.example.ui.theme.BgGradientBottom
import com.example.ui.theme.BgGradientMid
import com.example.ui.theme.BgGradientTop
import com.example.ui.theme.BgGradientUpperMid
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PlusTheme
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextChip
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextFooter
import com.example.ui.theme.TextFooterSecondary
import com.example.ui.theme.TextLightGray
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextPlaceholder
import com.example.ui.theme.TextVeryLightGray
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PlusTheme {
        MainScreen()
      }
    }
  }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
  var inputText by remember { mutableStateOf("") }
  var isProcessing by remember { mutableStateOf(false) }
  var isInputFocused by remember { mutableStateOf(false) }
  var activeTouchPoint by remember { mutableStateOf<Offset?>(null) }
  val focusManager = LocalFocusManager.current
  val coroutineScope = rememberCoroutineScope()

  var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
  var pendingDestination by remember { mutableStateOf<AppDestination?>(null) }
  var showNavMenu by remember { mutableStateOf(false) }
  var initialSymptom by remember { mutableStateOf("Headache") }

  val context = LocalContext.current
  var tts by remember { mutableStateOf<TextToSpeech?>(null) }
  var isTtsReady by remember { mutableStateOf(false) }
  var ttsEnabled by remember { mutableStateOf(true) }

  DisposableEffect(context) {
    var textToSpeech: TextToSpeech? = null
    try {
      textToSpeech = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
          textToSpeech?.language = Locale.US
          textToSpeech?.setSpeechRate(0.85f)
          textToSpeech?.setPitch(0.95f)
          isTtsReady = true
        }
      }
    } catch (_: Exception) {
    }
    tts = textToSpeech
    onDispose {
      try {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
      } catch (_: Exception) {
      }
    }
  }

  val speak: (String) -> Unit = { text ->
    if (ttsEnabled && isTtsReady) {
      try {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "baymax_speech")
      } catch (_: Exception) {
      }
    }
  }

  val chatMessages = remember {
    mutableStateListOf(
      ChatMessage(
        sender = Sender.PLUS,
        text = BaymaxAIEngine.GREETING
      )
    )
  }

  val isInputActive = isInputFocused || isProcessing || inputText.isNotEmpty()

  LaunchedEffect(isProcessing) {
    if (isProcessing) {
      delay(1200)
      isProcessing = false
      if (pendingDestination != null) {
        currentDestination = pendingDestination!!
        pendingDestination = null
      }
    }
  }

  val handleSubmit: () -> Unit = {
    focusManager.clearFocus()
    isProcessing = true
    val query = inputText.trim()
    if (query.isNotEmpty()) {
      if (BaymaxAIEngine.isEmergency(query)) {
        chatMessages.add(ChatMessage(sender = Sender.USER, text = query))
        val res = BaymaxAIEngine.generateResponse(query)
        chatMessages.add(res)
        speak(res.text)
        pendingDestination = AppDestination.EMERGENCY
      } else if (query.lowercase().contains("sleep")) {
        pendingDestination = AppDestination.SLEEP_LOGGER
      } else if (query.lowercase().contains("headache") || query.lowercase().contains("pain")) {
        initialSymptom = query
        pendingDestination = AppDestination.SYMPTOM_CHECKER
      } else {
        chatMessages.add(ChatMessage(sender = Sender.USER, text = query))
        val res = BaymaxAIEngine.generateResponse(query)
        chatMessages.add(res)
        speak(res.text)
        pendingDestination = AppDestination.CHAT
      }
    }
  }

  val density = LocalDensity.current

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        awaitEachGesture {
          val down = awaitFirstDown(requireUnconsumed = false)
          activeTouchPoint = down.position
          do {
            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
            val change = event.changes.firstOrNull()
            if (change != null) {
              activeTouchPoint = change.position
            }
          } while (event.changes.any { it.pressed })
          activeTouchPoint = null
        }
      }
  ) {
    val screenWidthPx = with(density) { maxWidth.toPx() }
    val screenHeightPx = with(density) { maxHeight.toPx() }

    // [GENERAL BACKGROUND] & [BOTTOM VISUAL]
    BackgroundAndMascotVisual(
      isInputActive = isInputActive,
      activeTouchPoint = activeTouchPoint,
      screenWidthPx = screenWidthPx,
      screenHeightPx = screenHeightPx,
      modifier = Modifier.fillMaxSize()
    )

    // Main UI Content Layer
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // [HEADER]
      HeaderSection(
        onMenuClick = { showNavMenu = true },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 12.dp)
      )

      Spacer(modifier = Modifier.height(34.dp))

      // [MAIN HEADLINE]
      HeadlineSection(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
      )

      Spacer(modifier = Modifier.height(26.dp))

      // [INPUT BAR]
      InputBarSection(
        text = inputText,
        isProcessing = isProcessing,
        onTextChange = { inputText = it },
        onSubmit = handleSubmit,
        onFocusChange = { isInputFocused = it },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
      )

      Spacer(modifier = Modifier.height(18.dp))

      // [SUGGESTION CHIPS]
      SuggestionChipsSection(
        onChipClick = { chipText ->
          inputText = chipText
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // [FOOTER TEXT]
      FooterTextSection(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 28.dp)
      )

      // The remaining lower portion displays the [BOTTOM VISUAL] in the canvas background
      Spacer(modifier = Modifier.weight(1f))
    }

    // Fullscreen / Slide-over sheet overlays for features
    AnimatedVisibility(
      visible = currentDestination != AppDestination.HOME,
      enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
      exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
    ) {
      when (currentDestination) {
        AppDestination.CHAT -> ChatSheet(
          messages = chatMessages,
          onSendMessage = { newText ->
            chatMessages.add(ChatMessage(sender = Sender.USER, text = newText))
            val reply = BaymaxAIEngine.generateResponse(newText)
            chatMessages.add(reply)
            speak(reply.text)
          },
          onSpeak = { speak(it) },
          onClose = { currentDestination = AppDestination.HOME }
        )
        AppDestination.SYMPTOM_CHECKER -> SymptomCheckerSheet(
          initialSymptom = initialSymptom,
          onClose = { currentDestination = AppDestination.HOME }
        )
        AppDestination.MOOD_TRACKER -> MoodTrackerSheet(
          onClose = { currentDestination = AppDestination.HOME }
        )
        AppDestination.SLEEP_LOGGER -> SleepLoggerSheet(
          onClose = { currentDestination = AppDestination.HOME }
        )
        AppDestination.MEDICATION_REMINDER -> MedicationReminderSheet(
          onClose = { currentDestination = AppDestination.HOME }
        )
        AppDestination.EMERGENCY -> EmergencySheet(
          onClose = { currentDestination = AppDestination.HOME }
        )
        AppDestination.HOME -> {}
      }
    }

    // Navigation Drawer Sheet Overlay
    AnimatedVisibility(
      visible = showNavMenu,
      enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
      exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
    ) {
      NavigationDrawerSheet(
        currentDestination = currentDestination,
        onNavigate = { dest ->
          currentDestination = dest
          showNavMenu = false
        },
        ttsEnabled = ttsEnabled,
        onToggleTts = { ttsEnabled = it },
        onClose = { showNavMenu = false }
      )
    }
  }
}

private data class GazeTarget(
  val x: Float,
  val y: Float,
  val tilt: Float,
  val duration: Int,
  val easing: androidx.compose.animation.core.Easing
)

/**
 * [GENERAL BACKGROUND] & [BOTTOM VISUAL]
 * - Vertical gradient background:
 *   - From top to mid-screen: pure white #FFFFFF fading gradually to very light gray #F5F5F5.
 *   - From mid-screen to bottom: starts with light gray #E0E0E0 and fades to darker gray #A0A0A0,
 *     with soft undulating waves at the mid-screen edge resembling hills.
 * - [BOTTOM VISUAL]:
 *   - Located in the lower half of the screen above the gray gradient background.
 *   - Two black dark ovals, slightly tilted inward.
 *   - A thin black line curving slightly upward connecting them (resembling eyes or black glasses).
 *   - A very light circular halo or gradient around them in the gray background to make them stand out.
 *   - Animated eyes: reacts to accelerometer tilt, finger touch tracking, and looks towards the input area when active.
 */
@Composable
fun BackgroundAndMascotVisual(
  isInputActive: Boolean = false,
  activeTouchPoint: Offset? = null,
  screenWidthPx: Float = 0f,
  screenHeightPx: Float = 0f,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val sensorManager = remember(context) { context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager }
  val accelerometer = remember(sensorManager) { sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

  // Accelerometer tilt offset (smoothed in real-time)
  var accelBiasX by remember { mutableStateOf(0f) }
  var accelBiasY by remember { mutableStateOf(0f) }

  DisposableEffect(accelerometer) {
    if (accelerometer == null) return@DisposableEffect onDispose {}
    val listener = object : SensorEventListener {
      override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
          val rawX = event.values[0]
          val rawY = event.values[1]
          // Portrait orientation:
          // Tilting device to the right produces negative X acceleration
          // Tilting device to the left produces positive X acceleration
          // Tilting top towards user produces rawY < 9.8
          val targetBiasX = (-rawX * 3.4f).coerceIn(-28f, 28f)
          val targetBiasY = ((rawY - 6.8f) * 2.6f).coerceIn(-22f, 22f)
          accelBiasX = accelBiasX * 0.82f + targetBiasX * 0.18f
          accelBiasY = accelBiasY * 0.82f + targetBiasY * 0.18f
        }
      }
      override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
    sensorManager?.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    onDispose {
      sensorManager?.unregisterListener(listener)
    }
  }

  // Gentle ambient breathing animation
  val infiniteTransition = rememberInfiniteTransition(label = "breathing")
  val breathPhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3600, easing = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breath_phase"
  )

  // Smooth gaze direction animatables (offsets in dp & tilt in degrees)
  val lookOffsetX = remember { Animatable(0f) }
  val lookOffsetY = remember { Animatable(0f) }
  val lookTilt = remember { Animatable(0f) }

  // Lifelike blinking animatable
  val blinkScaleY = remember { Animatable(1f) }

  // Ambient gaze directions
  var ambientTargetX by remember { mutableStateOf(0f) }
  var ambientTargetY by remember { mutableStateOf(0f) }
  var ambientTargetTilt by remember { mutableStateOf(0f) }

  // Cute gaze shifts: looking around in different directions with smooth cubic-bezier easing
  LaunchedEffect(isInputActive, activeTouchPoint) {
    if (isInputActive || activeTouchPoint != null) return@LaunchedEffect
    val gazeDirections = listOf(
      Triple(0f, 0f, 0f),
      Triple(-20f, -3f, -3.0f),
      Triple(-14f, -12f, -1.8f),
      Triple(0f, 0f, 0f),
      Triple(0f, -15f, 0f),
      Triple(20f, -3f, 3.0f),
      Triple(14f, -12f, 1.8f),
      Triple(0f, 0f, 0f),
      Triple(0f, 7f, 0f),
      Triple(0f, 0f, 0f)
    )

    var gazeIndex = 0
    while (isActive) {
      val (targetX, targetY, targetTilt) = gazeDirections[gazeIndex % gazeDirections.size]
      gazeIndex++

      // Living creatures often give a soft cute blink when redirecting their gaze
      if (Random.nextFloat() < 0.40f) {
        launch {
          blinkScaleY.animateTo(0.08f, tween(75, easing = FastOutLinearInEasing))
          blinkScaleY.animateTo(1f, tween(110, easing = LinearOutSlowInEasing))
        }
      }

      ambientTargetX = targetX
      ambientTargetY = targetY
      ambientTargetTilt = targetTilt

      // Dwell time: longer when looking straight at the user, shorter when curious glances
      val isCenter = targetX == 0f && targetY == 0f
      val dwellTime = if (isCenter) Random.nextLong(2800, 4200) else Random.nextLong(2000, 3200)
      delay(dwellTime)
    }
  }

  // Active gaze resolver: smooth animation logic combining touch position, accelerometer tilt, and input focus
  LaunchedEffect(
    isInputActive,
    activeTouchPoint,
    accelBiasX,
    accelBiasY,
    ambientTargetX,
    ambientTargetY,
    ambientTargetTilt
  ) {
    val target = when {
      // 1. Touch tracking: follow the user's finger smoothly on screen
      activeTouchPoint != null -> {
        val safeW = if (screenWidthPx > 0f) screenWidthPx else 1000f
        val safeH = if (screenHeightPx > 0f) screenHeightPx else 2000f
        val eyeOriginX = safeW / 2f
        val eyeOriginY = safeH * 0.66f
        val dx = (activeTouchPoint.x - eyeOriginX) / (safeW / 2f)
        val dy = (activeTouchPoint.y - eyeOriginY) / (safeH / 2f)
        val tx = (dx * 36f + accelBiasX * 0.25f).coerceIn(-38f, 38f)
        val ty = (dy * 40f + accelBiasY * 0.25f).coerceIn(-38f, 24f)
        val tt = (tx * 0.14f).coerceIn(-5.5f, 5.5f)
        GazeTarget(tx, ty, tt, duration = 140, easing = LinearOutSlowInEasing)
      }
      // 2. Input area active: character looks directly up towards the input area with attentive focus!
      isInputActive -> {
        val tx = (accelBiasX * 0.45f).coerceIn(-24f, 24f)
        val ty = (-34f + accelBiasY * 0.35f).coerceIn(-42f, -22f)
        val tt = (tx * 0.1f).coerceIn(-3.5f, 3.5f)
        GazeTarget(tx, ty, tt, duration = 320, easing = FastOutSlowInEasing)
      }
      // 3. Device accelerometer tilt + ambient look-around
      else -> {
        val tx = (ambientTargetX + accelBiasX).coerceIn(-38f, 38f)
        val ty = (ambientTargetY + accelBiasY).coerceIn(-38f, 24f)
        val tt = (ambientTargetTilt + accelBiasX * 0.12f).coerceIn(-5.5f, 5.5f)
        GazeTarget(tx, ty, tt, duration = 580, easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f))
      }
    }

    launch {
      lookOffsetX.animateTo(target.x, tween(target.duration, easing = target.easing))
    }
    launch {
      lookOffsetY.animateTo(target.y, tween(target.duration, easing = target.easing))
    }
    launch {
      lookTilt.animateTo(target.tilt, tween(target.duration, easing = target.easing))
    }
  }

  // Periodic natural blink loop
  LaunchedEffect(Unit) {
    while (isActive) {
      delay(Random.nextLong(2600, 4600))
      // Quick blink closure
      blinkScaleY.animateTo(
        targetValue = 0.05f,
        animationSpec = tween(durationMillis = 80, easing = FastOutLinearInEasing)
      )
      // Smooth opening
      blinkScaleY.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 125, easing = LinearOutSlowInEasing)
      )

      // Occasional curious double-blink
      if (Random.nextFloat() < 0.28f) {
        delay(110)
        blinkScaleY.animateTo(
          targetValue = 0.05f,
          animationSpec = tween(durationMillis = 70, easing = FastOutLinearInEasing)
        )
        blinkScaleY.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 110, easing = LinearOutSlowInEasing)
        )
      }
    }
  }

  val coroutineScope = rememberCoroutineScope()

  Canvas(
    modifier = modifier.clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = null,
      onClick = {
        coroutineScope.launch {
          // Look directly at user with happy cute response
          launch { lookOffsetX.animateTo(0f, tween(320, easing = FastOutSlowInEasing)) }
          launch { lookOffsetY.animateTo(0f, tween(320, easing = FastOutSlowInEasing)) }
          launch {
            val tilt = if (Random.nextBoolean()) 5.5f else -5.5f
            lookTilt.animateTo(tilt, tween(260, easing = FastOutSlowInEasing))
            delay(350)
            lookTilt.animateTo(0f, tween(380, easing = FastOutSlowInEasing))
          }
          // Cheerful double-blink
          blinkScaleY.animateTo(0.05f, tween(70))
          blinkScaleY.animateTo(1f, tween(100))
          delay(80)
          blinkScaleY.animateTo(0.05f, tween(70))
          blinkScaleY.animateTo(1f, tween(100))
        }
      }
    )
  ) {
    val width = size.width
    val height = size.height
    val centerX = width / 2f

    // 1. Full-screen base vertical gradient:
    // Pure white #FFFFFF at top fading to #F5F5F5 at mid-screen,
    // then #E0E0E0 fading to darker gray #A0A0A0 at the bottom.
    drawRect(
      brush = Brush.verticalGradient(
        colorStops = arrayOf(
          0.00f to BgGradientTop,       // #FFFFFF
          0.44f to BgGradientUpperMid,  // #F5F5F5
          0.54f to BgGradientMid,       // #E0E0E0
          0.78f to Color(0xFFB4B4B4),   // Soft gradient mid-tone
          1.00f to BgGradientBottom     // #A0A0A0
        ),
        startY = 0f,
        endY = height
      )
    )

    // Gentle vertical breathing offset
    val breathDisplacementY = (breathPhase - 0.5f) * 3.dp.toPx()

    // 2. Soft undulating waves at the mid-screen edge resembling hills / Baymax head dome
    val hillTopY = height * 0.52f + breathDisplacementY
    val hillPath = Path().apply {
      moveTo(0f, height * 0.68f)
      cubicTo(
        width * 0.22f, hillTopY,
        width * 0.78f, hillTopY,
        width, height * 0.68f
      )
      lineTo(width, height)
      lineTo(0f, height)
      close()
    }

    // Secondary subtle undulating wave layer behind it
    val secondaryWavePath = Path().apply {
      moveTo(0f, height * 0.62f)
      cubicTo(
        width * 0.35f, height * 0.50f + breathDisplacementY * 0.7f,
        width * 0.65f, height * 0.56f + breathDisplacementY * 0.7f,
        width, height * 0.58f
      )
      lineTo(width, height)
      lineTo(0f, height)
      close()
    }

    drawPath(
      path = secondaryWavePath,
      brush = Brush.verticalGradient(
        colors = listOf(
          Color.White.copy(alpha = 0.25f),
          Color.Transparent
        ),
        startY = height * 0.48f,
        endY = height * 0.72f
      )
    )

    // Primary hill shape with soft smooth gradient
    drawPath(
      path = hillPath,
      brush = Brush.verticalGradient(
        colors = listOf(
          Color(0xFFFCFCFC).copy(alpha = 0.88f),
          Color(0xFFEEEEEE).copy(alpha = 0.55f),
          Color.Transparent
        ),
        startY = hillTopY,
        endY = height * 0.88f
      )
    )

    // Subtle edge highlight on top of the undulating hill
    val hillEdgePath = Path().apply {
      moveTo(0f, height * 0.68f)
      cubicTo(
        width * 0.22f, hillTopY,
        width * 0.78f, hillTopY,
        width, height * 0.68f
      )
    }
    drawPath(
      path = hillEdgePath,
      brush = Brush.horizontalGradient(
        colors = listOf(
          Color.Transparent,
          Color.White.copy(alpha = 0.6f),
          Color.White.copy(alpha = 0.9f),
          Color.White.copy(alpha = 0.6f),
          Color.Transparent
        )
      ),
      style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
    )

    // 3. [BOTTOM VISUAL - CUTE EYES LOOKING AROUND]
    val currentGazeX = lookOffsetX.value.dp.toPx()
    val currentGazeY = lookOffsetY.value.dp.toPx()
    val currentEyeCenterX = centerX + currentGazeX
    val currentEyeCenterY = height * 0.66f + breathDisplacementY + currentGazeY
    val eyeSpacing = 132.dp.toPx()

    // A light circular halo that softly tracks gaze with subtle parallax
    val haloRadius = width * 0.52f
    val haloCenter = Offset(centerX + currentGazeX * 0.55f, currentEyeCenterY)
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.White.copy(alpha = 0.65f),
          Color(0xFFFAFAFA).copy(alpha = 0.40f),
          Color(0xFFEEEEEE).copy(alpha = 0.18f),
          Color.Transparent
        ),
        center = haloCenter,
        radius = haloRadius
      ),
      center = haloCenter,
      radius = haloRadius
    )

    // Eye dimensions
    val eyeWidth = 46.dp.toPx()
    val eyeHeight = 58.dp.toPx() * blinkScaleY.value.coerceAtLeast(0.06f)

    // Render eyes with cute glance tilt
    withTransform({
      rotate(degrees = lookTilt.value, pivot = Offset(currentEyeCenterX, currentEyeCenterY))
    }) {
      val leftEyeCenter = Offset(currentEyeCenterX - eyeSpacing / 2f, currentEyeCenterY)
      val rightEyeCenter = Offset(currentEyeCenterX + eyeSpacing / 2f, currentEyeCenterY)

      // Thin black line curving slightly upward connecting the eyes
      val lineStartX = leftEyeCenter.x + eyeWidth * 0.35f
      val lineStartY = leftEyeCenter.y - 2.dp.toPx()
      val lineEndX = rightEyeCenter.x - eyeWidth * 0.35f
      val lineEndY = rightEyeCenter.y - 2.dp.toPx()
      val lineControlY = currentEyeCenterY - 12.dp.toPx()

      val connectingLinePath = Path().apply {
        moveTo(lineStartX, lineStartY)
        quadraticTo(
          currentEyeCenterX,
          lineControlY,
          lineEndX,
          lineEndY
        )
      }

      drawPath(
        path = connectingLinePath,
        color = TextBlack,
        style = Stroke(
          width = 2.8.dp.toPx(),
          cap = StrokeCap.Round
        )
      )

      // Two black dark ovals, slightly tilted inward (+22° and -22°)
      rotate(degrees = 22f, pivot = leftEyeCenter) {
        drawOval(
          color = TextBlack,
          topLeft = Offset(leftEyeCenter.x - eyeWidth / 2f, leftEyeCenter.y - eyeHeight / 2f),
          size = Size(eyeWidth, eyeHeight)
        )
      }

      rotate(degrees = -22f, pivot = rightEyeCenter) {
        drawOval(
          color = TextBlack,
          topLeft = Offset(rightEyeCenter.x - eyeWidth / 2f, rightEyeCenter.y - eyeHeight / 2f),
          size = Size(eyeWidth, eyeHeight)
        )
      }
    }
  }
}

/**
 * Professional Liquid Glass (Glassmorphism) container.
 * - Backdrop blur: creates authentic frosted glass diffusion using Gaussian dispersion.
 * - Glass body: semi-transparent white tint (between 0.4 and 0.6 alpha).
 * - Specular highlight: light white gradient from opacity 0.15 to 0 across the top 50%.
 * - Inner top highlight: subtle bright reflection along the top rim.
 * - Glass edge: crisp 1px solid translucent white border.
 * - Outer shadow: soft, deep drop shadow.
 * - Keeps all foreground content 100% sharp, readable, and high-contrast.
 */
@Composable
fun LiquidGlassSurface(
  modifier: Modifier = Modifier,
  shape: Shape = CircleShape,
  backgroundColor: Color,
  blurRadius: Dp,
  borderColor: Color,
  borderWidth: Dp = 1.dp,
  specularAlpha: Float = 0.15f,
  shadowElevation: Dp,
  shadowColor: Color,
  ambientColor: Color = shadowColor.copy(alpha = shadowColor.alpha * 0.5f),
  content: @Composable BoxScope.() -> Unit
) {
  Box(
    modifier = modifier
      .shadow(
        elevation = shadowElevation,
        shape = shape,
        spotColor = shadowColor,
        ambientColor = ambientColor
      )
      .clip(shape)
      .border(
        width = borderWidth,
        color = borderColor,
        shape = shape
      )
  ) {
    // 1. Backdrop blur diffusion layer (frosted Gaussian dispersion)
    Box(
      modifier = Modifier
        .matchParentSize()
        .blur(radius = blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color(0xFFFFFFFF).copy(alpha = 0.45f),
              Color(0xFFF3F3F3).copy(alpha = 0.30f),
              Color(0xFFE8E8E8).copy(alpha = 0.20f)
            )
          )
        )
    )

    // 2. Liquid glass base tint (semi-transparent white)
    Box(
      modifier = Modifier
        .matchParentSize()
        .background(backgroundColor)
    )

    // 3. Subtle specular highlight: light white gradient opacity 0.15 to 0 across top 50%
    Box(
      modifier = Modifier
        .matchParentSize()
        .background(
          brush = Brush.verticalGradient(
            0.0f to Color.White.copy(alpha = specularAlpha),
            0.18f to Color.White.copy(alpha = specularAlpha * 0.70f),
            0.50f to Color.Transparent,
            1.0f to Color.Transparent
          )
        )
    )

    // 4. Inner highlight: subtle white gradient reflection at top edge
    Canvas(modifier = Modifier.matchParentSize()) {
      val innerGlowHeight = size.height * 0.35f
      drawRect(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color.White.copy(alpha = 0.35f),
            Color.Transparent
          ),
          startY = 0f,
          endY = innerGlowHeight
        ),
        size = Size(size.width, innerGlowHeight)
      )
    }

    // 5. Crisp, unblurred foreground content
    content()
  }
}

/**
 * [HEADER]
 * - Top-left: circular button with white background, containing three equal horizontal black lines (Hamburger Menu).
 * - Top-right: text "Plus" in Serif font (like Times New Roman) in black, relatively large.
 *   Directly above and to its right, the number "0.2" in a very small light gray font.
 */
@Composable
fun HeaderSection(
  onMenuClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    // Top-left: circular button with Liquid Glass effect & hamburger menu
    LiquidGlassSurface(
      shape = CircleShape,
      backgroundColor = Color.White.copy(alpha = 0.60f),
      blurRadius = 15.dp,
      borderColor = Color.White.copy(alpha = 0.70f),
      borderWidth = 1.dp,
      specularAlpha = 0.15f,
      shadowElevation = 8.dp,
      shadowColor = Color.Black.copy(alpha = 0.09f),
      ambientColor = Color.Black.copy(alpha = 0.04f),
      modifier = Modifier
        .size(48.dp)
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = ripple(bounded = true),
          onClick = onMenuClick
        )
        .testTag("hamburger_menu_button")
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
      ) {
        Canvas(modifier = Modifier.size(20.dp)) {
          val strokeWidth = 2.2.dp.toPx()
          val lineLength = 17.dp.toPx()
          val startX = (size.width - lineLength) / 2f
          val endX = startX + lineLength

          // Top line
          drawLine(
            color = TextBlack,
            start = Offset(startX, size.height * 0.24f),
            end = Offset(endX, size.height * 0.24f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
          )
          // Middle line
          drawLine(
            color = TextBlack,
            start = Offset(startX, size.height * 0.50f),
            end = Offset(endX, size.height * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
          )
          // Bottom line
          drawLine(
            color = TextBlack,
            start = Offset(startX, size.height * 0.76f),
            end = Offset(endX, size.height * 0.76f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
          )
        }
      }
    }

    // Top-right: "Plus" in Serif font in black, and "0.2" directly above and to its right
    Column(
      horizontalAlignment = Alignment.End,
      modifier = Modifier.padding(top = 2.dp)
    ) {
      Text(
        text = "0.2",
        fontFamily = PlayfairFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = TextVeryLightGray,
        modifier = Modifier
          .offset(x = 2.dp, y = 2.dp)
          .testTag("version_text")
      )
      Text(
        text = "Plus",
        fontFamily = PlayfairFontFamily,
        fontSize = 28.sp,
        fontWeight = FontWeight.Normal,
        color = TextBlack,
        letterSpacing = (-0.3).sp,
        modifier = Modifier
          .offset(y = (-4).dp)
          .testTag("brand_plus_text")
      )
    }
  }
}

/**
 * [MAIN HEADLINE]
 * - Located in the upper-center area.
 * - Text: "How are you feeling, really?"
 * - Font: Serif (like Georgia or Playfair Display), very large size, Regular weight.
 * - Color: dark gray #333333.
 * - Layout: two lines. Line 1: "How are you", Line 2: "feeling, really?" with a question mark.
 */
@Composable
fun HeadlineSection(modifier: Modifier = Modifier) {
  val headline = buildAnnotatedString {
    withStyle(SpanStyle(color = Color(0xFF262626), fontWeight = FontWeight.Normal)) {
      append("How are ")
    }
    withStyle(SpanStyle(color = TextMutedGray, fontWeight = FontWeight.Normal)) {
      append("you\n")
    }
    withStyle(SpanStyle(color = Color(0xFF262626), fontWeight = FontWeight.Normal)) {
      append("feeling, ")
    }
    withStyle(SpanStyle(color = TextLightGray, fontWeight = FontWeight.Normal)) {
      append("really?")
    }
  }

  Text(
    text = headline,
    fontFamily = PlayfairFontFamily,
    fontSize = 46.sp,
    lineHeight = 52.sp,
    letterSpacing = (-0.7).sp,
    modifier = modifier.testTag("headline_text")
  )
}

/**
 * [INPUT BAR]
 * - Directly below the main headline.
 * - Shape: fully rounded rectangle (Pill shape), white background, with a soft drop shadow.
 * - Inside, from left to right:
 *   - Circular black button with a white plus (+) sign.
 *   - Center: faded light gray text "Ask anything".
 *   - Right: black microphone icon.
 */
@Composable
fun InputBarSection(
  text: String,
  isProcessing: Boolean = false,
  onTextChange: (String) -> Unit,
  onSubmit: () -> Unit,
  onFocusChange: (Boolean) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val pulseTransition = rememberInfiniteTransition(label = "pulse_transition")

  val pulseScale by if (isProcessing) {
    pulseTransition.animateFloat(
      initialValue = 1f,
      targetValue = 1.028f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 800, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)),
        repeatMode = RepeatMode.Reverse
      ),
      label = "pulse_scale"
    )
  } else {
    remember { mutableStateOf(1f) }
  }

  val pulseElevation by if (isProcessing) {
    pulseTransition.animateFloat(
      initialValue = 9f,
      targetValue = 16f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 800, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)),
        repeatMode = RepeatMode.Reverse
      ),
      label = "pulse_elevation"
    )
  } else {
    remember { mutableStateOf(9f) }
  }

  val pulseGlowAlpha by if (isProcessing) {
    pulseTransition.animateFloat(
      initialValue = 0.08f,
      targetValue = 0.28f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 800, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)),
        repeatMode = RepeatMode.Reverse
      ),
      label = "pulse_glow"
    )
  } else {
    remember { mutableStateOf(0f) }
  }

  LiquidGlassSurface(
    shape = CircleShape,
    backgroundColor = Color.White.copy(alpha = 0.55f),
    blurRadius = 24.dp,
    borderColor = if (isProcessing) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.70f),
    borderWidth = 1.dp,
    specularAlpha = 0.15f,
    shadowElevation = pulseElevation.dp,
    shadowColor = if (isProcessing) Color.Black.copy(alpha = 0.14f) else Color.Black.copy(alpha = 0.10f),
    ambientColor = Color.Black.copy(alpha = 0.05f),
    modifier = modifier
      .height(58.dp)
      .graphicsLayer {
        scaleX = pulseScale
        scaleY = pulseScale
      }
      .then(
        if (isProcessing) {
          Modifier.border(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
              colors = listOf(
                Color.Black.copy(alpha = pulseGlowAlpha),
                Color.Black.copy(alpha = pulseGlowAlpha * 0.4f),
                Color.Black.copy(alpha = pulseGlowAlpha)
              )
            ),
            shape = CircleShape
          )
        } else Modifier
      )
      .testTag("input_bar_surface")
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(start = 9.dp, end = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Circular black button with a white plus (+) sign
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true, color = Color.White),
            onClick = onSubmit
          )
          .shadow(
            elevation = 2.dp,
            shape = CircleShape
          )
          .testTag("input_plus_button"),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawCircle(color = TextBlack)
        }
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add",
          tint = PureWhite,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      // Center: faded light gray text "Ask anything" / text field
      Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.CenterStart
      ) {
        if (text.isEmpty()) {
          Text(
            text = "Ask anything",
            fontFamily = FontFamily.SansSerif,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = TextPlaceholder
          )
        }
        BasicTextField(
          value = text,
          onValueChange = onTextChange,
          textStyle = TextStyle(
            color = TextBlack,
            fontSize = 15.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal
          ),
          cursorBrush = SolidColor(TextBlack),
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(onSend = { onSubmit() }),
          modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { onFocusChange(it.isFocused) }
            .testTag("ask_anything_input")
        )
      }

      // Right: black microphone icon
      IconButton(
        onClick = onSubmit,
        modifier = Modifier
          .size(40.dp)
          .testTag("microphone_button")
      ) {
        Icon(
          imageVector = Icons.Outlined.Mic,
          contentDescription = "Microphone",
          tint = TextBlack,
          modifier = Modifier.size(23.dp)
        )
      }
    }
  }
}

/**
 * [SUGGESTION CHIPS]
 * - Three horizontal buttons below the input bar.
 * - Design: fully rounded rectangles, white background, very soft shadow.
 * - Text in small Sans-serif font, dark gray color:
 *   1. "Just here to talk"
 *   2. "Log my sleep"
 *   3. "I have a headache"
 */
@Composable
fun SuggestionChipsSection(
  onChipClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val chips = listOf(
    "Just here to talk",
    "Log my sleep",
    "I have a headache"
  )

  val density = LocalDensity.current
  val offsetYPx = with(density) { 16.dp.toPx() }

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically
  ) {
    chips.forEachIndexed { index, text ->
      val animProgress = remember { Animatable(0f) }
      LaunchedEffect(Unit) {
        // Sequential staggered entrance when the app first loads
        delay(120L + index * 120L)
        animProgress.animateTo(
          targetValue = 1f,
          animationSpec = tween(
            durationMillis = 420,
            easing = CubicBezierEasing(0.16f, 1.0f, 0.3f, 1.0f)
          )
        )
      }

      SuggestionChip(
        text = text,
        onClick = { onChipClick(text) },
        modifier = Modifier.graphicsLayer {
          alpha = animProgress.value
          translationY = (1f - animProgress.value) * offsetYPx
          scaleX = 0.88f + 0.12f * animProgress.value
          scaleY = 0.88f + 0.12f * animProgress.value
        }
      )
    }
  }
}

@Composable
fun SuggestionChip(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  LiquidGlassSurface(
    shape = CircleShape,
    backgroundColor = Color.White.copy(alpha = 0.45f),
    blurRadius = 16.dp,
    borderColor = Color.White.copy(alpha = 0.60f),
    borderWidth = 1.dp,
    specularAlpha = 0.15f,
    shadowElevation = 6.dp,
    shadowColor = Color.Black.copy(alpha = 0.08f),
    ambientColor = Color.Black.copy(alpha = 0.04f),
    modifier = modifier
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true),
        onClick = onClick
      )
      .testTag("chip_${text.lowercase().replace(" ", "_")}")
  ) {
    Text(
      text = text,
      fontFamily = FontFamily.SansSerif,
      fontSize = 11.5.sp,
      fontWeight = FontWeight.Medium,
      color = TextChip,
      maxLines = 1,
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
    )
  }
}

/**
 * [FOOTER TEXT]
 * - Directly below the chips, center-aligned.
 * - Two lines in very small Sans-serif font, black color:
 *   1. "a Personal Healthcare Companion Robot."
 *   2. "Dedicated to providing diagnosis, treatment and emotional support to optimize patient health."
 */
@Composable
fun FooterTextSection(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "a Personal Healthcare Companion Robot.",
      fontFamily = FontFamily.SansSerif,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = TextFooter,
      letterSpacing = 0.1.sp,
      textAlign = TextAlign.Center,
      modifier = Modifier.testTag("footer_line_1")
    )
    Spacer(modifier = Modifier.height(2.5.dp))
    Text(
      text = "Dedicated to providing diagnosis, treatment and emotional support to optimize patient health.",
      fontFamily = FontFamily.SansSerif,
      fontSize = 8.8.sp,
      fontWeight = FontWeight.Medium,
      color = TextFooterSecondary,
      lineHeight = 12.5.sp,
      textAlign = TextAlign.Center,
      modifier = Modifier.testTag("footer_line_2")
    )
  }
}
