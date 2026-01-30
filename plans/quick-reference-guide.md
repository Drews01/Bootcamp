# Quick Reference Guide
## STAR Financial App - Style1 Theme Implementation

**For Developers**: This is your quick reference for implementing the Style1 theme across all screens.

---

## 🎨 Color Quick Reference

```kotlin
// Import these colors
import com.example.bootcamp.ui.theme.*

// Background Gradients
val backgroundGradient = Brush.linearGradient(
    colors = listOf(DeepSpace1, DeepSpace2, DeepSpace3)
)

// Button Gradient
val buttonGradient = Brush.horizontalGradient(
    colors = listOf(SpaceIndigo, SpaceViolet)
)

// Text Colors
val primaryText = Color.White
val secondaryText = MutedGray

// Status Colors
val success = Emerald500
val error = Red500
val warning = Amber500
val info = Blue500
```

---

## 📏 Spacing Quick Reference

```kotlin
// Use these spacing values consistently
val spacingXXS = 4.dp
val spacingXS = 8.dp
val spacingS = 12.dp
val spacingM = 16.dp
val spacingL = 20.dp
val spacingXL = 24.dp
val spacingXXL = 32.dp
val spacingXXXL = 48.dp

// Common usage
Modifier.padding(spacingM)           // 16.dp
Modifier.padding(horizontal = spacingXL)  // 24.dp
Spacer(modifier = Modifier.height(spacingL))  // 20.dp
```

---

## 🔤 Typography Quick Reference

```kotlin
// Headings
Text(
    text = "Display Title",
    fontSize = 56.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 8.sp,
    color = Color.White
)

Text(
    text = "Title Large",
    fontSize = 28.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.sp,
    color = Color.White
)

Text(
    text = "Title Medium",
    fontSize = 20.sp,
    fontWeight = FontWeight.SemiBold,
    color = Color.White
)

// Body Text
Text(
    text = "Body Large",
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium,
    color = Color.White
)

Text(
    text = "Body Medium",
    fontSize = 14.sp,
    color = MutedGray
)

// Labels
Text(
    text = "Label",
    fontSize = 13.sp,
    fontWeight = FontWeight.Medium,
    color = MutedGray
)
```

---

## 🎴 Component Usage Examples

### SpaceBackground

```kotlin
@Composable
fun MyScreen() {
    SpaceBackground(
        showFloatingShapes = true,
        showStars = true
    ) {
        // Your screen content here
        Column(modifier = Modifier.fillMaxSize()) {
            // ...
        }
    }
}
```

### GlassCard

```kotlin
GlassCard(
    modifier = Modifier.fillMaxWidth(),
    onClick = { /* optional click handler */ }
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Card Title", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Card content", fontSize = 14.sp, color = MutedGray)
    }
}
```

### SpaceTextField

```kotlin
var text by remember { mutableStateOf("") }

SpaceTextField(
    value = text,
    onValueChange = { text = it },
    label = "Email Address",
    leadingIcon = Icons.Default.Email,
    isError = false,
    supportingText = "Enter your email",
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next
    )
)
```

### PrimaryButton

```kotlin
PrimaryButton(
    text = "Submit",
    onClick = { /* handle click */ },
    modifier = Modifier.fillMaxWidth(),
    enabled = true,
    isLoading = false,
    icon = Icons.Default.Send
)
```

### SecondaryButton

```kotlin
SecondaryButton(
    text = "Cancel",
    onClick = { /* handle click */ },
    modifier = Modifier.fillMaxWidth(),
    enabled = true,
    icon = Icons.Default.Close
)
```

### SectionHeader

```kotlin
SectionHeader(
    title = "Personal Information",
    subtitle = "Your basic details",
    action = {
        IconButton(onClick = { /* edit */ }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
    }
)
```

### InfoCard

```kotlin
InfoCard(
    icon = Icons.Default.Person,
    label = "Username",
    value = "john_doe"
)
```

### StatusChip

```kotlin
StatusChip(
    status = "Approved",
    statusType = StatusType.SUCCESS
)

StatusChip(
    status = "Pending",
    statusType = StatusType.WARNING
)

StatusChip(
    status = "Rejected",
    statusType = StatusType.ERROR
)
```

### EmptyState

```kotlin
EmptyState(
    icon = Icons.Default.Info,
    title = "No Data Found",
    description = "There are no items to display at this time.",
    actionText = "Refresh",
    onActionClick = { /* refresh data */ }
)
```

### LoadingOverlay

```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    // Your content
    MyScreenContent()
    
    // Loading overlay
    LoadingOverlay(
        isLoading = uiState.isLoading,
        message = "Loading data..."
    )
}
```

---

## 📱 Responsive Layout Patterns

### Basic Responsive Padding

```kotlin
@Composable
fun rememberResponsivePadding(): Dp {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> 16.dp
        configuration.screenWidthDp < 840 -> 24.dp
        else -> 32.dp
    }
}

// Usage
val padding = rememberResponsivePadding()
Column(modifier = Modifier.padding(horizontal = padding)) {
    // Content
}
```

### Responsive Max Width

```kotlin
@Composable
fun Modifier.responsiveMaxWidth(): Modifier {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> this.fillMaxWidth()
        configuration.screenWidthDp < 840 -> this.widthIn(max = 600.dp)
        else -> this.widthIn(max = 840.dp)
    }
}

// Usage
Card(
    modifier = Modifier
        .responsiveMaxWidth()
        .padding(16.dp)
) {
    // Content
}
```

### Responsive Grid Columns

```kotlin
@Composable
fun rememberGridColumns(): Int {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> 1
        configuration.screenWidthDp < 840 -> 2
        else -> 3
    }
}

// Usage with LazyVerticalGrid
val columns = rememberGridColumns()
LazyVerticalGrid(
    columns = GridCells.Fixed(columns),
    contentPadding = PaddingValues(16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(items) { item ->
        ItemCard(item)
    }
}
```

---

## ♿ Accessibility Checklist

### For Every Interactive Element

```kotlin
// ✅ DO: Add content description
Icon(
    imageVector = Icons.Default.Person,
    contentDescription = "User profile icon",
    modifier = Modifier.semantics {
        role = Role.Image
    }
)

// ✅ DO: Add semantic role
Card(
    onClick = { /* action */ },
    modifier = Modifier.semantics {
        role = Role.Button
        contentDescription = "View profile details"
    }
) {
    // Content
}

// ✅ DO: Mark headings
Text(
    text = "Section Title",
    modifier = Modifier.semantics {
        heading()
    }
)

// ✅ DO: Ensure minimum touch target
Button(
    onClick = { /* action */ },
    modifier = Modifier
        .heightIn(min = 48.dp)
        .widthIn(min = 48.dp)
) {
    Text("Click Me")
}
```

### Color Contrast Verification

```kotlin
// ✅ GOOD: White on DeepSpace1 (21:1 ratio)
Text(
    text = "Primary Text",
    color = Color.White
)

// ✅ GOOD: MutedGray on DeepSpace1 (7:1 ratio)
Text(
    text = "Secondary Text",
    color = MutedGray
)

// ✅ GOOD: SpaceIndigo on DeepSpace1 (4.5:1 ratio)
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = SpaceIndigo
    )
) {
    Text("Action", color = Color.White)
}
```

---

## 🎬 Animation Patterns

### Floating Animation

```kotlin
val infiniteTransition = rememberInfiniteTransition(label = "float")
val offsetY by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = -30f,
    animationSpec = infiniteRepeatable(
        animation = tween(6000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "floatY"
)

Box(
    modifier = Modifier.offset(y = offsetY.dp)
) {
    // Floating content
}
```

### Glow Animation

```kotlin
val infiniteTransition = rememberInfiniteTransition(label = "glow")
val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.6f,
    animationSpec = infiniteRepeatable(
        animation = tween(3000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "glow"
)

Text(
    text = "Glowing Text",
    modifier = Modifier.graphicsLayer {
        shadowElevation = 40f * glowAlpha
    }
)
```

### Button Hover Effect

```kotlin
var isPressed by remember { mutableStateOf(false) }
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.98f else 1f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
)

Button(
    onClick = { /* action */ },
    modifier = Modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
) {
    Text("Press Me")
}
```

### Fade In Animation

```kotlin
var visible by remember { mutableStateOf(false) }

LaunchedEffect(Unit) {
    visible = true
}

AnimatedVisibility(
    visible = visible,
    enter = fadeIn(animationSpec = tween(300)) + 
            slideInVertically(initialOffsetY = { it / 2 })
) {
    // Content to fade in
    Card { /* ... */ }
}
```

---

## 🐛 Common Pitfalls to Avoid

### ❌ DON'T: Use hardcoded colors

```kotlin
// ❌ BAD
Text(text = "Hello", color = Color(0xFF6366F1))

// ✅ GOOD
Text(text = "Hello", color = SpaceIndigo)
```

### ❌ DON'T: Use hardcoded spacing

```kotlin
// ❌ BAD
Modifier.padding(16.dp)

// ✅ GOOD
Modifier.padding(spacingM)
```

### ❌ DON'T: Forget content descriptions

```kotlin
// ❌ BAD
Icon(imageVector = Icons.Default.Person, contentDescription = null)

// ✅ GOOD
Icon(
    imageVector = Icons.Default.Person,
    contentDescription = "User profile icon"
)
```

### ❌ DON'T: Use small touch targets

```kotlin
// ❌ BAD
IconButton(
    onClick = { },
    modifier = Modifier.size(24.dp)
) { /* ... */ }

// ✅ GOOD
IconButton(
    onClick = { },
    modifier = Modifier.size(48.dp)
) { /* ... */ }
```

### ❌ DON'T: Forget loading states

```kotlin
// ❌ BAD
Button(onClick = { viewModel.submit() }) {
    Text("Submit")
}

// ✅ GOOD
Button(
    onClick = { viewModel.submit() },
    enabled = !uiState.isLoading
) {
    if (uiState.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = Color.White
        )
    } else {
        Text("Submit")
    }
}
```

### ❌ DON'T: Ignore error states

```kotlin
// ❌ BAD
LazyColumn {
    items(items) { item ->
        ItemCard(item)
    }
}

// ✅ GOOD
when {
    uiState.isLoading -> LoadingOverlay(isLoading = true)
    uiState.error != null -> EmptyState(
        icon = Icons.Default.Error,
        title = "Error",
        description = uiState.error,
        actionText = "Retry",
        onActionClick = { viewModel.retry() }
    )
    uiState.items.isEmpty() -> EmptyState(
        icon = Icons.Default.Info,
        title = "No Items",
        description = "No items to display"
    )
    else -> LazyColumn {
        items(uiState.items) { item ->
            ItemCard(item)
        }
    }
}
```

---

## 🔍 Testing Checklist

### Before Committing Code

- [ ] All colors use theme constants (no hardcoded colors)
- [ ] All spacing uses spacing constants (no hardcoded dp values)
- [ ] All interactive elements have content descriptions
- [ ] All touch targets are at least 48dp × 48dp
- [ ] Loading states are implemented
- [ ] Error states are implemented
- [ ] Empty states are implemented
- [ ] Animations run at 60fps
- [ ] Layout is responsive (test on multiple screen sizes)
- [ ] Text is readable (proper contrast)
- [ ] No horizontal scrolling on small screens
- [ ] TalkBack navigation works correctly

### Device Testing

Test on these screen widths:
- [ ] 320dp (small phones)
- [ ] 360dp (standard phones)
- [ ] 400dp (large phones)
- [ ] 600dp (tablets portrait)
- [ ] 840dp (tablets landscape)

---

## 📚 File Structure Reference

```
app/src/main/java/com/example/bootcamp/
├── ui/
│   ├── theme/
│   │   ├── Color.kt              # All color definitions
│   │   ├── Type.kt               # Typography definitions
│   │   └── Theme.kt              # Theme configuration
│   ├── components/
│   │   ├── SpaceBackground.kt    # Background component
│   │   ├── buttons/
│   │   │   ├── PrimaryButton.kt
│   │   │   └── SecondaryButton.kt
│   │   ├── cards/
│   │   │   ├── GlassCard.kt
│   │   │   └── InfoCard.kt
│   │   ├── inputs/
│   │   │   └── SpaceTextField.kt
│   │   ├── loading/
│   │   │   └── LoadingOverlay.kt
│   │   ├── EmptyState.kt
│   │   ├── SectionHeader.kt
│   │   └── StatusChip.kt
│   └── screens/
│       ├── LoginScreen.kt
│       ├── RegisterScreen.kt
│       ├── ForgotPasswordScreen.kt
│       ├── HomeScreen.kt
│       ├── UserProfileScreen.kt
│       ├── ProfileDetailsScreen.kt
│       ├── EditProfileScreen.kt
│       ├── LoanHistoryScreen.kt
│       └── SubmitLoanScreen.kt
```

---

## 🚀 Quick Start Template

```kotlin
package com.example.bootcamp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.ui.components.*
import com.example.bootcamp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNewScreen(
    onNavigateBack: () -> Unit
) {
    SpaceBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Screen Title",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigate back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    SectionHeader(
                        title = "Section Title",
                        subtitle = "Section description"
                    )
                }
                
                item {
                    GlassCard {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Card Content",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            
                            PrimaryButton(
                                text = "Action",
                                onClick = { /* handle action */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
```

---

## 💡 Pro Tips

1. **Always use theme colors** - Never hardcode color values
2. **Consistent spacing** - Use the spacing scale for all padding/margins
3. **Accessibility first** - Add content descriptions as you code, not later
4. **Test early** - Check responsive behavior on different screen sizes
5. **Reuse components** - Don't recreate what already exists
6. **Performance matters** - Use `remember` and `derivedStateOf` for expensive calculations
7. **Loading states** - Always show feedback during async operations
8. **Error handling** - Provide clear error messages and recovery actions
9. **Empty states** - Make empty screens helpful, not boring
10. **Animations** - Keep them subtle and purposeful

---

**Document Version**: 1.0  
**Last Updated**: 2026-01-29  
**Author**: Kilo Code (Architect Mode)
