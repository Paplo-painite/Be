package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoodRating
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextBlack
import kotlin.math.roundToInt

data class DailyMoodData(
  val dayLabel: String,
  val fullDate: String,
  val score: Float, // 1.0 to 5.0
  val rating: MoodRating,
  val note: String
)

enum class ChartStyle {
  LINE_CURVE,
  BARS
}

@Composable
fun MoodTrendCanvasChart(
  weeklyData: List<DailyMoodData>,
  modifier: Modifier = Modifier,
  initialSelectedIndex: Int = weeklyData.lastIndex.coerceAtLeast(0)
) {
  var selectedIndex by remember { mutableIntStateOf(initialSelectedIndex) }
  var chartStyle by remember { mutableStateOf(ChartStyle.LINE_CURVE) }

  val safeIndex = selectedIndex.coerceIn(0, (weeklyData.size - 1).coerceAtLeast(0))
  val selectedDay = weeklyData.getOrNull(safeIndex) ?: return

  val averageScore = remember(weeklyData) {
    if (weeklyData.isNotEmpty()) {
      weeklyData.map { it.score }.average().toFloat()
    } else 0f
  }

  val trendDelta = remember(weeklyData) {
    if (weeklyData.size >= 4) {
      val firstHalf = weeklyData.take(weeklyData.size / 2).map { it.score }.average()
      val secondHalf = weeklyData.takeLast(weeklyData.size / 2).map { it.score }.average()
      (secondHalf - firstHalf).toFloat()
    } else 0f
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .background(PureWhite)
      .padding(20.dp)
      .testTag("mood_trend_chart_card")
  ) {
    // Header with Title & Chart Style Switcher
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Weekly Emotional Trend",
          fontFamily = PlayfairFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp,
          color = TextBlack
        )
        Text(
          text = "Past 7 days mood patterns",
          fontSize = 12.sp,
          color = Color(0xFF757575)
        )
      }

      // Toggle between Smooth Curve and Bar Chart
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFF1F1F1))
          .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (chartStyle == ChartStyle.LINE_CURVE) TextBlack else Color.Transparent)
            .clickable { chartStyle = ChartStyle.LINE_CURVE }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("chart_toggle_line")
        ) {
          Icon(
            imageVector = Icons.Default.ShowChart,
            contentDescription = "Line Chart",
            tint = if (chartStyle == ChartStyle.LINE_CURVE) PureWhite else Color.Gray,
            modifier = Modifier.size(16.dp)
          )
        }

        Spacer(modifier = Modifier.width(2.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (chartStyle == ChartStyle.BARS) TextBlack else Color.Transparent)
            .clickable { chartStyle = ChartStyle.BARS }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("chart_toggle_bars")
        ) {
          Icon(
            imageVector = Icons.Default.BarChart,
            contentDescription = "Bar Chart",
            tint = if (chartStyle == ChartStyle.BARS) PureWhite else Color.Gray,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Interactive Selected Day Badge (Liquid Glass inspired Pill)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0xFFF8F9FA))
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = selectedDay.rating.emoji,
            fontSize = 24.sp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = selectedDay.dayLabel,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextBlack
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "•  ${selectedDay.fullDate}",
                fontSize = 12.sp,
                color = Color.Gray
              )
            }
            Text(
              text = "${selectedDay.rating.label} (${selectedDay.score.toInt()}/5) - ${selectedDay.note}",
              fontSize = 12.5.sp,
              color = Color(0xFF4A4A4A)
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(TextBlack)
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "${selectedDay.score.toInt()}/5",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PureWhite
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Custom Canvas Rendering
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .pointerInput(weeklyData) {
          detectTapGestures { offset ->
            val totalPoints = weeklyData.size
            if (totalPoints > 0) {
              val sectionWidth = size.width / totalPoints
              val index = (offset.x / sectionWidth).toInt().coerceIn(0, totalPoints - 1)
              selectedIndex = index
            }
          }
        }
        .testTag("mood_canvas_viewport")
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val totalDays = weeklyData.size
        if (totalDays < 2) return@Canvas

        val topPadding = 16.dp.toPx()
        val bottomPadding = 24.dp.toPx()
        val usableHeight = height - topPadding - bottomPadding
        val horizontalPadding = 20.dp.toPx()
        val usableWidth = width - (horizontalPadding * 2)
        val stepX = usableWidth / (totalDays - 1)

        // Draw dashed horizontal reference gridlines (scores 1, 2, 3, 4, 5)
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 10f), 0f)
        for (scoreLevel in 1..5) {
          val normalized = (scoreLevel - 1f) / 4f
          val y = height - bottomPadding - (normalized * usableHeight)
          drawLine(
            color = Color(0xFFE8E8E8),
            start = Offset(horizontalPadding, y),
            end = Offset(width - horizontalPadding, y),
            strokeWidth = 1.dp.toPx(),
            pathEffect = dashEffect
          )
        }

        // Compute coordinate points for each day
        val points = weeklyData.mapIndexed { index, data ->
          val x = horizontalPadding + (index * stepX)
          val normalizedScore = ((data.score - 1f) / 4f).coerceIn(0f, 1f)
          val y = height - bottomPadding - (normalizedScore * usableHeight)
          Offset(x, y)
        }

        if (chartStyle == ChartStyle.LINE_CURVE) {
          // 1. Build smooth Cubic Bezier Path
          val linePath = Path()
          linePath.moveTo(points.first().x, points.first().y)

          for (i in 0 until points.size - 1) {
            val p0 = points[i]
            val p1 = points[i + 1]
            val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
            val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)
            linePath.cubicTo(
              controlPoint1.x, controlPoint1.y,
              controlPoint2.x, controlPoint2.y,
              p1.x, p1.y
            )
          }

          // 2. Area gradient fill under the curve
          val fillPath = Path()
          fillPath.addPath(linePath)
          fillPath.lineTo(points.last().x, height - bottomPadding)
          fillPath.lineTo(points.first().x, height - bottomPadding)
          fillPath.close()

          drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.14f),
                Color.Black.copy(alpha = 0.04f),
                Color.Transparent
              ),
              startY = topPadding,
              endY = height - bottomPadding
            )
          )

          // 3. Draw smooth curve stroke
          drawPath(
            path = linePath,
            color = TextBlack,
            style = Stroke(
              width = 3.dp.toPx(),
              cap = StrokeCap.Round,
              join = StrokeJoin.Round
            )
          )

          // 4. Draw data nodes
          points.forEachIndexed { index, pt ->
            val isSelected = index == safeIndex
            if (isSelected) {
              // Highlighted vertical indicator line
              drawLine(
                color = Color.Black.copy(alpha = 0.18f),
                start = Offset(pt.x, topPadding),
                end = Offset(pt.x, height - bottomPadding),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dashEffect
              )

              // Glow ring
              drawCircle(
                color = Color.Black.copy(alpha = 0.15f),
                radius = 12.dp.toPx(),
                center = pt
              )
              // Outer white circle
              drawCircle(
                color = PureWhite,
                radius = 7.dp.toPx(),
                center = pt
              )
              // Center black core
              drawCircle(
                color = TextBlack,
                radius = 4.5.dp.toPx(),
                center = pt
              )
            } else {
              // Standard point
              drawCircle(
                color = PureWhite,
                radius = 5.dp.toPx(),
                center = pt
              )
              drawCircle(
                color = Color(0xFF9E9E9E),
                radius = 3.dp.toPx(),
                center = pt
              )
            }
          }
        } else {
          // BARS STYLE
          val barWidth = 18.dp.toPx()
          val cornerRadius = 6.dp.toPx()

          points.forEachIndexed { index, pt ->
            val isSelected = index == safeIndex
            val barHeight = (height - bottomPadding) - pt.y
            val barTopLeft = Offset(pt.x - (barWidth / 2f), pt.y)

            val barBrush = if (isSelected) {
              Brush.verticalGradient(
                colors = listOf(TextBlack, Color(0xFF424242))
              )
            } else {
              Brush.verticalGradient(
                colors = listOf(Color(0xFFBDBDBD), Color(0xFFE0E0E0))
              )
            }

            drawRoundRect(
              brush = barBrush,
              topLeft = barTopLeft,
              size = Size(barWidth, barHeight),
              cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
            )

            // Top highlight dot
            drawCircle(
              color = if (isSelected) PureWhite else Color(0xFF757575),
              radius = 2.5.dp.toPx(),
              center = Offset(pt.x, pt.y + 5.dp.toPx())
            )
          }
        }
      }
    }

    // Day Labels under canvas (clickable)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      weeklyData.forEachIndexed { index, data ->
        val isSelected = index == safeIndex
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { selectedIndex = index }
            .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
          Text(
            text = data.dayLabel,
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) TextBlack else Color(0xFF8E8E8E)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Box(
            modifier = Modifier
              .size(4.dp)
              .clip(CircleShape)
              .background(if (isSelected) TextBlack else Color.Transparent)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Trend Analysis & Health Summary Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFFF8F9FA))
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Weekly Average
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "Weekly Average",
          fontSize = 11.sp,
          color = Color.Gray,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = String.format(java.util.Locale.US, "%.1f", averageScore),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
          )
          Text(
            text = " / 5.0",
            fontSize = 11.5.sp,
            color = Color.Gray
          )
        }
      }

      Box(
        modifier = Modifier
          .width(1.dp)
          .height(30.dp)
          .background(Color(0xFFE0E0E0))
      )

      // 2. Emotional Trajectory / Trend
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "Emotional Trend",
          fontSize = 11.sp,
          color = Color.Gray,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          val (trendIcon, trendText, trendColor) = when {
            trendDelta > 0.3f -> Triple(Icons.AutoMirrored.Filled.TrendingUp, "Trending Up", Color(0xFF2E7D32))
            trendDelta < -0.3f -> Triple(Icons.Default.TrendingDown, "Slight Dip", Color(0xFFC62828))
            else -> Triple(Icons.Default.TrendingFlat, "Steady & Calm", Color(0xFF1565C0))
          }
          Icon(
            imageVector = trendIcon,
            contentDescription = null,
            tint = trendColor,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = trendText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = trendColor
          )
        }
      }

      Box(
        modifier = Modifier
          .width(1.dp)
          .height(30.dp)
          .background(Color(0xFFE0E0E0))
      )

      // 3. Positive Days Ratio
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "Positive Days",
          fontSize = 11.sp,
          color = Color.Gray,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(3.dp))
        val positiveCount = weeklyData.count { it.score >= 3.5f }
        val pct = ((positiveCount.toFloat() / weeklyData.size.coerceAtLeast(1)) * 100).roundToInt()
        Text(
          text = "$pct%",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextBlack
        )
      }
    }
  }
}
