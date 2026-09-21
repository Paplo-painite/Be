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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.data.Sender
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextBlack

@Composable
fun ChatSheet(
  messages: List<ChatMessage>,
  onSendMessage: (String) -> Unit,
  onSpeak: (String) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  var textInput by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val handleSend = {
    if (textInput.isNotBlank()) {
      onSendMessage(textInput.trim())
      textInput = ""
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            PureWhite,
            Color(0xFFF6F6F6),
            Color(0xFFEBEBEB)
          )
        )
      )
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onClose,
        modifier = Modifier.testTag("chat_back_button")
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
          text = "Plus Healthcare Companion",
          fontFamily = PlayfairFontFamily,
          fontSize = 19.sp,
          fontWeight = FontWeight.Medium,
          color = TextBlack
        )
        Text(
          text = "Calm, empathetic medical guidance",
          fontSize = 12.sp,
          fontFamily = FontFamily.SansSerif,
          color = Color(0xFF757575)
        )
      }
    }

    // Chat Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(messages, key = { it.id }) { message ->
        ChatMessageRow(
          message = message,
          onSpeak = { onSpeak(message.text) }
        )
      }
      item {
        Spacer(modifier = Modifier.height(8.dp))
      }
    }

    // Input row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.85f))
          .padding(horizontal = 18.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        if (textInput.isEmpty()) {
          Text(
            text = "Tell Plus how you are feeling...",
            color = Color(0xFFA0A0A0),
            fontSize = 14.5.sp
          )
        }
        BasicTextField(
          value = textInput,
          onValueChange = { textInput = it },
          textStyle = TextStyle(
            color = TextBlack,
            fontSize = 15.sp,
            fontFamily = FontFamily.SansSerif
          ),
          cursorBrush = SolidColor(TextBlack),
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(onSend = { handleSend() }),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_input_field")
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Box(
        modifier = Modifier
          .size(50.dp)
          .clip(CircleShape)
          .background(TextBlack)
          .clickable { handleSend() }
          .testTag("chat_send_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send",
          tint = PureWhite,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun ChatMessageRow(
  message: ChatMessage,
  onSpeak: () -> Unit
) {
  val isUser = message.sender == Sender.USER

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    if (!isUser) {
      // Plus Baymax indicator
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(Color.White),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "●—●",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = TextBlack
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Box(
      modifier = Modifier
        .fillMaxWidth(0.82f)
        .clip(
          RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = if (isUser) 18.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 18.dp
          )
        )
        .background(
          if (isUser) Color(0xFF222222)
          else if (message.isEmergency) Color(0xFFFFECEC)
          else Color.White.copy(alpha = 0.92f)
        )
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Column {
        Text(
          text = message.text,
          color = if (isUser) PureWhite else if (message.isEmergency) Color(0xFFD32F2F) else TextBlack,
          fontSize = 14.5.sp,
          lineHeight = 21.sp,
          fontFamily = FontFamily.SansSerif
        )

        if (!isUser) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = onSpeak,
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Read aloud",
                tint = Color(0xFF888888),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}
