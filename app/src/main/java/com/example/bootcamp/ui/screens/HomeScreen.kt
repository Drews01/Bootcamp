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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.ui.theme.Gray400
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Gray700
import com.example.bootcamp.ui.theme.Indigo100
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.theme.Indigo700
import com.example.bootcamp.ui.theme.Red500
import com.example.bootcamp.ui.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
        viewModel: AuthViewModel,
        onNavigateToLogin: () -> Unit,
) {
        val uiState by viewModel.uiState.collectAsState()

        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .background(
                                        brush =
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        Indigo600,
                                                                        Indigo700,
                                                                )
                                                )
                                ),
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                ) {
                        // Logo
                        Text(
                                text = "STAR",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                        )
                        Text(
                                text = "Financial",
                                fontSize = 24.sp,
                                color = Color.White.copy(alpha = 0.8f),
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        // Welcome Card
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        ) {
                                Column(
                                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                        if (uiState.isLoggedIn) {
                                                // Logged In State
                                                Box(
                                                        modifier =
                                                                Modifier.size(80.dp)
                                                                        .clip(CircleShape)
                                                                        .background(Indigo100),
                                                        contentAlignment = Alignment.Center,
                                                ) {
                                                        Icon(
                                                                imageVector =
                                                                        Icons.Default.AccountCircle,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(60.dp),
                                                                tint = Indigo600,
                                                        )
                                                }

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Text(
                                                        text = "Welcome back!",
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Gray700,
                                                )

                                                if (uiState.username != null) {
                                                        Text(
                                                                text = uiState.username!!,
                                                                fontSize = 16.sp,
                                                                color = Indigo600,
                                                                fontWeight = FontWeight.SemiBold,
                                                        )
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                        text =
                                                                "You are successfully logged in to STAR Financial.",
                                                        fontSize = 14.sp,
                                                        color = Gray500,
                                                        textAlign = TextAlign.Center,
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

                                                // Logout Button
                                                Button(
                                                        onClick = { viewModel.logout() },
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(50.dp),
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor = Red500
                                                                ),
                                                ) {
                                                        Icon(
                                                                imageVector =
                                                                        Icons.AutoMirrored.Filled
                                                                                .Logout,
                                                                contentDescription = null,
                                                                modifier =
                                                                        Modifier.padding(
                                                                                end = 8.dp
                                                                        ),
                                                        )
                                                        Text(
                                                                text = "Logout",
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                        )
                                                }
                                        } else {
                                                // Not Logged In State
                                                Icon(
                                                        imageVector = Icons.Default.AccountCircle,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(80.dp),
                                                        tint = Gray400,
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Text(
                                                        text = "Welcome to STAR Financial",
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Gray700,
                                                        textAlign = TextAlign.Center,
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                        text =
                                                                "Your trusted partner for financial solutions. Sign in to access your account.",
                                                        fontSize = 14.sp,
                                                        color = Gray500,
                                                        textAlign = TextAlign.Center,
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

                                                // Login Button
                                                Button(
                                                        onClick = onNavigateToLogin,
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(50.dp),
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor = Indigo600
                                                                ),
                                                ) {
                                                        Icon(
                                                                imageVector =
                                                                        Icons.AutoMirrored.Filled
                                                                                .Login,
                                                                contentDescription = null,
                                                                modifier =
                                                                        Modifier.padding(
                                                                                end = 8.dp
                                                                        ),
                                                        )
                                                        Text(
                                                                text = "Sign In",
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                        )
                                                }
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Footer
                        Text(
                                text = "© 2026 STAR Financial. All rights reserved.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f),
                        )
                }
        }
}
