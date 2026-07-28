package com.kliq.app.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileQrCodeBottomSheet(
    isVisible: Boolean,
    qrBitmap: Bitmap?,
    isGenerating: Boolean,
    displayName: String,
    username: String,
    onDismissRequest: () -> Unit,
    onScanQrCode: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val context = LocalContext.current

    // Automatische Helligkeitsanhebung auf Maximum in dunklen Club-Umgebungen
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalBrightness = activity?.window?.attributes?.screenBrightness

        activity?.window?.attributes = activity?.window?.attributes?.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }

        onDispose {
            activity?.window?.attributes = activity?.window?.attributes?.apply {
                screenBrightness = originalBrightness ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val cardBg = Color(0xFF1E1B2E)
    val accentPurple = Color(0xFF7C3AED)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = cardBg,
        contentColor = Color.White
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "QR Pass",
                        tint = accentPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mein Kliq QR-Pass",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Schließen",
                        tint = Color.Gray
                    )
                }
            }

            // Helligkeits-Boost Status Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentPurple.copy(alpha = 0.15f))
                    .border(1.dp, accentPurple.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Helligkeits-Boost",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Display-Helligkeit für Club-Scan maximiert",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // High-Contrast QR Code Display Container
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(3.dp, accentPurple, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isGenerating || qrBitmap == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = accentPurple)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Generiere Kliq QR-Code...",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.DarkGray
                        )
                    }
                } else {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Kliq Profil QR Code",
                        modifier = Modifier.size(228.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = username,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Zeige diesen verifizierten QR-Code deiner Kliq-Begleitung zum Einscannen für Sofort-Bewertungen.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onScanQrCode != null) {
                    Button(
                        onClick = {
                            onDismissRequest()
                            onScanQrCode()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Code scannen",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (onScanQrCode != null) Color.White.copy(alpha = 0.15f) else accentPurple
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Schließen",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
