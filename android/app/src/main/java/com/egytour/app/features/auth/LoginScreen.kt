package com.egytour.app.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egytour.app.core.theme.EgytourColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EgytourColors.BgPrimary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Text(
            text = "EgyTour",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = EgytourColors.PrimaryGold,
            style = MaterialTheme.typography.displayLarge
        )
        
        Text(
            text = "Your Pocket Guide to the Pharaohs",
            style = MaterialTheme.typography.bodyMedium,
            color = EgytourColors.TextSecondary,
            modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
        )
        
        // Form
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EgytourColors.PrimaryGold,
                unfocusedBorderColor = EgytourColors.Border,
                focusedTextColor = EgytourColors.TextPrimary,
                unfocusedTextColor = EgytourColors.TextPrimary
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EgytourColors.PrimaryGold,
                unfocusedBorderColor = EgytourColors.Border,
                focusedTextColor = EgytourColors.TextPrimary,
                unfocusedTextColor = EgytourColors.TextPrimary
            )
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    delay(1500) // Simulate API
                    isLoading = false
                    onLoginSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EgytourColors.PrimaryGold,
                contentColor = EgytourColors.BgPrimary
            ),
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = EgytourColors.BgPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Sign In",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        TextButton(onClick = onLoginSuccess) {
            Text("Skip for Demo", color = EgytourColors.TextTertiary)
        }
    }
}
