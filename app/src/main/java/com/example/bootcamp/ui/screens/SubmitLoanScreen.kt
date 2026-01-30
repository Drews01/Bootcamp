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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.R
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.viewmodel.LoanErrorType
import com.example.bootcamp.ui.viewmodel.LoanViewModel
import java.text.NumberFormat
import java.util.Locale

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

    // State for preview dialog
    var showPreviewDialog by remember { mutableStateOf(false) }

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

    // Show Preview Dialog
    if (showPreviewDialog) {
        LoanPreviewDialog(
            amount = uiState.amount,
            tenure = uiState.tenure,
            branchName = uiState.selectedBranch?.name ?: "-",
            onDismiss = { showPreviewDialog = false },
            onConfirm = {
                showPreviewDialog = false
                viewModel.submitLoan()
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
                        text = stringResource(R.string.submit_loan_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                    Text(
                        text = stringResource(R.string.submit_loan_subtitle),
                        fontSize = 13.sp,
                        color = Gray500,
                    )

                    // Branch Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.selectedBranch?.name
                                ?: stringResource(R.string.submit_loan_select_branch),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.submit_loan_branch_label)) },
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
                                            stringResource(R.string.button_expand)
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
                                if (!uiState.isBranchesLoading) {
                                    expanded = true
                                }
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
                        label = { Text(stringResource(R.string.submit_loan_amount_label)) },
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
                        label = { Text(stringResource(R.string.submit_loan_tenor_label)) },
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
                        onClick = { showPreviewDialog = true }, // Show dialog instead of direct submit
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
                                text = stringResource(R.string.submit_loan_button),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.submit_loan_terms),
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
 * Preview dialog for loan submission.
 */
@Composable
fun LoanPreviewDialog(
    amount: String,
    tenure: String,
    branchName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        title = {
            Text(
                text = stringResource(R.string.submit_loan_confirm_title),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.submit_loan_confirm_subtitle),
                    color = Gray500,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                PreviewRow(label = stringResource(R.string.submit_loan_preview_branch), value = branchName)
                PreviewRow(
                    label = stringResource(R.string.submit_loan_confirm_amount),
                    value = formatter.format(amount.toLongOrNull() ?: 0)
                )
                PreviewRow(
                    label = stringResource(R.string.submit_loan_confirm_tenor),
                    value = stringResource(R.string.submit_loan_months, tenure)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text(stringResource(R.string.button_submit), color = Color.White)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_cancel), color = Gray500)
            }
        }
    )
}

@Composable
private fun PreviewRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Gray500, fontSize = 14.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val (titleResId, contentResId, primaryAction, primaryLabelResId, contentFormatArgs) = when (errorType) {
        is LoanErrorType.IncompleteProfile -> {
            ErrorDialogData(
                R.string.error_profile_incomplete_title,
                R.string.error_profile_incomplete_message,
                onNavigateToEditProfile,
                R.string.button_complete_profile,
                null
            )
        }
        is LoanErrorType.ActiveLoanExists -> {
            ErrorDialogData(
                R.string.error_active_loan_title,
                R.string.error_active_loan_message,
                onDismiss,
                R.string.button_ok,
                null
            )
        }
        is LoanErrorType.ExceedsLimit -> {
            ErrorDialogData(
                R.string.error_exceeds_limit_title,
                R.string.error_exceeds_limit_message,
                onDismiss,
                R.string.button_ok,
                arrayOf(errorType.remainingLimit, errorType.tier)
            )
        }
        is LoanErrorType.BranchRequired -> {
            ErrorDialogData(
                R.string.error_branch_required_title,
                R.string.error_branch_required_message,
                onDismiss,
                R.string.button_ok,
                null
            )
        }
        is LoanErrorType.BranchNotFound -> {
            ErrorDialogData(
                R.string.error_branch_not_found_title,
                R.string.error_branch_not_found_message,
                onRefreshBranches,
                R.string.button_refresh_branches,
                null
            )
        }
        is LoanErrorType.NoTierAvailable -> {
            ErrorDialogData(
                R.string.error_system_title,
                R.string.error_no_tier_message,
                onDismiss,
                R.string.button_ok,
                null
            )
        }
        is LoanErrorType.Generic -> {
            ErrorDialogData(
                R.string.error_generic_title,
                null,
                onDismiss,
                R.string.button_ok,
                null,
                errorType.message
            )
        }
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(titleResId),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            val contentText = if (contentResId != null) {
                if (contentFormatArgs != null) {
                    stringResource(contentResId, *contentFormatArgs)
                } else {
                    stringResource(contentResId)
                }
            } else {
                (errorType as LoanErrorType.Generic).message
            }
            Text(
                text = contentText,
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
                Text(stringResource(primaryLabelResId))
            }
        },
        dismissButton = {
            if (errorType !is LoanErrorType.Generic &&
                errorType !is LoanErrorType.ActiveLoanExists &&
                errorType !is LoanErrorType.BranchRequired
            ) {
                androidx.compose.material3.TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.dialog_cancel), color = Gray500)
                }
            }
        },
        containerColor = Color(0xFF1E293B),
        tonalElevation = 8.dp
    )
}

/** Helper data class for error dialog data */
private data class ErrorDialogData(
    val titleResId: Int,
    val contentResId: Int?,
    val primaryAction: () -> Unit,
    val primaryLabelResId: Int,
    val contentFormatArgs: Array<Any>? = null,
    val fallbackMessage: String? = null
)
