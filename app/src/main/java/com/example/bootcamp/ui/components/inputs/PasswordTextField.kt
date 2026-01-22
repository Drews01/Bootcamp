package com.example.bootcamp.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bootcamp.ui.theme.MutedGray
import com.example.bootcamp.ui.theme.SpaceIndigo

/**
 * Reusable password text field component with visibility toggle. Styled according to Style1 design
 * with dark theme colors.
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
fun PasswordTextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        label: String = "Password",
        enabled: Boolean = true,
        isError: Boolean = false,
        imeAction: ImeAction = ImeAction.Done
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            label = { Text(label, color = MutedGray) },
            leadingIcon = {
                Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MutedGray,
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                            imageVector =
                                    if (passwordVisible) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                            contentDescription =
                                    if (passwordVisible) "Hide password" else "Show password",
                            tint = MutedGray,
                    )
                }
            },
            visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
            keyboardOptions =
                    KeyboardOptions(
                            keyboardType = KeyboardType.Password,
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
