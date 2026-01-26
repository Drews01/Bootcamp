package com.example.bootcamp.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.ui.components.AuthBackground
import com.example.bootcamp.ui.theme.DeepSpace1
import com.example.bootcamp.ui.theme.DeepSpace2
import com.example.bootcamp.ui.theme.DeepSpace3
import com.example.bootcamp.ui.theme.Emerald500
import com.example.bootcamp.ui.theme.MutedGray
import com.example.bootcamp.ui.theme.Red500
import com.example.bootcamp.ui.theme.SpaceIndigo
import com.example.bootcamp.ui.theme.SpacePink
import com.example.bootcamp.ui.theme.SpacePurple
import com.example.bootcamp.ui.theme.SpaceViolet
import com.example.bootcamp.ui.viewmodel.AuthViewModel
import kotlin.random.Random

@Composable
fun LoginScreen(
        viewModel: AuthViewModel,
        onNavigateToRegister: () -> Unit,
        onNavigateToForgotPassword: () -> Unit,
        onLoginSuccess: () -> Unit,
) {
        val uiState by viewModel.uiState.collectAsState()

        var usernameOrEmail by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }


        LaunchedEffect(uiState.isLoggedIn) {
                if (uiState.isLoggedIn) {
                        onLoginSuccess()
                }
        }

        AuthBackground { glowAlpha ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                ) {
                        // Logo/Title with glow effect
                        Text(
                                text = "STAR",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 8.sp,
                                modifier =
                                        Modifier.graphicsLayer { shadowElevation = 40f * glowAlpha }
                        )
                        Text(
                                text = "Financial",
                                fontSize = 20.sp,
                                color = MutedGray,
                                letterSpacing = 4.sp,
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        // Login Card - Glassmorphism effect
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor = Color.White.copy(alpha = 0.1f)
                                        ),
                        ) {
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .background(
                                                                brush =
                                                                        Brush.verticalGradient(
                                                                                colors =
                                                                                        listOf(
                                                                                                Color.White
                                                                                                        .copy(
                                                                                                                alpha =
                                                                                                                        0.15f
                                                                                                        ),
                                                                                                Color.White
                                                                                                        .copy(
                                                                                                                alpha =
                                                                                                                        0.05f
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                ) {
                                        Column(
                                                modifier = Modifier.fillMaxWidth().padding(28.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                                Text(
                                                        text = "Welcome Back",
                                                        fontSize = 28.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        letterSpacing = 1.sp,
                                                )
                                                Text(
                                                        text = "Sign in to continue",
                                                        fontSize = 14.sp,
                                                        color = MutedGray,
                                                )

                                                Spacer(modifier = Modifier.height(28.dp))

                                                // Username/Email Field
                                                OutlinedTextField(
                                                        value = usernameOrEmail,
                                                        onValueChange = { usernameOrEmail = it },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        label = {
                                                                Text(
                                                                        "Email or Username",
                                                                        color = MutedGray
                                                                )
                                                        },
                                                        leadingIcon = {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default.Email,
                                                                        contentDescription = null,
                                                                        tint = MutedGray,
                                                                )
                                                        },
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Email,
                                                                        imeAction = ImeAction.Next,
                                                                ),
                                                        singleLine = true,
                                                        shape = RoundedCornerShape(14.dp),
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                SpaceIndigo,
                                                                        unfocusedBorderColor =
                                                                                Color.White.copy(
                                                                                        alpha = 0.3f
                                                                                ),
                                                                        focusedTextColor =
                                                                                Color.White,
                                                                        unfocusedTextColor =
                                                                                Color.White,
                                                                        cursorColor = SpaceIndigo,
                                                                        focusedLabelColor =
                                                                                SpaceIndigo,
                                                                        unfocusedLabelColor =
                                                                                MutedGray,
                                                                ),
                                                )

                                                Spacer(modifier = Modifier.height(18.dp))

                                                // Password Field
                                                OutlinedTextField(
                                                        value = password,
                                                        onValueChange = { password = it },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        label = {
                                                                Text("Password", color = MutedGray)
                                                        },
                                                        leadingIcon = {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default.Lock,
                                                                        contentDescription = null,
                                                                        tint = MutedGray,
                                                                )
                                                        },
                                                        trailingIcon = {
                                                                IconButton(
                                                                        onClick = {
                                                                                passwordVisible =
                                                                                        !passwordVisible
                                                                        }
                                                                ) {
                                                                        Icon(
                                                                                imageVector =
                                                                                        if (passwordVisible
                                                                                        )
                                                                                                Icons.Default
                                                                                                        .Visibility
                                                                                        else
                                                                                                Icons.Default
                                                                                                        .VisibilityOff,
                                                                                contentDescription =
                                                                                        if (passwordVisible
                                                                                        )
                                                                                                "Hide password"
                                                                                        else
                                                                                                "Show password",
                                                                                tint = MutedGray,
                                                                        )
                                                                }
                                                        },
                                                        visualTransformation =
                                                                if (passwordVisible)
                                                                        VisualTransformation.None
                                                                else PasswordVisualTransformation(),
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType
                                                                                        .Password,
                                                                        imeAction = ImeAction.Done,
                                                                ),
                                                        singleLine = true,
                                                        shape = RoundedCornerShape(14.dp),
                                                        colors =
                                                                OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor =
                                                                                SpaceIndigo,
                                                                        unfocusedBorderColor =
                                                                                Color.White.copy(
                                                                                        alpha = 0.3f
                                                                                ),
                                                                        focusedTextColor =
                                                                                Color.White,
                                                                        unfocusedTextColor =
                                                                                Color.White,
                                                                        cursorColor = SpaceIndigo,
                                                                        focusedLabelColor =
                                                                                SpaceIndigo,
                                                                        unfocusedLabelColor =
                                                                                MutedGray,
                                                                ),
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                // Forgot Password Link
                                                TextButton(
                                                        onClick = onNavigateToForgotPassword,
                                                        modifier = Modifier.align(Alignment.End),
                                                ) {
                                                        Text(
                                                                text = "Forgot Password?",
                                                                color = SpaceViolet,
                                                                fontSize = 14.sp,
                                                        )
                                                }

                                                Spacer(modifier = Modifier.height(16.dp))

                                                // Error Message
                                                if (uiState.errorMessage != null) {
                                                        Text(
                                                                text = uiState.errorMessage!!,
                                                                color = Red500,
                                                                fontSize = 14.sp,
                                                                textAlign = TextAlign.Center,
                                                                modifier =
                                                                        Modifier.padding(
                                                                                bottom = 16.dp
                                                                        ),
                                                        )
                                                }

                                                // Success Message
                                                if (uiState.successMessage != null) {
                                                        Text(
                                                                text = uiState.successMessage!!,
                                                                color = Emerald500,
                                                                fontSize = 14.sp,
                                                                textAlign = TextAlign.Center,
                                                                modifier =
                                                                        Modifier.padding(
                                                                                bottom = 16.dp
                                                                        ),
                                                        )
                                                }

                                                // Login Button - Purple gradient with glow
                                                Button(
                                                        onClick = {
                                                                viewModel.clearMessages()
                                                                viewModel.login(
                                                                        usernameOrEmail,
                                                                        password
                                                                )
                                                        },
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(54.dp),
                                                        enabled =
                                                                !uiState.isLoading &&
                                                                        usernameOrEmail
                                                                                .isNotBlank() &&
                                                                        password.isNotBlank(),
                                                        shape = RoundedCornerShape(14.dp),
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                Color.Transparent,
                                                                        disabledContainerColor =
                                                                                Color.Gray.copy(
                                                                                        alpha = 0.3f
                                                                                ),
                                                                ),
                                                ) {
                                                        Box(
                                                                modifier =
                                                                        Modifier.fillMaxSize()
                                                                                .background(
                                                                                        brush =
                                                                                                Brush.horizontalGradient(
                                                                                                        colors =
                                                                                                                listOf(
                                                                                                                        SpaceIndigo,
                                                                                                                        SpaceViolet
                                                                                                                )
                                                                                                ),
                                                                                        shape =
                                                                                                RoundedCornerShape(
                                                                                                        14.dp
                                                                                                )
                                                                                ),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                if (uiState.isLoading) {
                                                                        CircularProgressIndicator(
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                24.dp
                                                                                        ),
                                                                                color = Color.White,
                                                                                strokeWidth = 2.dp,
                                                                        )
                                                                } else {
                                                                        Text(
                                                                                text = "Sign In",
                                                                                fontSize = 16.sp,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .SemiBold,
                                                                                color = Color.White,
                                                                        )
                                                                }
                                                        }
                                                }

                                                Spacer(modifier = Modifier.height(20.dp))

                                                // Register Link
                                                TextButton(onClick = onNavigateToRegister) {
                                                        Text(
                                                                text = "Don't have an account? ",
                                                                color = MutedGray,
                                                                fontSize = 14.sp,
                                                        )
                                                        Text(
                                                                text = "Sign Up",
                                                                color = SpaceViolet,
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                        )
                                                }

                                                Spacer(modifier = Modifier.height(16.dp))

                                                // Guest Mode Link
                                                TextButton(
                                                    onClick = {
                                                        viewModel.clearMessages()
                                                        onLoginSuccess()
                                                    },
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = "Continue as Guest",
                                                        color = Color.White.copy(alpha = 0.7f),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Normal
                                                    )
                                                }
                                        }
                                }
                        }
                }
        }
}
