package com.example.bootcamp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.viewmodel.ProfileDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailsScreen(
        viewModel: ProfileDetailsViewModel = hiltViewModel(),
        onNavigateBack: () -> Unit,
        onNavigateToEditProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Profile Details", color = Color.White) },
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
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Indigo600
                    )
                }
                uiState.profileNotFound -> {
                    // Profile not found - show create profile prompt
                    ProfileNotFoundContent(onCreateProfile = onNavigateToEditProfile)
                }
                uiState.errorMessage != null -> {
                    Column(
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                                text = "Failed to load profile",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                        )
                        Text(
                                text = uiState.errorMessage ?: "Unknown error",
                                color = Gray500,
                                textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProfile() }) { Text("Retry") }
                    }
                }
                else -> {
                    val profile = uiState.profile
                    if (profile != null) {
                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .verticalScroll(rememberScrollState())
                                                .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Personal Info Section
                            SectionTitle("Personal Information")

                            ProfileInfoCard(
                                    icon = Icons.Default.Person,
                                    label = "Username",
                                    value = profile.username
                            )

                            ProfileInfoCard(
                                    icon = Icons.Default.Email,
                                    label = "Email",
                                    value = profile.email
                            )

                            ProfileInfoCard(
                                    icon = Icons.Default.Phone,
                                    label = "Phone Number",
                                    value = profile.phoneNumber ?: "-"
                            )

                            ProfileInfoCard(
                                    icon = Icons.Default.Home,
                                    label = "Address",
                                    value = profile.address ?: "-"
                            )

                            ProfileInfoCard(
                                    icon = Icons.Default.Badge,
                                    label = "NIK",
                                    value = profile.nik ?: "-"
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // KTP Section
                            SectionTitle("ID Card (KTP)")
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                            ) {
                                val ktpPath = profile.ktpPath
                                if (!ktpPath.isNullOrBlank()) {
                                    coil.compose.SubcomposeAsyncImage(
                                        model = ktpPath,
                                        contentDescription = "KTP Photo",
                                        modifier = Modifier.fillMaxSize(),
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
                                                Text("Failed to load image", color = Color.Red, fontSize = 12.sp)
                                            }
                                        }
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Badge,
                                            contentDescription = null,
                                            tint = Gray500,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text("No KTP Uploaded", color = Gray500, fontSize = 14.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Bank Info Section
                            SectionTitle("Bank Information")

                            ProfileInfoCard(
                                    icon = Icons.Default.AccountBalance,
                                    label = "Bank Name",
                                    value = profile.bankName ?: "-"
                            )

                            ProfileInfoCard(
                                    icon = Icons.Default.CreditCard,
                                    label = "Account Number",
                                    value = profile.accountNumber ?: "-"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Edit Profile Button
                            Button(
                                    onClick = onNavigateToEditProfile,
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    colors =
                                            ButtonDefaults.buttonColors(containerColor = Indigo600),
                                    shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = "Edit Profile",
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
private fun ProfileNotFoundContent(onCreateProfile: () -> Unit) {
    Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors =
                        CardDefaults.cardColors(
                                containerColor = Color(0xFF020617).copy(alpha = 0.95f)
                        )
        ) {
            Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color(0xFFA5B4FC),
                        modifier = Modifier.size(72.dp)
                )

                Text(
                        text = "No Profile Yet",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )

                Text(
                        text =
                                "You haven't created your profile yet. Create one now to complete your account setup.",
                        fontSize = 14.sp,
                        color = Gray500,
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                        onClick = onCreateProfile,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                            text = "Create Profile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ProfileInfoCard(icon: ImageVector, label: String, value: String) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFA5B4FC),
                    modifier = Modifier.size(24.dp)
            )

            Column {
                Text(text = label, fontSize = 12.sp, color = Gray500)
                Text(
                        text = value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                )
            }
        }
    }
}
