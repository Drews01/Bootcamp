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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.viewmodel.LoanErrorType
import com.example.bootcamp.ui.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitLoanScreen(
        viewModel: LoanViewModel,
        modifier: Modifier = Modifier,
        onSubmitSuccess: () -> Unit = {},
        onNavigateToEditProfile: () -> Unit = {}
) {
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        var expanded by remember { mutableStateOf(false) }

        LaunchedEffect(uiState.successMessage) {
                uiState.successMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearSuccessMessage()
                        onSubmitSuccess()
                }
        }

        // Show error dialog when errorType is present
        uiState.errorType?.let { errorType ->
            LoanErrorDialog(
                errorType = errorType,
                errorMessage = uiState.errorMessage ?: "",
                onDismiss = { viewModel.clearErrorMessage() },
                onNavigateToEditProfile = {
                    viewModel.clearErrorMessage()
                    onNavigateToEditProfile()
                },
                onRefreshBranches = {
                    viewModel.clearErrorMessage()
                    viewModel.loadBranches()
                }
            )
        }

        Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = Color.Transparent,
                modifier = modifier
        ) { paddingValues ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(
                                                brush =
                                                        Brush.verticalGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color(0xFF0F1020),
                                                                                Color(0xFF111827),
                                                                        )
                                                        )
                                        )
                                        .padding(paddingValues)
                                        .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor =
                                                        Color(0xFF020617).copy(alpha = 0.95f),
                                        ),
                        ) {
                                Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                        Text(
                                                text = "Form Pengajuan",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White,
                                        )
                                        Text(
                                                text = "Isi data singkat pengajuanmu.",
                                                fontSize = 13.sp,
                                                color = Gray500,
                                        )

                                                // Branch Dropdown
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                                OutlinedTextField(
                                                        value = uiState.selectedBranch?.name
                                                                        ?: "Pilih Cabang",
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Cabang") },
                                                        trailingIcon = {
                                                                if (uiState.isBranchesLoading) {
                                                                        CircularProgressIndicator(
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                24.dp
                                                                                        ),
                                                                                strokeWidth = 2.dp
                                                                        )
                                                                } else {
                                                                        IconButton(
                                                                                onClick = {
                                                                                        expanded =
                                                                                                true
                                                                                }
                                                                        ) {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .ArrowDropDown,
                                                                                        contentDescription =
                                                                                                "Expand"
                                                                                )
                                                                        }
                                                                }
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        enabled = !uiState.isBranchesLoading,
                                                        colors =
                                                                androidx.compose.material3
                                                                        .OutlinedTextFieldDefaults
                                                                        .colors(
                                                                                focusedTextColor =
                                                                                        Color.White,
                                                                                unfocusedTextColor =
                                                                                        Color.White,
                                                                                focusedBorderColor =
                                                                                        Indigo600,
                                                                                unfocusedBorderColor =
                                                                                        Gray500,
                                                                                focusedLabelColor =
                                                                                        Indigo600,
                                                                                unfocusedLabelColor =
                                                                                        Gray500,
                                                                                cursorColor =
                                                                                        Color.White,
                                                                        )
                                                )

                                                // Invisible button to open dropdown when clicking
                                                // text field
                                                androidx.compose.material3.Surface(
                                                        modifier =
                                                                Modifier.matchParentSize()
                                                                        .padding(
                                                                                top = 8.dp
                                                                        ), // Adjust for label
                                                        color = Color.Transparent,
                                                        onClick = {
                                                                if (!uiState.isBranchesLoading)
                                                                        expanded = true
                                                        }
                                                ) {}

                                                DropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false },
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        containerColor = Color(0xFF1E293B)
                                                ) {
                                                        uiState.branches.forEach { branch ->
                                                                DropdownMenuItem(
                                                                        text = {
                                                                                Text(
                                                                                        branch.name,
                                                                                        color = Color.White
                                                                                )
                                                                        },
                                                                        onClick = {
                                                                                viewModel.onBranchSelected(
                                                                                        branch
                                                                                )
                                                                                expanded = false
                                                                        }
                                                                )
                                                        }
                                                }
                                        }

                                        // Loan Amount
                                        OutlinedTextField(
                                                value = uiState.amount,
                                                onValueChange = { viewModel.onAmountChanged(it) },
                                                label = { Text("Limit pengajuan (Rp)") },
                                                singleLine = true,
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        androidx.compose.material3
                                                                .OutlinedTextFieldDefaults.colors(
                                                                        focusedTextColor =
                                                                                Color.White,
                                                                        unfocusedTextColor =
                                                                                Color.White,
                                                                        focusedBorderColor =
                                                                                Indigo600,
                                                                        unfocusedBorderColor =
                                                                                Gray500,
                                                                        focusedLabelColor =
                                                                                Indigo600,
                                                                        unfocusedLabelColor =
                                                                                Gray500,
                                                                        cursorColor = Color.White,
                                                                )
                                        )

                                        // Tenure
                                        OutlinedTextField(
                                                value = uiState.tenure,
                                                onValueChange = { viewModel.onTenureChanged(it) },
                                                label = { Text("Tenor (bulan)") },
                                                singleLine = true,
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        androidx.compose.material3
                                                                .OutlinedTextFieldDefaults.colors(
                                                                        focusedTextColor =
                                                                                Color.White,
                                                                        unfocusedTextColor =
                                                                                Color.White,
                                                                        focusedBorderColor =
                                                                                Indigo600,
                                                                        unfocusedBorderColor =
                                                                                Gray500,
                                                                        focusedLabelColor =
                                                                                Indigo600,
                                                                        unfocusedLabelColor =
                                                                                Gray500,
                                                                        cursorColor = Color.White,
                                                                )
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(
                                                onClick = { viewModel.submitLoan() },
                                                enabled = uiState.isSubmitEnabled,
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = Indigo600,
                                                                disabledContainerColor =
                                                                        Indigo600.copy(alpha = 0.5f)
                                                        ),
                                        ) {
                                                if (uiState.isSubmitting) {
                                                        CircularProgressIndicator(
                                                                color = Color.White,
                                                                modifier = Modifier.size(24.dp)
                                                        )
                                                } else {
                                                        Text(
                                                                text = "Kirim Pengajuan",
                                                                fontSize = 15.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                        )
                                                }
                                        }

                                        Text(
                                                text =
                                                        "Dengan menekan tombol di atas, kamu menyetujui syarat dan ketentuan yang berlaku.",
                                                fontSize = 11.sp,
                                                color = Gray500,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth(),
                                        )
                                }
                        }
                }
        }
}

/**
 * Error dialog that shows error-specific content and actions based on error type.
 */
@Composable
fun LoanErrorDialog(
    errorType: LoanErrorType,
    errorMessage: String,
    onDismiss: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onRefreshBranches: () -> Unit
) {
    val (title, content, primaryAction, primaryLabel) = when (errorType) {
        is LoanErrorType.IncompleteProfile -> {
            Quadruple(
                "Profile Incomplete",
                "Silahkan Lengkapi Data Anda terlebih dahulu",
                onNavigateToEditProfile,
                "Complete Profile"
            )
        }
        is LoanErrorType.ActiveLoanExists -> {
            Quadruple(
                "Active Loan Exists",
                "Cannot submit new loan. You already have an active loan application that is being processed. Please wait for your current loan to be disbursed, paid, or rejected before submitting a new one.",
                onDismiss,
                "OK"
            )
        }
        is LoanErrorType.ExceedsLimit -> {
            Quadruple(
                "Exceeds Credit Limit",
                "Loan amount exceeds remaining credit limit of Rp ${errorType.remainingLimit} for ${errorType.tier} tier. Please lower the amount.",
                onDismiss,
                "OK"
            )
        }
        is LoanErrorType.BranchRequired -> {
            Quadruple(
                "Branch Required",
                "Branch ID is required for loan submission. Please select a branch.",
                onDismiss,
                "OK"
            )
        }
        is LoanErrorType.BranchNotFound -> {
            Quadruple(
                "Branch Not Found",
                "The selected branch was not found. Please refresh and select another branch.",
                onRefreshBranches,
                "Refresh Branches"
            )
        }
        is LoanErrorType.NoTierAvailable -> {
            Quadruple(
                "System Error",
                "No tier product available for user. Please contact support.",
                onDismiss,
                "OK"
            )
        }
        is LoanErrorType.Generic -> {
            Quadruple(
                "Error",
                errorType.message,
                onDismiss,
                "OK"
            )
        }
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Text(
                text = content,
                color = Gray500
            )
        },
        confirmButton = {
            Button(
                onClick = primaryAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Indigo600
                )
            ) {
                Text(primaryLabel)
            }
        },
        dismissButton = {
            if (errorType !is LoanErrorType.Generic && 
                errorType !is LoanErrorType.ActiveLoanExists &&
                errorType !is LoanErrorType.BranchRequired) {
                androidx.compose.material3.TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Gray500)
                }
            }
        },
        containerColor = Color(0xFF1E293B),
        tonalElevation = 8.dp
    )
}

/** Helper data class for dialog content */
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
