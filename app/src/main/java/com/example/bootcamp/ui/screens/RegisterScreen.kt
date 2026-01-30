package com.example.bootcamp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.R
import com.example.bootcamp.ui.components.AuthBackground
import com.example.bootcamp.ui.theme.Emerald500
import com.example.bootcamp.ui.theme.MutedGray
import com.example.bootcamp.ui.theme.Red500
import com.example.bootcamp.ui.theme.SpaceIndigo
import com.example.bootcamp.ui.theme.SpaceViolet
import com.example.bootcamp.ui.viewmodel.AuthViewModel

@Suppress("UNUSED_PARAMETER")
@Composable
fun RegisterScreen(viewModel: AuthViewModel, onNavigateToLogin: () -> Unit, onRegisterSuccess: () -> Unit,) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Validation states
    val usernameError = username.isNotBlank() && (username.length < 3 || username.length > 50)
    val emailError =
        email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordError = password.isNotBlank() && password.length < 8
    val confirmPasswordError = confirmPassword.isNotBlank() && password != confirmPassword

    val isFormValid =
        username.length in 3..50 &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            password.length >= 8 &&
            password == confirmPassword

    AuthBackground { glowAlpha ->
        Column(
            modifier =
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Back Button
            IconButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.align(Alignment.Start),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logo/Title
            Text(
                text = stringResource(R.string.app_name_star),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 8.sp,
                modifier = Modifier.graphicsLayer { shadowElevation = 40f * glowAlpha }
            )
            Text(
                text = stringResource(R.string.app_name_financial),
                fontSize = 20.sp,
                color = MutedGray,
                letterSpacing = 4.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Register Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.create_account),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = stringResource(R.string.join_star_financial_today),
                            fontSize = 14.sp,
                            color = MutedGray,
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Username Field
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.username), color = MutedGray) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MutedGray,
                                )
                            },
                            isError = usernameError,
                            supportingText =
                            if (usernameError) {
                                {
                                    Text(
                                        stringResource(R.string.username_must_be_3_50_characters),
                                        color = Red500
                                    )
                                }
                            } else {
                                null
                            },
                            keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpaceIndigo,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = SpaceIndigo,
                                focusedLabelColor = SpaceIndigo,
                                unfocusedLabelColor = MutedGray,
                                errorBorderColor = Red500,
                                errorLabelColor = Red500
                            ),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.email), color = MutedGray) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = MutedGray,
                                )
                            },
                            isError = emailError,
                            supportingText =
                            if (emailError) {
                                {
                                    Text(
                                        stringResource(R.string.please_enter_a_valid_email),
                                        color = Red500
                                    )
                                }
                            } else {
                                null
                            },
                            keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpaceIndigo,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = SpaceIndigo,
                                focusedLabelColor = SpaceIndigo,
                                unfocusedLabelColor = MutedGray,
                                errorBorderColor = Red500,
                                errorLabelColor = Red500
                            ),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.password), color = MutedGray) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
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
                                        if (passwordVisible) {
                                            Icons.Default
                                                .Visibility
                                        } else {
                                            Icons.Default
                                                .VisibilityOff
                                        },
                                        contentDescription =
                                        if (passwordVisible) {
                                            stringResource(R.string.hide_password)
                                        } else {
                                            stringResource(R.string.show_password)
                                        },
                                        tint = MutedGray,
                                    )
                                }
                            },
                            isError = passwordError,
                            supportingText =
                            if (passwordError) {
                                {
                                    Text(
                                        stringResource(R.string.password_must_be_at_least_8_characters),
                                        color = Red500
                                    )
                                }
                            } else {
                                null
                            },
                            visualTransformation =
                            if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                KeyboardType.Password,
                                imeAction = ImeAction.Next,
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpaceIndigo,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = SpaceIndigo,
                                focusedLabelColor = SpaceIndigo,
                                unfocusedLabelColor = MutedGray,
                                errorBorderColor = Red500,
                                errorLabelColor = Red500
                            ),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Confirm Password Field
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.confirm_password), color = MutedGray) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MutedGray,
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        confirmPasswordVisible =
                                            !confirmPasswordVisible
                                    }
                                ) {
                                    Icon(
                                        imageVector =
                                        if (confirmPasswordVisible
                                        ) {
                                            Icons.Default
                                                .Visibility
                                        } else {
                                            Icons.Default
                                                .VisibilityOff
                                        },
                                        contentDescription =
                                        if (confirmPasswordVisible
                                        ) {
                                            stringResource(R.string.hide_password)
                                        } else {
                                            stringResource(R.string.show_password)
                                        },
                                        tint = MutedGray,
                                    )
                                }
                            },
                            isError = confirmPasswordError,
                            supportingText =
                            if (confirmPasswordError) {
                                { Text(stringResource(R.string.passwords_do_not_match), color = Red500) }
                            } else {
                                null
                            },
                            visualTransformation =
                            if (confirmPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                KeyboardType.Password,
                                imeAction = ImeAction.Done,
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpaceIndigo,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = SpaceIndigo,
                                focusedLabelColor = SpaceIndigo,
                                unfocusedLabelColor = MutedGray,
                                errorBorderColor = Red500,
                                errorLabelColor = Red500
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

                        // Register Button
                        Button(
                            onClick = {
                                viewModel.clearMessages()
                                viewModel.register(
                                    username,
                                    email,
                                    password
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            enabled = !uiState.isLoading && isFormValid,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            ),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(SpaceIndigo, SpaceViolet)
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                    )
                                } else {
                                    Text(
                                        text = stringResource(R.string.create_account),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Login Link
                        TextButton(onClick = onNavigateToLogin) {
                            Text(
                                text = stringResource(R.string.already_have_an_account),
                                color = MutedGray,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = stringResource(R.string.sign_in),
                                color = SpaceIndigo,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
