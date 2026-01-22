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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.ui.theme.Emerald500
import com.example.bootcamp.ui.theme.Gray400
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Gray700
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.theme.Indigo700
import com.example.bootcamp.ui.theme.Red500
import com.example.bootcamp.ui.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
        viewModel: AuthViewModel,
        onNavigateToLogin: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    val emailError =
            email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

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
                modifier =
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
        ) {
            // Back Button
            IconButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.align(Alignment.Start),
            ) {
                Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logo/Title
            Text(
                    text = "STAR",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
            )
            Text(
                    text = "Financial",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f),
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Forgot Password Card
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                            text = "Forgot Password?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray700,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                            text =
                                    "Enter your email address and we'll send you a link to reset your password.",
                            fontSize = 14.sp,
                            color = Gray500,
                            textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email Field
                    OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Gray400,
                                )
                            },
                            isError = emailError,
                            supportingText =
                                    if (emailError) {
                                        { Text("Please enter a valid email") }
                                    } else null,
                            keyboardOptions =
                                    KeyboardOptions(
                                            keyboardType = KeyboardType.Email,
                                            imeAction = ImeAction.Done,
                                    ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors =
                                    OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Indigo600,
                                            unfocusedBorderColor = Gray400,
                                            errorBorderColor = Red500,
                                    ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Message
                    if (uiState.errorMessage != null) {
                        Text(
                                text = uiState.errorMessage!!,
                                color = Red500,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp),
                        )
                    }

                    // Success Message
                    if (uiState.successMessage != null) {
                        Text(
                                text = uiState.successMessage!!,
                                color = Emerald500,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp),
                        )
                    }

                    // Submit Button
                    Button(
                            onClick = {
                                viewModel.clearMessages()
                                viewModel.forgotPassword(email)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled = !uiState.isLoading && isFormValid,
                            shape = RoundedCornerShape(12.dp),
                            colors =
                                    ButtonDefaults.buttonColors(
                                            containerColor = Indigo600,
                                            disabledContainerColor = Gray400,
                                    ),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                    text = "Send Reset Link",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back to Login Link
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                                text = "Remember your password? ",
                                color = Gray500,
                                fontSize = 14.sp,
                        )
                        Text(
                                text = "Sign In",
                                color = Indigo600,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
