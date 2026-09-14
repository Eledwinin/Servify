package com.example.servify.ui.componentes.inputs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servify.ui.theme.ServifyBorder
import com.example.servify.ui.theme.ServifyGreenPrimary
import com.example.servify.ui.theme.ServifyTextMuted
import com.example.servify.ui.theme.ServifyTextPlaceholder
import com.example.servify.ui.theme.ServifyTextTitle

@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onValorCambiado: (String) -> Unit,
    placeholder: String,
    iconoInicio: ImageVector? = null,
    esPassword: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = ServifyTextTitle
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambiado,
            textStyle = TextStyle(color = ServifyTextTitle, fontSize = 14.sp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = ServifyTextPlaceholder,
                    fontSize = 14.sp
                )
            },
            leadingIcon = iconoInicio?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = etiqueta,
                        tint = ServifyTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = if (esPassword) {
                {
                    val icono = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = icono,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            tint = ServifyTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,
            singleLine = true,
            visualTransformation = if (esPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ServifyTextTitle,
                unfocusedTextColor = ServifyTextTitle,
                focusedBorderColor = ServifyGreenPrimary,
                unfocusedBorderColor = ServifyBorder,
                cursorColor = ServifyGreenPrimary,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}