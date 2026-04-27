package com.example.myhabits.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myhabits.ui.theme.DarkSurface
import com.example.myhabits.ui.theme.DeepBlack
import com.example.myhabits.ui.theme.EnergyLime
import com.example.myhabits.ui.theme.MyHabitsTheme

import androidx.compose.ui.res.painterResource
import com.example.myhabits.R
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Brush
import com.example.myhabits.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, 
    onNavigateToRegistration: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BrandDark, BrandDarkGreen)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = BrandGreen.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(32.dp),
            color = CardGray,
            border = BorderStroke(1.dp, SoftWhite.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp, horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Image
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(24.dp))
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "MYHABITS",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = SoftWhite,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "TU PROGRESO, UN HÁBITO A LA VEZ",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = BrandCyan,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo Electrónico", color = SoftWhite.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onPreviewKeyEvent { 
                            if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
                                focusManager.moveFocus(FocusDirection.Down)
                                true
                            } else if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                                focusManager.moveFocus(FocusDirection.Down)
                                true
                            } else {
                                false
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = BrandGreen,
                        unfocusedBorderColor = SoftWhite.copy(alpha = 0.1f),
                        cursorColor = BrandGreen
                    ),
                    isError = uiState.error != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña", color = SoftWhite.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onPreviewKeyEvent {
                            if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                                viewModel.login(onLoginSuccess)
                                true
                            } else {
                                false
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedBorderColor = BrandGreen,
                        unfocusedBorderColor = SoftWhite.copy(alpha = 0.1f),
                        cursorColor = BrandGreen
                    ),
                    isError = uiState.error != null,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.login(onLoginSuccess) }
                    ),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = SoftWhite.copy(alpha = 0.4f))
                        }
                    }
                )

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { viewModel.login(onLoginSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandDark
                    )
                ) {
                    Text(
                        text = "INICIAR SESIÓN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToRegistration) {
                    Text(
                        text = "¿NO TIENES CUENTA? REGÍSTRATE",
                        color = BrandCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MyHabitsTheme {
        LoginScreen(onLoginSuccess = {}, onNavigateToRegistration = {})
    }
}
