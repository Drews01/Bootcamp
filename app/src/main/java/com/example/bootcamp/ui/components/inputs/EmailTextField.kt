package com.example.bootcamp.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bootcamp.ui.theme.MutedGray
import com.example.bootcamp.ui.theme.SpaceIndigo

/**
 * Reusable email text field component. Styled according to Style1 design with dark theme colors.
 *
 * @param value Current text value
 * @param onValueChange Callback for text changes
 * @param modifier Modifier for the text field
 * @param label Label text
 * @param enabled Whether the field is enabled
 * @param isError Whether the field has an error
 * @param imeAction Keyboard action
 */
@Composable
fun EmailTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        label: String = "Email",
        enabled: Boolean = true,
        isError: Boolean = false,
        imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            label = { Text(label, color = MutedGray) },
            leadingIcon = {
                Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MutedGray,
                )
            },
            keyboardOptions =
                    KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = imeAction,
                    ),
            singleLine = true,
            enabled = enabled,
            isError = isError,
            shape = RoundedCornerShape(14.dp),
            colors =
                    OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SpaceIndigo,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = SpaceIndigo,
                            focusedLabelColor = SpaceIndigo,
                            unfocusedLabelColor = MutedGray,
                            errorBorderColor = Color.Red,
                            errorLabelColor = Color.Red,
                    ),
    )
}
