package com.egytour.app.features.checkout.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egytour.app.R
import com.egytour.app.core.theme.EgytourColors

enum class PaymentMethodType {
    CREDIT_CARD,
    GOOGLE_PAY,
    MOBILE_WALLET
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    passTitle: String = "Giza Necropolis & Plateau",
    passSubtitle: String = "Full Monument & Audio Pass",
    priceEgp: Double = 450.00,
    onBack: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.CREDIT_CARD) }
    var cardNumber by remember { mutableStateOf("4111 2222 3333 4444") }
    var expiryDate by remember { mutableStateOf("12/28") }
    var cvv by remember { mutableStateOf("888") }
    var cardholderName by remember { mutableStateOf("Omar Sherif") }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = EgytourColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EgytourColors.BgPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                color = EgytourColors.BgPrimary,
                border = androidx.compose.foundation.BorderStroke(1.dp, EgytourColors.Border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            isProcessing = true
                            onPaymentSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EgytourColors.PrimaryGold
                        )
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Pay EGP ${String.format("%.2f", priceEgp)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "By clicking Pay, you agree to our Terms of Service & monument entry rules.",
                        fontSize = 11.sp,
                        color = EgytourColors.TextTertiary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        containerColor = EgytourColors.BgPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Order Summary Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EgytourColors.BgSecondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EgytourColors.Border, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "ORDER SUMMARY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EgytourColors.PrimaryGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            passTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EgytourColors.TextPrimary
                        )
                        Text(
                            passSubtitle,
                            fontSize = 13.sp,
                            color = EgytourColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "EGP ${String.format("%.2f", priceEgp)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EgytourColors.PrimaryGold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Image(
                        painter = painterResource(id = R.drawable.giza_pyramids_purchase),
                        contentDescription = "Monuments pass preview",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Payment Methods Selection
            Text(
                "Payment Method",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EgytourColors.TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PaymentMethodOption(
                    title = "Credit Card",
                    subtitle = "Visa, Mastercard, Meeza",
                    icon = Icons.Default.CreditCard,
                    isSelected = selectedMethod == PaymentMethodType.CREDIT_CARD,
                    onClick = { selectedMethod = PaymentMethodType.CREDIT_CARD }
                )

                PaymentMethodOption(
                    title = "Google Pay",
                    subtitle = "Fast & secure one-tap payment",
                    icon = Icons.Default.PhoneAndroid,
                    isSelected = selectedMethod == PaymentMethodType.GOOGLE_PAY,
                    onClick = { selectedMethod = PaymentMethodType.GOOGLE_PAY }
                )

                PaymentMethodOption(
                    title = "Mobile Wallet",
                    subtitle = "Vodafone Cash, Orange, Etisalat Cash",
                    icon = Icons.Default.AccountBalanceWallet,
                    isSelected = selectedMethod == PaymentMethodType.MOBILE_WALLET,
                    onClick = { selectedMethod = PaymentMethodType.MOBILE_WALLET }
                )
            }

            // Card Details Form (displayed when credit card is selected)
            if (selectedMethod == PaymentMethodType.CREDIT_CARD) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Card Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgytourColors.TextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "SSL Encrypted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Card Number") },
                    trailingIcon = { Icon(Icons.Default.CreditCard, null, tint = EgytourColors.TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EgytourColors.PrimaryGold,
                        unfocusedBorderColor = EgytourColors.Border,
                        focusedLabelColor = EgytourColors.PrimaryGold
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("Expiry (MM/YY)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = EgytourColors.PrimaryGold,
                            unfocusedBorderColor = EgytourColors.Border,
                            focusedLabelColor = EgytourColors.PrimaryGold
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it },
                        label = { Text("CVV") },
                        trailingIcon = { Icon(Icons.Default.Lock, null, tint = EgytourColors.TextSecondary) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = EgytourColors.PrimaryGold,
                            unfocusedBorderColor = EgytourColors.Border,
                            focusedLabelColor = EgytourColors.PrimaryGold
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = cardholderName,
                    onValueChange = { cardholderName = it },
                    label = { Text("Cardholder Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EgytourColors.PrimaryGold,
                        unfocusedBorderColor = EgytourColors.Border,
                        focusedLabelColor = EgytourColors.PrimaryGold
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    }
}

@Composable
fun PaymentMethodOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = EgytourColors.BgSecondary,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) EgytourColors.PrimaryGold else EgytourColors.Border
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) EgytourColors.PrimaryGold else EgytourColors.TextSecondary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EgytourColors.TextPrimary
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = EgytourColors.TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) EgytourColors.PrimaryGold else Color.Transparent
                    )
                    .border(
                        1.5.dp,
                        if (isSelected) EgytourColors.PrimaryGold else EgytourColors.TextSecondary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
