package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AdGeneratorHelper
import com.example.data.api.AdGenerationResult
import com.example.data.database.AdEntity
import com.example.data.database.AppDatabase
import com.example.data.repository.AdRepository
import com.example.ui.theme.AdDesignTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface GenerationUiState {
    object Idle : GenerationUiState
    object Loading : GenerationUiState
    data class Success(val result: AdGenerationResult) : GenerationUiState
    data class Error(val message: String) : GenerationUiState
}

class AdViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "AdViewModel"
    private val repository: AdRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AdRepository(database.adDao())
    }

    // Saved Ads Flow from Room
    val savedAds: StateFlow<List<AdEntity>> = repository.allAds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current generation state
    private val _generationState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    val generationState: StateFlow<GenerationUiState> = _generationState.asStateFlow()

    // Active design draft values
    val brandName = MutableStateFlow("VOLT.AI")
    val productDetails = MutableStateFlow("Unlock your creative potential with the world's most advanced AI workspace.")
    val selectedObjective = MutableStateFlow("Brand Awareness")
    val selectedTheme = MutableStateFlow("Bold Typography")

    // Theme preset selection
    val primaryColorHex = MutableStateFlow("#6750A4")
    val secondaryColorHex = MutableStateFlow("#EADDFF")
    val textColorHex = MutableStateFlow("#21005D")
    val backgroundColorHex = MutableStateFlow("#FEF7FF")

    // Generated Copy Values
    val headline = MutableStateFlow("WORK WITHOUT LIMITS.")
    val description = MutableStateFlow("Unlock your creative potential with the world's most advanced AI workspace.")
    val ctaText = MutableStateFlow("Get Started Free")

    // Warning / Status flags
    val showApiKeyWarning = MutableStateFlow(!AdGeneratorHelper.isApiKeyConfigured())

    // Currently selected ad for details / full screen view
    private val _selectedAd = MutableStateFlow<AdEntity?>(null)
    val selectedAd: StateFlow<AdEntity?> = _selectedAd.asStateFlow()

    init {
        // Initialize default colors based on theme
        applyThemeColors("Bold Typography")
    }

    fun selectAd(ad: AdEntity?) {
        _selectedAd.value = ad
    }

    fun applyThemeColors(themeName: String) {
        val paletteId = when (themeName) {
            "Bold Typography" -> "bold_typography"
            "Modern Minimalist" -> "modern_minimalist"
            "Premium Luxury" -> "premium_luxury"
            "Playful & Vibrant" -> "playful_vibrant"
            "High-Contrast Tech" -> "cyber_tech"
            else -> "bold_typography"
        }
        val palette = AdDesignTheme.getPaletteById(paletteId)
        primaryColorHex.value = palette.primaryHex
        secondaryColorHex.value = palette.secondaryHex
        textColorHex.value = palette.textHex
        backgroundColorHex.value = palette.backgroundHex
    }

    fun updateCustomColors(primary: String, secondary: String, text: String, bg: String) {
        primaryColorHex.value = primary
        secondaryColorHex.value = secondary
        textColorHex.value = text
        backgroundColorHex.value = bg
    }

    /**
     * Executes copy generation, handling API calls or falling back.
     */
    fun generateAd() {
        viewModelScope.launch {
            _generationState.value = GenerationUiState.Loading
            try {
                val result = AdGeneratorHelper.generateAdCopy(
                    brandName = brandName.value,
                    productDetails = productDetails.value,
                    objective = selectedObjective.value,
                    theme = selectedTheme.value
                )
                result.fold(
                    onSuccess = { data ->
                        headline.value = data.headline
                        description.value = data.description
                        ctaText.value = data.ctaText
                        _generationState.value = GenerationUiState.Success(data)
                        Log.d(TAG, "Ad generated successfully: $data")
                    },
                    onFailure = { error ->
                        _generationState.value = GenerationUiState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _generationState.value = GenerationUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Saves the current active draft into Room DB
     */
    fun saveCurrentAd() {
        viewModelScope.launch {
            val entity = AdEntity(
                brandName = brandName.value.ifBlank { "Website Ad Maker" },
                productDetails = productDetails.value.ifBlank { "Professional Display Banners" },
                objective = selectedObjective.value,
                theme = selectedTheme.value,
                headline = headline.value,
                description = description.value,
                ctaText = ctaText.value,
                primaryColorHex = primaryColorHex.value,
                secondaryColorHex = secondaryColorHex.value,
                textColorHex = textColorHex.value,
                backgroundColorHex = backgroundColorHex.value
            )
            repository.insertAd(entity)
            Log.d(TAG, "Saved ad entity: $entity")
        }
    }

    /**
     * Deletes a specific saved ad
     */
    fun deleteAd(ad: AdEntity) {
        viewModelScope.launch {
            repository.deleteAd(ad)
            if (_selectedAd.value?.id == ad.id) {
                _selectedAd.value = null
            }
        }
    }

    /**
     * Load a saved ad back as the current active draft
     */
    fun loadAdIntoDraft(ad: AdEntity) {
        brandName.value = ad.brandName
        productDetails.value = ad.productDetails
        selectedObjective.value = ad.objective
        selectedTheme.value = ad.theme
        headline.value = ad.headline
        description.value = ad.description
        ctaText.value = ad.ctaText
        primaryColorHex.value = ad.primaryColorHex
        secondaryColorHex.value = ad.secondaryColorHex
        textColorHex.value = ad.textColorHex
        backgroundColorHex.value = ad.backgroundColorHex
        _generationState.value = GenerationUiState.Idle
    }
}
