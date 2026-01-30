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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bootcamp.R
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
    val context = androidx.compose.ui.platform.LocalContext.current

    // Temporary URI for Camera capture
    var tempCameraUri by androidx.compose.runtime.saveable.rememberSaveable {
        androidx.compose.runtime.mutableStateOf<String?>(null)
    }

    // Helper to create temp URI
    fun createTempUri(): android.net.Uri {
        val tempFile = java.io.File.createTempFile("ktp_capture_", ".jpg", context.cacheDir).apply {
            createNewFile()
            // deleteOnExit() removed to prevent file loss on process death
        }
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    // Camera Launcher
    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            val uri = android.net.Uri.parse(tempCameraUri)
            viewModel.onKtpFileSelected(uri)
        }
    }

    // Permission Launcher
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createTempUri()
            tempCameraUri = uri.toString()
            cameraLauncher.launch(uri)
        } else {
            // Show permission denied message
            // Ideally scope.launch { snackbarHostState.showSnackbar("Camera permission needed") }
        }
    }

    // Gallery Launcher
    val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { viewModel.onKtpFileSelected(it) }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            // Do not clear immediately if it's "KTP uploaded", maybe just show it
            // checking if it's the save profile success
            if (it.contains("Profile updated")) {
                onSaveSuccess()
            }
            viewModel.clearMessages()
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
                title = { Text(stringResource(R.string.edit_profile), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
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
                        text = stringResource(R.string.edit_profile_description),
                        color = Gray500,
                        fontSize = 14.sp
                    )

                    // --- KTP Photo Section ---
                    Text(
                        text = stringResource(R.string.edit_profile_ktp_label),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageModifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black, RoundedCornerShape(10.dp))

                        // Priority: 1. Selected URI (Preview), 2. Existing Path (Server), 3. Placeholder
                        val displayModel = uiState.ktpUri ?: uiState.ktpPath.takeIf { it.isNotBlank() }

                        if (displayModel != null) {
                            // Assuming ktpPath is a full URL or user understands;
                            // if it is a relative path, we might need to prepend base url in ViewModel.
                            // But let's assume Coil handles paths if local, urls if remote.
                            // For now validation assumed UserProfileDto returns valid URLs or local paths.

                            coil.compose.SubcomposeAsyncImage(
                                model = displayModel,
                                contentDescription = stringResource(R.string.profile_ktp_photo),
                                modifier = imageModifier,
                                // Changed from Fit to Crop for better look, or use Fit to show full document
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                loading = {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = Indigo600)
                                    }
                                },
                                error = {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.Red)
                                        Text(stringResource(R.string.error_load_image), color = Color.Red, fontSize = 12.sp)
                                    }
                                }
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = Gray500,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(stringResource(R.string.profile_no_ktp), color = Gray500)
                            }
                        }

                        // Overlay Loading for upload
                        if (uiState.isUploadingKtp) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val permission = android.Manifest.permission.CAMERA
                                if (androidx.core.content.ContextCompat.checkSelfPermission(context, permission) ==
                                    android.content.pm.PackageManager.PERMISSION_GRANTED
                                ) {
                                    val uri = createTempUri()
                                    tempCameraUri = uri.toString()
                                    cameraLauncher.launch(uri)
                                } else {
                                    permissionLauncher.launch(permission)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.button_camera))
                        }

                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.button_gallery))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Address
                    OutlinedTextField(
                        value = uiState.address,
                        onValueChange = { viewModel.onAddressChanged(it) },
                        label = { Text(stringResource(R.string.edit_profile_address_label)) },
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
                        label = { Text(stringResource(R.string.edit_profile_nik_label)) },
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
                        label = { Text(stringResource(R.string.edit_profile_phone_label)) },
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
                        text = stringResource(R.string.profile_bank_info),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Bank Name
                    OutlinedTextField(
                        value = uiState.bankName,
                        onValueChange = { viewModel.onBankNameChanged(it) },
                        label = { Text(stringResource(R.string.edit_profile_bank_name_label)) },
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
                        label = { Text(stringResource(R.string.edit_profile_account_number_label)) },
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
                        enabled = !uiState.isSaving && !uiState.isUploadingKtp,
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
                                text = stringResource(R.string.edit_profile_save_button),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.edit_profile_required_note),
                        color = Gray500,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
