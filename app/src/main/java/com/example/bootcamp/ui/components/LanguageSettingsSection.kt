package com.example.bootcamp.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bootcamp.R
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.viewmodel.LanguageViewModel
import com.example.bootcamp.util.Language

/**
 * Utility function to find Activity from Context.
 * Unwraps ContextWrapper chain to find the actual Activity.
 */
private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

/**
 * Language settings section with static two-button selector.
 * Integrated into ProfileDetailsScreen.
 */
@Composable
fun LanguageSettingsSection(viewModel: LanguageViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = remember { context.findActivity() }

    Column(modifier = modifier) {
        // Section Title
        Text(
            text = stringResource(R.string.settings_section_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Language Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header with Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color(0xFFA5B4FC),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.settings_language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.settings_language_subtitle),
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }
                }

                Divider(color = Color(0xFF334155), thickness = 1.dp)

                // Static Language Options - English
                LanguageOptionRow(
                    languageCode = "en",
                    languageName = stringResource(R.string.language_english),
                    isSelected = uiState.currentLanguage == Language.ENGLISH,
                    onClick = {
                        viewModel.changeLanguage(Language.ENGLISH, activity)
                    }
                )

                // Static Language Options - Indonesian
                LanguageOptionRow(
                    languageCode = "id",
                    languageName = stringResource(R.string.language_indonesian),
                    isSelected = uiState.currentLanguage == Language.INDONESIAN,
                    onClick = {
                        viewModel.changeLanguage(Language.INDONESIAN, activity)
                    }
                )
            }
        }
    }
}

@Composable
private fun LanguageOptionRow(languageCode: String, languageName: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Language Flag (using emoji)
            Text(
                text = if (languageCode == "en") "??" else "??",
                fontSize = 20.sp
            )
            Column {
                Text(
                    text = languageName,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) Indigo600 else Color.White
                )
                if (isSelected) {
                    Text(
                        text = stringResource(R.string.language_selected),
                        fontSize = 12.sp,
                        color = Indigo600
                    )
                }
            }
        }

        // Selection Indicator
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.language_selected),
                tint = Indigo600,
                modifier = Modifier.size(24.dp)
            )
        } else {
            // Radio button style for unselected
            RadioButton(
                selected = false,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    unselectedColor = Gray500
                )
            )
        }
    }
}
