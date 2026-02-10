package com.example.bootcamp.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.R
import com.example.bootcamp.ui.theme.Gray400
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Indigo600
import com.example.bootcamp.ui.viewmodel.AuthViewModel
import java.text.NumberFormat
import java.util.Locale

private data class LoanProduct(
    val name: String,
    val limitRupiah: Long,
    val maxTenorMonth: Int,
    val ratePercentPerYear: Double,
)

private val loanProducts =
    listOf(
        LoanProduct(
            name = "Bronze",
            limitRupiah = 10_000_000,
            maxTenorMonth = 36,
            ratePercentPerYear = 8.0,
        ),
        LoanProduct(
            name = "Silver",
            limitRupiah = 25_000_000,
            maxTenorMonth = 36,
            ratePercentPerYear = 7.0,
        ),
        LoanProduct(
            name = "Gold",
            limitRupiah = 50_000_000,
            maxTenorMonth = 36,
            ratePercentPerYear = 6.0,
        ),
    )

@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    homeViewModel: com.example.bootcamp.ui.viewmodel.HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToSubmitLoan: () -> Unit,
    // Default empty for now to avoid breaking changes immediately, user can wire up later
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()

    // Fetch data when logged in
    androidx.compose.runtime.LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            homeViewModel.loadData()
        }
    }

    // Select the first product by default
    var selectedProduct by remember { mutableStateOf(loanProducts.first()) }

    Box(
        modifier =
        modifier
            .fillMaxSize()
            .background(
                brush =
                Brush.verticalGradient(
                    colors =
                    listOf(
                        Color(0xFF0F1020),
                        Color(0xFF14162B),
                        Color(0xFF111827),
                    )
                )
            ),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Column {
                    val greeting = if (uiState.isLoggedIn && uiState.username != null) {
                        stringResource(R.string.home_greeting_user, uiState.username ?: "")
                    } else {
                        stringResource(R.string.home_greeting_guest)
                    }
                    Text(
                        text = greeting,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = stringResource(R.string.home_subtitle),
                        fontSize = 14.sp,
                        color = Gray400,
                    )
                }
            }

            // Tier Card or Empty State
            if (uiState.isLoggedIn) {
                item {
                    if (homeUiState.userTier != null) {
                        com.example.bootcamp.ui.components.UserProductTierCard(
                            tierDTO = homeUiState.userTier!!
                        )
                    } else if (!homeUiState.isTierLoading && !homeUiState.isProfileLoading) {
                        // If logic: Profile matches "New User" / 404 (implied by null tier + loaded) -> Show Empty State
                        com.example.bootcamp.ui.components.EmptyStateCard(
                            onGetStartedClick = onNavigateToProfile
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.home_select_product),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
                ProductSelector(
                    products = loanProducts,
                    selectedProduct = selectedProduct,
                    onProductSelected = { selectedProduct = it }
                )
            }

            item {
                LoanSimulator(
                    product = selectedProduct,
                    isLoggedIn = uiState.isLoggedIn,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToSubmitLoan = onNavigateToSubmitLoan
                )
            }
        }
    }
}

@Composable
private fun ProductSelector(
    products: List<LoanProduct>,
    selectedProduct: LoanProduct,
    onProductSelected: (LoanProduct) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        products.forEach { product ->
            val isSelected = product.name == selectedProduct.name

            val activeColor = when (product.name) {
                "Bronze" -> Color(0xFFCD7F32)
                "Silver" -> Color(0xFF64748B)
                "Gold" -> Color(0xFFD97706)
                else -> Indigo600
            }

            val backgroundColor = if (isSelected) activeColor else Color(0xFF1F2937)
            val borderColor = if (isSelected) activeColor else Color.Transparent

            Card(
                modifier = Modifier.weight(1f).height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                onClick = { onProductSelected(product) }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val productName = when (product.name) {
                        "Bronze" -> stringResource(R.string.tier_bronze)
                        "Silver" -> stringResource(R.string.tier_silver)
                        "Gold" -> stringResource(R.string.tier_gold)
                        else -> product.name
                    }
                    Text(
                        text = productName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.interest_rate_per_year, product.ratePercentPerYear),
                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Gray400,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LoanSimulator(
    product: LoanProduct,
    isLoggedIn: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToSubmitLoan: () -> Unit,
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    val decimalFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    // Use derived state for initial values to update when product changes,
    // but separate mutable state for user edits.
    var limitText by remember(product) { mutableStateOf(decimalFormatter.format(product.limitRupiah)) }
    var tenorText by remember(product) { mutableStateOf(product.maxTenorMonth.toString()) }

    // Parse logic
    val rawLimit = limitText.replace(",", "").toLongOrNull() ?: 0L
    // Clamp for calculation: Min 100,000, Max 50,000,000
    // Note: The input field itself will restrict typing > 50,000,000
    // But for calculation, we also ensure it respects the bounds.
    val limit = rawLimit.coerceAtMost(50_000_000).coerceAtMost(product.limitRupiah)

    // Tenure validation: Max 36
    val rawTenor = tenorText.toIntOrNull() ?: 0
    val tenor = rawTenor.coerceAtMost(36).coerceAtMost(product.maxTenorMonth).coerceAtLeast(1)

    val yearlyRate = product.ratePercentPerYear / 100.0
    val totalInterest = limit * yearlyRate * (tenor / 12.0)
    val totalPayment = limit + totalInterest
    val monthlyInstallment = if (tenor > 0) totalPayment / tenor else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827).copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val productName = when (product.name) {
                    "Bronze" -> stringResource(R.string.tier_bronze)
                    "Silver" -> stringResource(R.string.tier_silver)
                    "Gold" -> stringResource(R.string.tier_gold)
                    else -> product.name
                }
                Text(
                    text = stringResource(R.string.loan_simulation_title, productName),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
                Text(
                    text = stringResource(R.string.loan_max_limit, formatter.format(product.limitRupiah)),
                    color = Color(0xFF6EE7B7),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.loan_amount_label),
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { value ->
                            // Remove existing commas to check raw number
                            val filtered = value.filter { it.isDigit() }
                            val number = filtered.toLongOrNull()

                            if (number != null) {
                                // Max validation: 50,000,000
                                if (number <= 50_000_000) {
                                    limitText = decimalFormatter.format(number)
                                }
                            } else if (filtered.isEmpty()) {
                                limitText = ""
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = Gray500,
                            cursorColor = Color.White,
                            focusedLabelColor = Indigo600,
                            unfocusedLabelColor = Gray500,
                        ),
                        supportingText = {
                            if (rawLimit < 100_000 && rawLimit != 0L) {
                                Text(
                                    text = "Min 100.000",
                                    color = Color.Red,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    )
                }
                Column(modifier = Modifier.weight(0.7f)) {
                    Text(
                        text = stringResource(R.string.loan_tenor_label),
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = tenorText,
                        onValueChange = { value ->
                            val filtered = value.filter { it.isDigit() }
                            val number = filtered.toIntOrNull()
                            if (number != null) {
                                // Max validation: 36
                                if (number <= 36) {
                                    tenorText = filtered
                                }
                            } else if (filtered.isEmpty()) {
                                tenorText = ""
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = Gray500,
                            cursorColor = Color.White,
                            focusedLabelColor = Indigo600,
                            unfocusedLabelColor = Gray500,
                        )
                    )
                    Text(
                        text = stringResource(R.string.loan_max_tenor, product.maxTenorMonth),
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Result
            Crossfade(targetState = monthlyInstallment) { installment ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1F2937), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.loan_monthly_estimate),
                        color = Color.White,
                        fontSize = 13.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatter.format(installment),
                        color = Color(0xFF4ADE80),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Button(
                onClick = {
                    if (isLoggedIn) {
                        onNavigateToSubmitLoan()
                    } else {
                        onNavigateToLogin()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                // Disable button if limit is effectively invalid for "Apply" logic if we wanted,
                // but user didn't ask to disable. Just validation visuals.
                // However, minimum logic usually implies you can't submit if < min.
                enabled = rawLimit >= 100_000
            ) {
                Text(
                    text = stringResource(R.string.loan_apply_now),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
