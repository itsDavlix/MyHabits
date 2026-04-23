package com.example.myhabits.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhabits.ui.theme.MyHabitsTheme

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: RegistrationViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "REGISTRO",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color(0xFFD4FF00),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "EMPIEZA TU CAMINO HOY",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("NOMBRE COMPLETO", color = Color.White.copy(alpha = 0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFD4FF00),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                isError = uiState.nameError != null,
                supportingText = { uiState.nameError?.let { Text(it, color = Color.Red) } },
                leadingIcon = { Text("👤", modifier = Modifier.padding(start = 12.dp)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("CORREO ELECTRÓNICO", color = Color.White.copy(alpha = 0.4f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            viewModel.validateEmailOnBlur()
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFD4FF00),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                ),
                isError = uiState.emailError != null,
                supportingText = { uiState.emailError?.let { Text(it, color = Color.Red) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Text("📧", modifier = Modifier.padding(start = 12.dp)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.alias,
                onValueChange = { viewModel.onAliasChange(it) },
                label = { Text("ALIAS / USUARIO", color = Color.White.copy(alpha = 0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFD4FF00),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                ),
                isError = uiState.aliasError != null,
                supportingText = { uiState.aliasError?.let { Text(it, color = Color.Red) } },
                leadingIcon = { Text("🆔", modifier = Modifier.padding(start = 12.dp)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("CONTRASEÑA", color = Color.White.copy(alpha = 0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFD4FF00),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                ),
                isError = uiState.passwordError != null,
                supportingText = { uiState.passwordError?.let { Text(it, color = Color.Red) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Text("🔒", modifier = Modifier.padding(start = 12.dp)) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.registerUser(onRegistrationSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4FF00),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "CREAR CUENTA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(onClick = onBackToLogin) {
                Text(
                    text = "¿YA TIENES CUENTA? INICIA SESIÓN",
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    MyHabitsTheme {
        RegistrationScreen(onRegistrationSuccess = {}, onBackToLogin = {})
    }
}
