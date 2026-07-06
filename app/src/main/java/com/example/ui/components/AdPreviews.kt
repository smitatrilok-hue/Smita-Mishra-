package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Parse custom hex colors safely
fun safeColor(hex: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        fallback
    }
}

/**
 * A beautiful scaled-down preview container that draws its content at its exact target size
 * and scales it to fit the device width flawlessly.
 */
@Composable
fun ScaledAdPreview(
    widthDp: Int,
    heightDp: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth
        val targetWidth = widthDp.dp
        
        val scale = if (availableWidth < targetWidth) {
            availableWidth / targetWidth
        } else {
            1f
        }
        
        val scaledHeight = heightDp.dp * scale
        
        Box(
            modifier = Modifier
                .width(availableWidth)
                .height(scaledHeight),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(targetWidth)
                    .height(heightDp.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    )
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    .clip(RoundedCornerShape(4.dp))
            ) {
                content()
            }
        }
    }
}

/**
 * Abstract background decoration generator based on theme
 */
@Composable
fun AdBackgroundDecorations(
    theme: String,
    primaryColor: Color,
    secondaryColor: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        when (theme.lowercase()) {
            "bold typography" -> {
                // Large overlapping soft circle on bottom right
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.45f),
                    center = Offset(width * 1.05f, height * 1.05f),
                    radius = height * 0.5f
                )
                // Outline circle on top left
                drawCircle(
                    color = primaryColor.copy(alpha = 0.25f),
                    center = Offset(width * -0.05f, height * -0.05f),
                    radius = height * 0.35f,
                    style = Stroke(width = 4f)
                )
            }
            "modern minimalist" -> {
                // Diagonal thin rule lines
                drawLine(
                    color = primaryColor.copy(alpha = 0.15f),
                    start = Offset(0f, height * 0.2f),
                    end = Offset(width * 0.8f, height),
                    strokeWidth = 2f
                )
                drawCircle(
                    color = primaryColor.copy(alpha = 0.08f),
                    center = Offset(width * 0.9f, height * 0.2f),
                    radius = height * 0.3f,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
            "premium luxury" -> {
                // Gold wave curves
                val path = Path().apply {
                    moveTo(0f, height * 0.7f)
                    cubicTo(
                        width * 0.25f, height * 0.6f,
                        width * 0.75f, height * 0.9f,
                        width, height * 0.65f
                    )
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.12f), primaryColor.copy(alpha = 0.02f))
                    )
                )
                // Secondary gold thin circle ring
                drawCircle(
                    color = primaryColor.copy(alpha = 0.15f),
                    center = Offset(width * 0.1f, height * 0.1f),
                    radius = height * 0.4f,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
            "playful & vibrant" -> {
                // Energetic overlapping soft blobs
                drawCircle(
                    color = primaryColor.copy(alpha = 0.15f),
                    center = Offset(width * 0.15f, height * 0.75f),
                    radius = height * 0.35f
                )
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.25f),
                    center = Offset(width * 0.85f, height * 0.2f),
                    radius = height * 0.45f
                )
            }
            else -> { // Cyber / Tech
                // Tech grid pattern
                val rows = 8
                val cols = 15
                val cellW = width / cols
                val cellH = height / rows
                for (r in 0..rows) {
                    for (c in 0..cols) {
                        if ((r + c) % 3 == 0) {
                            drawCircle(
                                color = primaryColor.copy(alpha = 0.25f),
                                center = Offset(c * cellW, r * cellH),
                                radius = 2f
                            )
                        }
                    }
                }
                // Cyan modern polygon line
                drawLine(
                    color = primaryColor.copy(alpha = 0.2f),
                    start = Offset(0f, height * 0.5f),
                    end = Offset(width * 0.4f, height * 0.8f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = secondaryColor.copy(alpha = 0.15f),
                    start = Offset(width * 0.4f, height * 0.8f),
                    end = Offset(width, height * 0.2f),
                    strokeWidth = 2f
                )
            }
        }
    }
}

// ==========================================
// 1. LEADERBOARD AD PREVIEW (728 x 90)
// ==========================================
@Composable
fun LeaderboardAd(
    brandName: String,
    headline: String,
    description: String,
    ctaText: String,
    primaryColorHex: String,
    secondaryColorHex: String,
    textColorHex: String,
    backgroundColorHex: String,
    theme: String
) {
    val bgColor = safeColor(backgroundColorHex, Color.White)
    val txtColor = safeColor(textColorHex, Color.DarkGray)
    val priColor = safeColor(primaryColorHex, Color.Blue)
    val secColor = safeColor(secondaryColorHex, Color.Gray)

    Box(
        modifier = Modifier
            .width(728.dp)
            .height(90.dp)
            .background(bgColor)
            .testTag("ad_preview_leaderboard")
    ) {
        AdBackgroundDecorations(theme, priColor, secColor)

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Column: Brand name + Headline
            Column(
                modifier = Modifier
                    .weight(1.8f)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = brandName.uppercase(),
                    color = priColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = headline,
                    color = txtColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Middle Column: Description text
            Column(
                modifier = Modifier
                    .weight(2.2f)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = description,
                    color = txtColor.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right Column: CTA Button
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = priColor),
                shape = if (theme.lowercase() == "bold typography") CircleShape else RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                modifier = Modifier
                    .height(38.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = ctaText,
                    color = if (bgColor == Color.White || theme.contains("Luxury", true)) Color.White else bgColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 2. MEDIUM RECTANGLE AD PREVIEW (300 x 250)
// ==========================================
@Composable
fun MediumRectangleAd(
    brandName: String,
    headline: String,
    description: String,
    ctaText: String,
    primaryColorHex: String,
    secondaryColorHex: String,
    textColorHex: String,
    backgroundColorHex: String,
    theme: String
) {
    val bgColor = safeColor(backgroundColorHex, Color.White)
    val txtColor = safeColor(textColorHex, Color.DarkGray)
    val priColor = safeColor(primaryColorHex, Color.Blue)
    val secColor = safeColor(secondaryColorHex, Color.Gray)

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(250.dp)
            .background(bgColor)
            .padding(16.dp)
            .testTag("ad_preview_rectangle")
    ) {
        AdBackgroundDecorations(theme, priColor, secColor)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Label & Rating Mock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = brandName.uppercase(),
                    color = priColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = priColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Ad",
                        color = txtColor.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Headline + Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = headline,
                    color = txtColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 24.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    color = txtColor.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // CTA Button Button
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = priColor),
                shape = if (theme.lowercase() == "bold typography") CircleShape else RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(
                    text = ctaText,
                    color = if (bgColor == Color.White || theme.contains("Luxury", true)) Color.White else bgColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

// ==========================================
// 3. WIDE SKYSCRAPER AD PREVIEW (160 x 600)
// ==========================================
@Composable
fun SkyscraperAd(
    brandName: String,
    headline: String,
    description: String,
    ctaText: String,
    primaryColorHex: String,
    secondaryColorHex: String,
    textColorHex: String,
    backgroundColorHex: String,
    theme: String
) {
    val bgColor = safeColor(backgroundColorHex, Color.White)
    val txtColor = safeColor(textColorHex, Color.DarkGray)
    val priColor = safeColor(primaryColorHex, Color.Blue)
    val secColor = safeColor(secondaryColorHex, Color.Gray)

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(600.dp)
            .background(bgColor)
            .padding(14.dp)
            .testTag("ad_preview_skyscraper")
    ) {
        AdBackgroundDecorations(theme, priColor, secColor)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top: Brand Name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SPONSORED",
                    color = txtColor.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = brandName.uppercase(),
                    color = priColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = priColor.copy(alpha = 0.2f), thickness = 1.dp)
            }

            // Middle: Punchy Large Headline & Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = headline,
                    color = txtColor,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = description,
                    color = txtColor.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Bottom: Bold CTA Button
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = priColor),
                shape = if (theme.lowercase() == "bold typography") CircleShape else RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    text = ctaText,
                    color = if (bgColor == Color.White || theme.contains("Luxury", true)) Color.White else bgColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

// ==========================================
// 4. MOBILE BANNER AD PREVIEW (320 x 50)
// ==========================================
@Composable
fun MobileBannerAd(
    brandName: String,
    headline: String,
    description: String,
    ctaText: String,
    primaryColorHex: String,
    secondaryColorHex: String,
    textColorHex: String,
    backgroundColorHex: String,
    theme: String
) {
    val bgColor = safeColor(backgroundColorHex, Color.White)
    val txtColor = safeColor(textColorHex, Color.DarkGray)
    val priColor = safeColor(primaryColorHex, Color.Blue)
    val secColor = safeColor(secondaryColorHex, Color.Gray)

    Box(
        modifier = Modifier
            .width(320.dp)
            .height(50.dp)
            .background(bgColor)
            .testTag("ad_preview_mobile_banner")
    ) {
        AdBackgroundDecorations(theme, priColor, secColor)

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Content (Brand Logo Pill + Headline)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Little Brand Initials Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(priColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = brandName.take(2).uppercase(),
                        color = if (bgColor == Color.White || theme.contains("Luxury", true)) Color.White else bgColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = brandName.uppercase(),
                        color = priColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = headline,
                        color = txtColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right Button
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = priColor),
                shape = if (theme.lowercase() == "bold typography") CircleShape else RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                modifier = Modifier
                    .height(28.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = ctaText,
                    color = if (bgColor == Color.White || theme.contains("Luxury", true)) Color.White else bgColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
