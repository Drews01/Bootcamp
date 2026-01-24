package com.example.bootcamp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.viewmodel.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
            onSaveSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F1020), Color(0xFF111827))
                    )
                )
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Indigo600
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Complete your profile to be able to submit loan applications.",
                        color = Gray500,
                        fontSize = 14.sp
                    )

                    // Address
                    OutlinedTextField(
                        value = uiState.address,
                        onValueChange = { viewModel.onAddressChanged(it) },
                        label = { Text("Address *") },
                        leadingIcon = {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Gray500)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = Gray500,
                            focusedLabelColor = Indigo600,
                            unfocusedLabelColor = Gray500,
                            cursorColor = Color.White
                        )
                    )

                    // NIK
                    OutlinedTextField(
                        value = uiState.nik,
                        onValueChange = { viewModel.onNikChanged(it) },
                        label = { Text("NIK (16 digits) *") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Gray500)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = Gray500,
                            focusedLabelColor = Indigo600,
                            unfocusedLabelColor = Gray500,
                            cursorColor = Color.White
                        ),
                        supportingText = {
                            Text("${uiState.nik.length}/16", color = Gray500)
                        }
                    )

                    // Phone Number
                    OutlinedTextField(
                        value = uiState.phoneNumber,
                        onValueChange = { viewModel.onPhoneNumberChanged(it) },
                        label = { Text("Phone Number *") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Gray500)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = Gray500,
                            focusedLabelColor = Indigo600,
                            unfocusedLabelColor = Gray500,
                            cursorColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bank Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Bank Name
                    OutlinedTextField(
                        value = uiState.bankName,
                        onValueChange = { viewModel.onBankNameChanged(it) },
                        label = { Text("Bank Name *") },
                        leadingIcon = {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Gray500)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = Gray500,
                            focusedLabelColor = Indigo600,
                            unfocusedLabelColor = Gray500,
                            cursorColor = Color.White
                        )
                    )

                    // Account Number
                    OutlinedTextField(
                        value = uiState.accountNumber,
                        onValueChange = { viewModel.onAccountNumberChanged(it) },
                        label = { Text("Account Number *") },
                        leadingIcon = {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = Gray500)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = Gray500,
                            focusedLabelColor = Indigo600,
                            unfocusedLabelColor = Gray500,
                            cursorColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Button
                    Button(
                        onClick = { viewModel.saveProfile() },
                        enabled = !uiState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Indigo600,
                            disabledContainerColor = Indigo600.copy(alpha = 0.5f)
                        )
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Save Profile",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = "* Required fields",
                        color = Gray500,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
