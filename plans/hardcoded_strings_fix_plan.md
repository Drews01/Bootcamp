# Hardcoded Strings Fix Implementation Plan

## Overview
This document outlines the plan to fix remaining hardcoded strings in the Bootcamp Android application. The app already has a comprehensive localization infrastructure with English (default) and Indonesian translations, but some screens still contain hardcoded strings that bypass the localization system.

## Current State Analysis

### Screens with Hardcoded Strings

#### 1. SubmitLoanScreen.kt
**Location:** `app/src/main/java/com/example/bootcamp/ui/screens/SubmitLoanScreen.kt`

**Hardcoded Indonesian Strings Found:**

| Line | Current Text | Existing String Key | Action |
|------|--------------|---------------------|--------|
| 142 | `"Form Pengajuan"` | `submit_loan_title` | Replace with `stringResource(R.string.submit_loan_title)` |
| 148 | `"Isi data singkat pengajuanmu."` | `submit_loan_subtitle` | Replace with `stringResource(R.string.submit_loan_subtitle)` |
| 157 | `"Pilih Cabang"` | `submit_loan_select_branch` | Replace with `stringResource(R.string.submit_loan_select_branch)` |
| 160 | `"Cabang"` | `submit_loan_branch_label` | Replace with `stringResource(R.string.submit_loan_branch_label)` |
| 181 | `"Expand"` | `button_expand` | Replace with `stringResource(R.string.button_expand)` |
| 254 | `"Limit pengajuan (Rp)"` | `submit_loan_amount_label` | Replace with `stringResource(R.string.submit_loan_amount_label)` |
| 284 | `"Tenor (bulan)"` | `submit_loan_tenor_label` | Replace with `stringResource(R.string.submit_loan_tenor_label)` |
| 330 | `"Kirim Pengajuan"` | `submit_loan_button` | Replace with `stringResource(R.string.submit_loan_button)` |
| 339 | `"Dengan menekan tombol..."` | `submit_loan_terms` | Replace with `stringResource(R.string.submit_loan_terms)` |
| 367 | `"Konfirmasi Pengajuan"` | `submit_loan_confirm_title` | Replace with `stringResource(R.string.submit_loan_confirm_title)` |
| 375 | `"Pastikan data berikut sudah benar:"` | `submit_loan_confirm_subtitle` | Replace with `stringResource(R.string.submit_loan_confirm_subtitle)` |
| 382 | `"Cabang"` | NEW KEY NEEDED | Add `submit_loan_preview_branch` |
| 384 | `"Jumlah Pinjaman"` | `submit_loan_confirm_amount` | Replace with `stringResource(R.string.submit_loan_confirm_amount)` |
| 391 | `"Tenor"` | `submit_loan_confirm_tenor` | Replace with `stringResource(R.string.submit_loan_confirm_tenor)` |
| 391 | `"Bulan"` | `submit_loan_months` | Replace with `stringResource(R.string.submit_loan_months)` |
| 399 | `"Kirim"` | `button_submit` | Replace with `stringResource(R.string.button_submit)` |
| 404 | `"Batal"` | `button_cancel` | Replace with `stringResource(R.string.button_cancel)` |
| 522 | `"Cancel"` | `dialog_cancel` | Replace with `stringResource(R.string.dialog_cancel)` |

**Error Dialog Hardcoded Strings (lines 432-488):**
These are currently hardcoded in the `when` expression. They should use string resources:

| Error Type | Title Key | Message Key |
|------------|-----------|-------------|
| IncompleteProfile | `error_profile_incomplete_title` | `error_profile_incomplete_message` |
| ActiveLoanExists | `error_active_loan_title` | `error_active_loan_message` |
| ExceedsLimit | `error_exceeds_limit_title` | `error_exceeds_limit_message` |
| BranchRequired | `error_branch_required_title` | `error_branch_required_message` |
| BranchNotFound | `error_branch_not_found_title` | `error_branch_not_found_message` |
| NoTierAvailable | `error_system_title` | `error_no_tier_message` |
| Generic | `error_generic_title` | Uses dynamic message |

**Button Labels in Error Dialog:**
- `"Complete Profile"` → `button_complete_profile`
- `"OK"` → `button_ok`
- `"Refresh Branches"` → `button_refresh_branches`
- `"Cancel"` → `dialog_cancel`

---

#### 2. EditProfileScreen.kt
**Location:** `app/src/main/java/com/example/bootcamp/ui/screens/EditProfileScreen.kt`

**Hardcoded English Strings Found:**

| Line | Current Text | Existing String Key | Action |
|------|--------------|---------------------|--------|
| 158 | `"Complete your profile..."` | `edit_profile_description` | Replace with `stringResource(R.string.edit_profile_description)` |
| 165 | `"ID Card (KTP) *"` | `edit_profile_ktp_label` | Replace with `stringResource(R.string.edit_profile_ktp_label)` |
| 194 | `"KTP Photo"` | `profile_ktp_photo` | Replace with `stringResource(R.string.profile_ktp_photo)` |
| 210 | `"Failed to load image"` | `error_load_image` | Replace with `stringResource(R.string.error_load_image)` |
| 222 | `"No KTP Uploaded"` | `profile_no_ktp` | Replace with `stringResource(R.string.profile_no_ktp)` |
| 261 | `"Camera"` | `button_camera` | Replace with `stringResource(R.string.button_camera)` |
| 271 | `"Gallery"` | `button_gallery` | Replace with `stringResource(R.string.button_gallery)` |
| 281 | `"Address *"` | `edit_profile_address_label` | Replace with `stringResource(R.string.edit_profile_address_label)` |
| 301 | `"NIK (16 digits) *"` | `edit_profile_nik_label` | Replace with `stringResource(R.string.edit_profile_nik_label)` |
| 325 | `"Phone Number *"` | `edit_profile_phone_label` | Replace with `stringResource(R.string.edit_profile_phone_label)` |
| 345 | `"Bank Information"` | `profile_bank_info` | Replace with `stringResource(R.string.profile_bank_info)` |
| 355 | `"Bank Name *"` | `edit_profile_bank_name_label` | Replace with `stringResource(R.string.edit_profile_bank_name_label)` |
| 375 | `"Account Number *"` | `edit_profile_account_number_label` | Replace with `stringResource(R.string.edit_profile_account_number_label)` |
| 414 | `"Save Profile"` | `edit_profile_save_button` | Replace with `stringResource(R.string.edit_profile_save_button)` |
| 422 | `"* Required fields"` | `edit_profile_required_note` | Replace with `stringResource(R.string.edit_profile_required_note)` |

---

## New String Resources Required

### For English (values/strings.xml):
```xml
<!-- Submit Loan Screen - Preview Dialog -->
<string name="submit_loan_preview_branch">Branch</string>
```

### For Indonesian (values-id/strings.xml):
```xml
<!-- Submit Loan Screen - Preview Dialog -->
<string name="submit_loan_preview_branch">Cabang</string>
```

---

## Implementation Steps

### Phase 1: Update SubmitLoanScreen.kt

1. **Add import for stringResource** (if not already present):
   ```kotlin
   import androidx.compose.ui.res.stringResource
   import com.example.bootcamp.R
   ```

2. **Replace hardcoded strings in main screen** (lines 141-344):
   - Replace all `Text(text = "...")` with `Text(text = stringResource(R.string....))`
   - Replace `label = { Text("...") }` with `label = { Text(stringResource(R.string....)) }`
   - Replace `contentDescription = "..."` with `contentDescription = stringResource(R.string....)`

3. **Update LoanPreviewDialog** (lines 354-408):
   - Replace hardcoded dialog title, subtitle, labels, and button text
   - Use stringResource with formatting for dynamic values

4. **Refactor LoanErrorDialog** (lines 424-529):
   - Change the `when` expression to return string resource IDs instead of hardcoded strings
   - Use `stringResource()` when displaying the text
   - For `ExceedsLimit`, use `stringResource(R.string.error_exceeds_limit_message, errorType.remainingLimit, errorType.tier)`

### Phase 2: Update EditProfileScreen.kt

1. **Add import for stringResource** (if not already present):
   ```kotlin
   import androidx.compose.ui.res.stringResource
   import com.example.bootcamp.R
   ```

2. **Replace all hardcoded strings** (lines 158-422):
   - Replace all `Text(text = "...")` with `Text(text = stringResource(R.string....))`
   - Replace all `label = { Text("...") }` with `label = { Text(stringResource(R.string....)) }`
   - Replace `contentDescription = "..."` with `contentDescription = stringResource(R.string....)`

### Phase 3: Add Missing String Resources

1. **Add to `app/src/main/res/values/strings.xml`**:
   ```xml
   <string name="submit_loan_preview_branch">Branch</string>
   ```

2. **Add to `app/src/main/res/values-id/strings.xml`**:
   ```xml
   <string name="submit_loan_preview_branch">Cabang</string>
   ```

### Phase 4: Verification

1. Build the app and verify no compilation errors
2. Run the app in English mode and verify all text displays correctly
3. Run the app in Indonesian mode and verify all text displays correctly
4. Check that error dialogs display properly with correct formatting

---

## Code Examples

### Example 1: Replacing Text in SubmitLoanScreen

**Before:**
```kotlin
Text(
    text = "Form Pengajuan",
    fontSize = 20.sp,
    fontWeight = FontWeight.SemiBold,
    color = Color.White,
)
```

**After:**
```kotlin
Text(
    text = stringResource(R.string.submit_loan_title),
    fontSize = 20.sp,
    fontWeight = FontWeight.SemiBold,
    color = Color.White,
)
```

### Example 2: Replacing Label in OutlinedTextField

**Before:**
```kotlin
OutlinedTextField(
    value = uiState.amount,
    onValueChange = { viewModel.onAmountChanged(it) },
    label = { Text("Limit pengajuan (Rp)") },
    // ...
)
```

**After:**
```kotlin
OutlinedTextField(
    value = uiState.amount,
    onValueChange = { viewModel.onAmountChanged(it) },
    label = { Text(stringResource(R.string.submit_loan_amount_label)) },
    // ...
)
```

### Example 3: Error Dialog with Parameters

**Before:**
```kotlin
is LoanErrorType.ExceedsLimit -> {
    Quadruple(
        "Exceeds Credit Limit",
        "Loan amount exceeds remaining credit limit of Rp ${errorType.remainingLimit} for ${errorType.tier} tier. Please lower the amount.",
        onDismiss,
        "OK"
    )
}
```

**After:**
```kotlin
is LoanErrorType.ExceedsLimit -> {
    Quadruple(
        R.string.error_exceeds_limit_title,
        R.string.error_exceeds_limit_message,
        onDismiss,
        R.string.button_ok
    )
}
// Then in the dialog:
Text(
    text = if (errorType is LoanErrorType.ExceedsLimit) {
        stringResource(content, errorType.remainingLimit, errorType.tier)
    } else {
        stringResource(content)
    },
    color = Gray500
)
```

---

## Files to Modify

1. `app/src/main/java/com/example/bootcamp/ui/screens/SubmitLoanScreen.kt`
2. `app/src/main/java/com/example/bootcamp/ui/screens/EditProfileScreen.kt`
3. `app/src/main/res/values/strings.xml` (add 1 new string)
4. `app/src/main/res/values-id/strings.xml` (add 1 new string)

---

## Testing Checklist

- [ ] SubmitLoanScreen displays all text in English when language is English
- [ ] SubmitLoanScreen displays all text in Indonesian when language is Indonesian
- [ ] EditProfileScreen displays all text in English when language is English
- [ ] EditProfileScreen displays all text in Indonesian when language is Indonesian
- [ ] Error dialogs in SubmitLoanScreen show correct localized text
- [ ] Preview dialog in SubmitLoanScreen shows correct localized text
- [ ] All labels and buttons are properly localized
- [ ] No hardcoded strings remain in SubmitLoanScreen.kt
- [ ] No hardcoded strings remain in EditProfileScreen.kt

---

## Notes

1. Most string resources already exist in both English and Indonesian strings.xml files
2. Only one new string needs to be added: `submit_loan_preview_branch`
3. The error dialog in SubmitLoanScreen requires refactoring to use string resource IDs instead of hardcoded strings
4. All other screens (HomeScreen, LoginScreen, RegisterScreen, ForgotPasswordScreen, ProfileDetailsScreen, LoanHistoryScreen, UserProfileScreen) already use stringResource() correctly
5. Components (EmptyStateCard, UserProductTierCard) already use stringResource() correctly
