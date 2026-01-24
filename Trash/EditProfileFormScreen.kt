package com.example.bootcamp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.viewmodel.EditProfileFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileFormScreen(
        viewModel: EditProfileFormViewModel = hiltViewModel(),
        onNavigateBack: () -> Unit,
        onSubmitSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Image picker launcher
    val imagePickerLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
                    uri: Uri? ->
                viewModel.onImageSelected(uri)
            }

    // Handle success message
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
            onSubmitSuccess()
        }
    }

    // Handle error message
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    // Handle image validation error
    LaunchedEffect(uiState.imageValidationError) {
        uiState.imageValidationError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearImageValidationError()
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
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = Color.Transparent
                                )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .background(
                                        brush =
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        Color(0xFF0F1020),
                                                                        Color(0xFF111827)
                                                                )
                                                )
                                )
                                .padding(paddingValues)
        ) {
            Column(
                    modifier =
                            Modifier.fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Email Card (Read-only)
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Email", fontSize = 12.sp, color = Gray500)
                        Text(
                                text = uiState.email.ifEmpty { "-" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                        )
                    }
                }

                // Form Fields Card
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = Color(0xFF020617).copy(alpha = 0.95f)
                                )
                ) {
                    Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                                text = "Profile Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                        )

                        // Address
                        ProfileTextField(
                                value = uiState.address,
                                onValueChange = { viewModel.onAddressChanged(it) },
                                label = "Address"
                        )

                        // NIK
                        ProfileTextField(
                                value = uiState.nik,
                                onValueChange = { viewModel.onNikChanged(it) },
                                label = "NIK (National ID)",
                                keyboardType = KeyboardType.Number
                        )

                        // Phone Number
                        ProfileTextField(
                                value = uiState.phoneNumber,
                                onValueChange = { viewModel.onPhoneNumberChanged(it) },
                                label = "Phone Number",
                                keyboardType = KeyboardType.Phone
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = "Bank Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                        )

                        // Bank Name
                        ProfileTextField(
                                value = uiState.bankName,
                                onValueChange = { viewModel.onBankNameChanged(it) },
                                label = "Bank Name"
                        )

                        // Account Number
                        ProfileTextField(
                                value = uiState.accountNumber,
                                onValueChange = { viewModel.onAccountNumberChanged(it) },
                                label = "Account Number",
                                keyboardType = KeyboardType.Number
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = "KTP Upload",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                        )

                        Text(
                                text = "Upload your KTP image (JPEG, PNG, or JPG only)",
                                fontSize = 12.sp,
                                color = Gray500
                        )

                        // KTP Upload Section
                        KtpUploadSection(
                                selectedImageUri = uiState.selectedImageUri,
                                ktpPath = uiState.ktpPath,
                                isUploading = uiState.isUploading,
                                onPickImage = { imagePickerLauncher.launch("image/*") }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                                onClick = { viewModel.submitProfile() },
                                enabled = uiState.isSubmitEnabled,
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = Indigo600,
                                                disabledContainerColor =
                                                        Indigo600.copy(alpha = 0.5f)
                                        ),
                                shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isSubmitting) {
                                CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                        text = "Save Profile",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            colors =
                    OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = Gray500,
                            focusedLabelColor = Indigo600,
                            unfocusedLabelColor = Gray500,
                            cursorColor = Color.White
                    )
    )
}

@Composable
private fun KtpUploadSection(
        selectedImageUri: Uri?,
        ktpPath: String,
        isUploading: Boolean,
        onPickImage: () -> Unit
) {
    Box(
            modifier =
                    Modifier.fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B))
                            .border(
                                    width = 2.dp,
                                    color =
                                            if (ktpPath.isNotEmpty()) Color(0xFF22C55E)
                                            else Gray500,
                                    shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = !isUploading) { onPickImage() },
            contentAlignment = Alignment.Center
    ) {
        when {
            isUploading -> {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(color = Indigo600, modifier = Modifier.size(40.dp))
                    Text(text = "Uploading...", color = Gray500, fontSize = 14.sp)
                }
            }
            selectedImageUri != null -> {
                val context = LocalContext.current
                val bitmap = remember(selectedImageUri) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(selectedImageUri)
                        android.graphics.BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }
                
                Box(modifier = Modifier.fillMaxSize()) {
                    if (bitmap != null) {
                        androidx.compose.foundation.Image(
                                bitmap = bitmap,
                                contentDescription = "Selected KTP",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback if bitmap loading fails
                        Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Image Selected",
                                    tint = Color(0xFF22C55E),
                                    modifier = Modifier.size(48.dp)
                            )
                            Text(text = "Image selected", color = Color.White, fontSize = 14.sp)
                        }
                    }
                    if (ktpPath.isNotEmpty()) {
                        Box(
                                modifier =
                                        Modifier.align(Alignment.TopEnd)
                                                .padding(8.dp)
                                                .background(
                                                        Color(0xFF22C55E),
                                                        RoundedCornerShape(50)
                                                )
                                                .padding(4.dp)
                        ) {
                            Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Uploaded",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            else -> {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Upload KTP",
                            tint = Color(0xFFA5B4FC),
                            modifier = Modifier.size(48.dp)
                    )
                    Text(text = "Tap to select KTP image", color = Gray500, fontSize = 14.sp)
                }
            }
        }
    }
}
