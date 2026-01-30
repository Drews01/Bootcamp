package com.example.bootcamp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.R
import com.example.bootcamp.ui.components.LanguageSettingsSection
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.viewmodel.AuthViewModel

@Composable
fun UserProfileScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileDetails: () -> Unit,
    onNavigateToLoanHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutWarning by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier =
        modifier
            .fillMaxSize()
            .background(
                brush =
                Brush.verticalGradient(
                    colors =
                    listOf(
                        Color(0xFF0F1020),
                        Color(0xFF111827),
                    )
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors =
                CardDefaults.cardColors(
                    containerColor = Color(0xFF020617).copy(alpha = 0.95f),
                ),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier =
                        Modifier.clip(CircleShape)
                            .background(Color(0xFF111827))
                            .padding(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color(0xFFA5B4FC),
                            modifier = Modifier.height(72.dp),
                        )
                    }

                    if (!uiState.isLoggedIn) {
                        Text(
                            text = stringResource(R.string.profile_not_logged_in),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                        Text(
                            text = stringResource(R.string.profile_login_prompt),
                            fontSize = 14.sp,
                            color = Gray500,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNavigateToLogin,
                            modifier =
                            Modifier.fillMaxWidth()
                                .height(48.dp),
                            colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Indigo600,
                            ),
                        ) {
                            Icon(
                                imageVector =
                                Icons.AutoMirrored.Filled
                                    .Login,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.profile_login_button),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.profile_your_profile),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )

                        Text(
                            text = uiState.username ?: "-",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA5B4FC),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Menu Cards
                        MenuCard(
                            title = stringResource(R.string.menu_profile_details),
                            icon = Icons.Default.AccountCircle,
                            onClick = onNavigateToProfileDetails
                        )

                        MenuCard(
                            title = stringResource(R.string.menu_loan_history),
                            icon = Icons.Default.History,
                            onClick = onNavigateToLoanHistory
                        )

                        // Settings Section - Language
//                        Spacer(modifier = Modifier.height(16.dp))
//                        LanguageSettingsSection()

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (uiState.hasPendingData) {
                                    showLogoutWarning = true
                                } else {
                                    viewModel.logout()
                                }
                            },
                            modifier =
                            Modifier.fillMaxWidth()
                                .height(48.dp),
                            colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.Red.copy(alpha = 0.8f),
                            ),
                        ) {
                            Icon(
                                imageVector =
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.profile_logout_button),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }

        if (showLogoutWarning) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showLogoutWarning = false },
                title = { Text(text = stringResource(R.string.dialog_unsaved_changes_title)) },
                text = { Text(text = stringResource(R.string.dialog_unsaved_changes_message)) },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            showLogoutWarning = false
                            viewModel.logout()
                        }
                    ) {
                        Text(stringResource(R.string.dialog_logout_delete), color = Color.Red)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showLogoutWarning = false }) {
                        Text(stringResource(R.string.dialog_cancel))
                    }
                },
                containerColor = Color(0xFF1E293B),
                titleContentColor = Color.White,
                textContentColor = Gray500
            )
        }
    }
}

@Composable
fun MenuCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFA5B4FC)
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Gray500,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
