# Localization Implementation Documentation

## Overview

This document describes the implementation of multi-language support (English and Indonesian) for the Bootcamp Android application. The default language is **Indonesian**.

## Current State

The application previously had hardcoded strings mixed between English and Indonesian throughout the UI. This implementation standardizes all user-facing text using Android's resource localization system.

## Architecture

### File Structure

```
app/src/main/res/
├── values/
│   └── strings.xml          # English (fallback)
├── values-id/
│   └── strings.xml          # Indonesian (default)
├── xml/
│   └── locales_config.xml   # Supported locales configuration
```

### Language Manager Architecture

```
User selects language in UserProfileScreen
    ↓
LanguageSettingsSection (UI Component)
    ↓
LanguageViewModel
    ↓
LanguageManager
    ↓
├── Save preference to DataStore
├── Update AppCompatDelegate locale
└── Recreate Activity
    ↓
App restarts with new locale
    ↓
stringResource() reads from correct strings.xml
```

## Components

### 1. Language Enum

Location: `app/src/main/java/com/example/bootcamp/util/Language.kt`

```kotlin
enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    INDONESIAN("id", "Bahasa Indonesia");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: INDONESIAN
        }
    }
}
```

### 2. LanguagePreferences (DataStore)

Location: `app/src/main/java/com/example/bootcamp/data/local/LanguagePreferences.kt`

- Stores language preference persistently
- Provides Flow for reactive updates
- Default language: **Indonesian**

### 3. LanguageManager

Location: `app/src/main/java/com/example/bootcamp/util/LanguageManager.kt`

- Core language management logic
- Handles locale switching
- Applies stored language on app startup
- Supports API 33+ per-app language preferences

### 4. LanguageViewModel

Location: `app/src/main/java/com/example/bootcamp/ui/viewmodel/LanguageViewModel.kt`

- UI state management for language settings
- Bridges UI and LanguageManager

### 5. LanguageSettingsSection

Location: `app/src/main/java/com/example/bootcamp/ui/components/LanguageSettingsSection.kt`

- Static language selector with two buttons
- Integrated into UserProfileScreen
- Shows current selection with checkmark

## String Resources

### Default Language: Indonesian (values-id/strings.xml)

All strings are defined with Indonesian as the default. English strings serve as fallback.

### Key String Categories

1. **Home Screen** - Product selection, loan simulation
2. **Authentication** - Login, register, forgot password
3. **Profile** - Profile details, edit profile
4. **Loan** - Loan history, loan submission
5. **Settings** - Language selection
6. **Errors** - Validation messages, system errors
7. **UI Components** - Buttons, labels, placeholders

### String Naming Convention

```
{screen}_{description}

Examples:
- login_welcome
- profile_edit_button
- error_invalid_email
- loan_amount_label
```

## UI Implementation

### UserProfileScreen Integration

The language selector is added as a "Settings" section in UserProfileScreen:

```kotlin
Column {
    // ... existing sections ...

    // NEW: Settings Section
    LanguageSettingsSection()

    // ... logout button ...
}
```

### Language Selector UI

Two static buttons side by side:

```
┌─────────────────────────────────────┐
│  Settings                           │
├─────────────────────────────────────┤
│  Language                           │
│  Choose your preferred language     │
│                                     │
│  ┌────────────┐  ┌────────────┐    │
│  │ ✓ English  │  │ Indonesian │    │
│  └────────────┘  └────────────┘    │
└─────────────────────────────────────┘
```

## Implementation Steps

### Phase 1: String Resources

1. Create `values/strings.xml` with English translations
2. Create `values-id/strings.xml` with Indonesian translations (default)
3. Create `xml/locales_config.xml` for supported locales

### Phase 2: Language Management

1. Create `Language.kt` enum
2. Create `LanguagePreferences.kt` for DataStore
3. Create `LanguageManager.kt` for locale management
4. Create `LanguageViewModel.kt` for UI state

### Phase 3: UI Components

1. Create `LanguageSettingsSection.kt`
2. Integrate into `ProfileDetailsScreen.kt`

### Phase 4: App Integration

1. Update `MainActivity.kt` to apply stored language
2. Update `BootcampApplication.kt` for early locale setup
3. Update `AndroidManifest.xml` with localeConfig
4. Add Hilt DI module for language components

### Phase 5: String Migration

1. Replace all hardcoded strings with `stringResource()`
2. Handle string formatting with parameters
3. Test all screens in both languages

## Configuration

### AndroidManifest.xml

```xml
<application
    android:name=".BootcampApplication"
    android:localeConfig="@xml/locales_config"
    ...>
```

### locales_config.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<locale-config xmlns:android="http://schemas.android.com/apk/res/android">
    <locale android:name="id"/>  <!-- Default: Indonesian -->
    <locale android:name="en"/>  <!-- English -->
</locale-config>
```

### build.gradle.kts

```kotlin
android {
    defaultConfig {
        resourceConfigurations += listOf("en", "id")
    }
}
```

## Usage Examples

### Basic String

```kotlin
// Before
Text(text = "Welcome Back")

// After
Text(text = stringResource(R.string.login_welcome))
```

### String with Parameters

```kotlin
// Before
Text(text = "Hi, ${username}")

// After
Text(text = stringResource(R.string.home_greeting_user, username))
```

### Content Description

```kotlin
// Before
Icon(
    imageVector = Icons.Default.Person,
    contentDescription = "User profile"
)

// After
Icon(
    imageVector = Icons.Default.Person,
    contentDescription = stringResource(R.string.content_desc_user_profile)
)
```

## Language Change Flow

1. User taps language button in ProfileDetailsScreen
2. `LanguageViewModel.changeLanguage()` is called
3. `LanguageManager.setLanguage()`:
   - Saves to DataStore
   - Updates AppCompatDelegate locale
   - Recreates activity
4. App restarts with new locale
5. All `stringResource()` calls now use the new language

## Best Practices Applied

1. **Android Guidelines**: Using `strings.xml` as per official documentation
2. **DataStore**: Modern replacement for SharedPreferences
3. **AppCompatDelegate**: Handles per-app language preferences (API 33+)
4. **Static UI**: Simple two-button selector for better UX
5. **Activity Recreation**: Cleanest way to apply locale changes
6. **Default Language**: Indonesian set as default for target market

## Testing Checklist

- [ ] App starts with Indonesian language by default
- [ ] Language persists after app kill
- [ ] All screens update when language changes
- [ ] String formatting works correctly (parameters, plurals)
- [ ] No hardcoded strings remain in UI
- [ ] Content descriptions are localized
- [ ] Error messages display correctly in both languages
- [ ] System settings integration works (Android 13+)

## Files Modified/Created

### New Files
- `app/src/main/java/com/example/bootcamp/util/Language.kt`
- `app/src/main/java/com/example/bootcamp/data/local/LanguagePreferences.kt`
- `app/src/main/java/com/example/bootcamp/util/LanguageManager.kt`
- `app/src/main/java/com/example/bootcamp/ui/viewmodel/LanguageViewModel.kt`
- `app/src/main/java/com/example/bootcamp/ui/components/LanguageSettingsSection.kt`
- `app/src/main/java/com/example/bootcamp/di/LanguageModule.kt`
- `app/src/main/res/xml/locales_config.xml`
- `app/src/main/res/values/strings.xml` (complete)
- `app/src/main/res/values-id/strings.xml` (complete)

### Modified Files
- `app/src/main/java/com/example/bootcamp/MainActivity.kt`
- `app/src/main/java/com/example/bootcamp/BootcampApplication.kt`
- `app/src/main/java/com/example/bootcamp/ui/screens/UserProfileScreen.kt`
- `app/src/main/java/com/example/bootcamp/ui/screens/ProfileDetailsScreen.kt`
- `app/src/main/AndroidManifest.xml`
- `app/build.gradle.kts`
- All UI screen files (to use stringResource)

## Notes

- Backend error messages remain in their original language
- Date/number formatting should use `DateTimeFormatter` with current locale
- Currency formatting uses Indonesian Rupiah format consistently
- Language change requires activity recreation for immediate effect
