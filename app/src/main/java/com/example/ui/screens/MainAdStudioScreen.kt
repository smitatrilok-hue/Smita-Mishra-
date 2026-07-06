package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.database.AdEntity
import com.example.ui.components.*
import com.example.ui.theme.AdDesignTheme
import com.example.ui.viewmodel.AdViewModel
import com.example.ui.viewmodel.GenerationUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAdStudioScreen(
    viewModel: AdViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Screen navigation tab
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Ad Studio", "Saved Gallery")

    // Form states from ViewModel
    val brandName by viewModel.brandName.collectAsState()
    val productDetails by viewModel.productDetails.collectAsState()
    val selectedObjective by viewModel.selectedObjective.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()

    // Generated values
    val headline by viewModel.headline.collectAsState()
    val description by viewModel.description.collectAsState()
    val ctaText by viewModel.ctaText.collectAsState()

    // Colors
    val primaryColorHex by viewModel.primaryColorHex.collectAsState()
    val secondaryColorHex by viewModel.secondaryColorHex.collectAsState()
    val textColorHex by viewModel.textColorHex.collectAsState()
    val backgroundColorHex by viewModel.backgroundColorHex.collectAsState()

    // UI/API state
    val generationState by viewModel.generationState.collectAsState()
    val savedAds by viewModel.savedAds.collectAsState()
    val showWarning by viewModel.showApiKeyWarning.collectAsState()

    // Preview format: 0 = Leaderboard, 1 = Rectangle, 2 = Skyscraper, 3 = Mobile Banner
    var previewFormatIndex by remember { mutableStateOf(1) }
    val formatNames = listOf("Leaderboard", "Rectangle", "Skyscraper", "Mobile")

    // Export Dialog State
    var showExportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VOLT.AI",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            letterSpacing = (-0.5).sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("app_top_bar")
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen switching tab bar
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().testTag("main_navigation_tabs")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title.uppercase(),
                                fontWeight = if (selectedTab == index) FontWeight.ExtraBold else FontWeight.SemiBold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        modifier = Modifier.testTag("tab_$index")
                    )
                }
            }

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "tab_transition",
                modifier = Modifier.weight(1f)
            ) { targetTab ->
                when (targetTab) {
                    0 -> {
                        // AD STUDIO WORKSHOP
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            // Volt.ai active engine badge
                            Row(
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "VOLT V4.0 AD ENGINE ACTIVE",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Gemini Key Missing Callout Banner
                            if (showWarning) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFFFF9E6),
                                        contentColor = Color(0xFF856404)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = Color(0xFF856404),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Local Sandbox Mode",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF856404)
                                            )
                                            Text(
                                                text = "Configure your GEMINI_API_KEY in the Secrets Panel to unlock expert AI copy generation.",
                                                fontSize = 12.sp,
                                                color = Color(0xFF856404).copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }
                            }

                            // --- 1. LIVE PREVIEW BLOCK ---
                            Text(
                                text = "Live Ad Preview",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Previews format toggle selector
                            ScrollableTabRow(
                                selectedTabIndex = previewFormatIndex,
                                edgePadding = 0.dp,
                                containerColor = Color.Transparent,
                                divider = {},
                                indicator = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                formatNames.forEachIndexed { index, name ->
                                    val isSelected = previewFormatIndex == index
                                    Card(
                                        onClick = { previewFormatIndex = index },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .height(36.dp)
                                            .testTag("preview_format_tab_$index")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .padding(horizontal = 14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Live render container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                when (previewFormatIndex) {
                                    0 -> ScaledAdPreview(widthDp = 728, heightDp = 90) {
                                        LeaderboardAd(
                                            brandName = brandName,
                                            headline = headline,
                                            description = description,
                                            ctaText = ctaText,
                                            primaryColorHex = primaryColorHex,
                                            secondaryColorHex = secondaryColorHex,
                                            textColorHex = textColorHex,
                                            backgroundColorHex = backgroundColorHex,
                                            theme = selectedTheme
                                        )
                                    }
                                    1 -> ScaledAdPreview(widthDp = 300, heightDp = 250) {
                                        MediumRectangleAd(
                                            brandName = brandName,
                                            headline = headline,
                                            description = description,
                                            ctaText = ctaText,
                                            primaryColorHex = primaryColorHex,
                                            secondaryColorHex = secondaryColorHex,
                                            textColorHex = textColorHex,
                                            backgroundColorHex = backgroundColorHex,
                                            theme = selectedTheme
                                        )
                                    }
                                    2 -> ScaledAdPreview(widthDp = 160, heightDp = 600) {
                                        SkyscraperAd(
                                            brandName = brandName,
                                            headline = headline,
                                            description = description,
                                            ctaText = ctaText,
                                            primaryColorHex = primaryColorHex,
                                            secondaryColorHex = secondaryColorHex,
                                            textColorHex = textColorHex,
                                            backgroundColorHex = backgroundColorHex,
                                            theme = selectedTheme
                                        )
                                    }
                                    else -> ScaledAdPreview(widthDp = 320, heightDp = 50) {
                                        MobileBannerAd(
                                            brandName = brandName,
                                            headline = headline,
                                            description = description,
                                            ctaText = ctaText,
                                            primaryColorHex = primaryColorHex,
                                            secondaryColorHex = secondaryColorHex,
                                            textColorHex = textColorHex,
                                            backgroundColorHex = backgroundColorHex,
                                            theme = selectedTheme
                                        )
                                    }
                                }
                            }

                            // Ad Copy Quick Action Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                
                                OutlinedButton(
                                    onClick = {
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Headline", headline))
                                        Toast.makeText(context, "Headline copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).padding(end = 4.dp).testTag("copy_headline_button")
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Headline", fontSize = 11.sp, maxLines = 1)
                                }

                                OutlinedButton(
                                    onClick = {
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Description", description))
                                        Toast.makeText(context, "Description copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp).testTag("copy_desc_button")
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Desc", fontSize = 11.sp, maxLines = 1)
                                }

                                Button(
                                    onClick = { showExportDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    modifier = Modifier.weight(1.2f).padding(start = 4.dp).testTag("export_code_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Get Code", fontSize = 11.sp, maxLines = 1)
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(bottom = 24.dp))

                            // --- 2. WORKSHOP BUILDER FORM ---
                            Text(
                                text = "Campaign Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Brand Name Field
                            OutlinedTextField(
                                value = brandName,
                                onValueChange = { viewModel.brandName.value = it },
                                label = { Text("Brand/Company Name") },
                                placeholder = { Text("e.g. EcoClean") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .testTag("input_brand_name")
                            )

                            // Product Details Field
                            OutlinedTextField(
                                value = productDetails,
                                onValueChange = { viewModel.productDetails.value = it },
                                label = { Text("What are you advertising?") },
                                placeholder = { Text("e.g. Natural multi-surface cleaning sprays that smell like fresh pine.") },
                                maxLines = 3,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .testTag("input_product_details")
                            )

                            // Campaign Objective Picker
                            Text(
                                text = "Campaign Objective",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            val objectives = listOf("Brand Awareness", "Product Sale", "Lead Generation")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                objectives.forEach { obj ->
                                    val isSel = selectedObjective == obj
                                    Card(
                                        onClick = { viewModel.selectedObjective.value = obj },
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .testTag("objective_$obj")
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = obj.replace("Brand ", "").replace("Product ", ""),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            // Theme Presets Selector
                            Text(
                                text = "Design Theme Preset",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            val themesList = listOf("Bold Typography", "Modern Minimalist", "Premium Luxury", "Playful & Vibrant", "High-Contrast Tech")
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 20.dp)
                            ) {
                                themesList.forEach { theme ->
                                    val isSel = selectedTheme == theme
                                    val paletteId = when (theme) {
                                        "Bold Typography" -> "bold_typography"
                                        "Modern Minimalist" -> "modern_minimalist"
                                        "Premium Luxury" -> "premium_luxury"
                                        "Playful & Vibrant" -> "playful_vibrant"
                                        "High-Contrast Tech" -> "cyber_tech"
                                        else -> "bold_typography"
                                    }
                                    val palette = AdDesignTheme.getPaletteById(paletteId)

                                    Card(
                                        onClick = {
                                            viewModel.selectedTheme.value = theme
                                            viewModel.applyThemeColors(theme)
                                        },
                                        border = BorderStroke(
                                            width = 2.dp,
                                            color = if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("theme_card_$theme")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Tiny Color Palette Row
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color.LightGray)
                                            ) {
                                                Box(modifier = Modifier.size(16.dp).background(palette.backgroundColor))
                                                Box(modifier = Modifier.size(16.dp).background(palette.primaryColor))
                                                Box(modifier = Modifier.size(16.dp).background(palette.textColor))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = theme,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = palette.description,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // --- 3. GENERATION TRIGGER ---
                            Button(
                                onClick = { viewModel.generateAd() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = CircleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("generate_ad_button")
                            ) {
                                when (generationState) {
                                    is GenerationUiState.Loading -> {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("AI Copywriting Active...", fontWeight = FontWeight.Bold)
                                    }
                                    else -> {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Generate Ad Campaign", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // SAVE DESIGN BUTTON
                            OutlinedButton(
                                onClick = {
                                    viewModel.saveCurrentAd()
                                    Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
                                },
                                shape = CircleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("save_ad_button")
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Draft to Gallery", fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }

                    1 -> {
                        // SAVED GALLERY VIEW
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Saved Campaigns (${savedAds.size})",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            if (savedAds.isEmpty()) {
                                // EMPTY STATE
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .testTag("gallery_empty_state"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FolderOpen,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Your Gallery is Empty",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Designs built in the Ad Studio will appear here for easy editing and code exporting.",
                                            textAlign = TextAlign.Center,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { selectedTab = 0 },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Go Create an Ad")
                                        }
                                    }
                                }
                            } else {
                                // SCROLLABLE LIST OF SAVED ADS
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .testTag("saved_ads_list")
                                ) {
                                    items(savedAds) { ad ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("saved_ad_card_${ad.id}"),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = ad.brandName,
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 15.sp,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Text(
                                                            text = "${ad.objective} • ${ad.theme}",
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }

                                                    Row {
                                                        IconButton(
                                                            onClick = {
                                                                viewModel.loadAdIntoDraft(ad)
                                                                selectedTab = 0
                                                                Toast.makeText(context, "Loaded into Ad Studio!", Toast.LENGTH_SHORT).show()
                                                            },
                                                            modifier = Modifier.testTag("edit_ad_${ad.id}")
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Edit,
                                                                contentDescription = "Edit Ad",
                                                                tint = MaterialTheme.colorScheme.primary
                                                            )
                                                        }

                                                        IconButton(
                                                            onClick = { viewModel.deleteAd(ad) },
                                                            modifier = Modifier.testTag("delete_ad_${ad.id}")
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Delete,
                                                                contentDescription = "Delete Ad",
                                                                tint = MaterialTheme.colorScheme.error
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))
                                                
                                                // Tiny Ad Visual Block Preview
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .height(48.dp)
                                                        .background(safeColor(ad.backgroundColorHex, Color.White)),
                                                    contentAlignment = Alignment.CenterStart
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(horizontal = 12.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = ad.headline,
                                                            color = safeColor(ad.textColorHex, Color.Black),
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(safeColor(ad.primaryColorHex, Color.Blue))
                                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                                        ) {
                                                            Text(
                                                                text = ad.ctaText,
                                                                color = Color.White,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- HTML/CSS EXPORT DIALOG ---
    if (showExportDialog) {
        val simulatedHtml = """
            <!-- Website Display Ad Embed Code -->
            <div class="ad-craft-banner" style="
              width: 300px;
              height: 250px;
              background-color: $backgroundColorHex;
              color: $textColorHex;
              font-family: 'Segoe UI', system-ui, sans-serif;
              padding: 20px;
              box-sizing: border-box;
              border: 1px solid rgba(0,0,0,0.1);
              display: flex;
              flex-direction: column;
              justify-content: space-between;
              position: relative;
              overflow: hidden;
              border-radius: 6px;
            ">
              <div style="font-size: 11px; font-weight: 800; color: $primaryColorHex; letter-spacing: 1px;">
                ${brandName.uppercase().ifBlank { "BRAND" }}
              </div>
              <div style="margin: auto 0; padding: 10px 0;">
                <h1 style="font-size: 20px; font-weight: 900; margin: 0 0 8px 0; line-height: 1.2;">
                  $headline
                </h1>
                <p style="font-size: 12px; margin: 0; line-height: 1.4; opacity: 0.85;">
                  $description
                </p>
              </div>
              <button style="
                background-color: $primaryColorHex;
                color: white;
                border: none;
                padding: 10px;
                font-weight: 700;
                font-size: 13px;
                cursor: pointer;
                border-radius: 4px;
                width: 100%;
                text-transform: uppercase;
                letter-spacing: 0.5px;
              ">
                $ctaText
              </button>
            </div>
        """.trimIndent()

        Dialog(onDismissRequest = { showExportDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("export_dialog_box")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Embed Code (HTML/CSS)",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { showExportDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Copy this HTML code to insert this responsive Medium Rectangle ad directly into your website's sidebar or body container:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Code Viewer Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E1E1E))
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp)
                    ) {
                        Text(
                            text = simulatedHtml,
                            color = Color(0xFFD4D4D4),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        
                        TextButton(
                            onClick = { showExportDialog = false },
                            modifier = Modifier.testTag("close_export_dialog")
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                clipboard.setPrimaryClip(ClipData.newPlainText("Embed HTML", simulatedHtml))
                                Toast.makeText(context, "Embed code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                showExportDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("copy_embed_code_button")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Code")
                        }
                    }
                }
            }
        }
    }
}
