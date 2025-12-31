package com.egytour.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egytour.app.R
import com.egytour.app.ui.theme.EgytourTheme
import com.egytour.app.ui.theme.BrandOrange

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        backgroundColor = EgytourTheme.colors.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .offset(x = (-100).dp, y = (-100).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(BrandOrange.copy(alpha = 0.1f), Color.Transparent),
                            radius = 200f
                        ),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = 200.dp, y = 100.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(BrandBlack.copy(alpha = 0.1f), Color.Transparent),
                            radius = 150f
                        ),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Handle back press */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                }
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.h6,
                    color = BrandOrange
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
            Spacer(modifier = Modifier.height(48.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.explore),
                    style = MaterialTheme.typography.h3
                )
                Text(
                    text = stringResource(R.string.the_unseen),
                    style = MaterialTheme.typography.h3,
                    color = BrandOrange
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.unlock_exclusive_tours),
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    TabRow(
                        selectedTabIndex = 0,
                        backgroundColor = Color.Transparent,
                        contentColor = BrandOrange,
                        divider = {}
                    ) {
                        Tab(
                            selected = true,
                            onClick = { /* Handle login tab select */ },
                            text = { Text(stringResource(R.string.log_in)) }
                        )
                        Tab(
                            selected = false,
                            onClick = { /* Handle register tab select */ },
                            text = { Text(stringResource(R.string.register)) }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(R.string.email_address)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.password)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(image, contentDescription = stringResource(R.string.toggle_password_visibility))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { /* Handle forgot password */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.forgot_password))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(BrandOrange, BrandDarkOrange)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { /* Handle login */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.access_account),
                                style = MaterialTheme.typography.button,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Divider(modifier = Modifier.weight(1f))
                        Text(
                            text = stringResource(R.string.or_continue_with),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.overline,
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                        )
                        Divider(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* Handle Google login */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(stringResource(R.string.google))
                        }
                        OutlinedButton(
                            onClick = { /* Handle Apple login */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(stringResource(R.string.apple))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { /* Handle create account */ }) {
                Row {
                    Text(
                        text = stringResource(R.string.not_a_member),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.create_account),
                        textAlign = TextAlign.Center,
                        color = BrandOrange
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    EgytourTheme {
        LoginScreen()
    }
}
