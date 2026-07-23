package com.kliq.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

@Composable
fun ProfileAvatarImage(
    imageUri: String?,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 110.dp,
    isProcessing: Boolean = false,
    initials: String? = null,
    showCameraBadge: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .clickable { onAvatarClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PurplePrimaryLight,
                            FuchsiaTertiary,
                            PurplePrimary
                        )
                    ),
                    shape = CircleShape
                )
                .padding(4.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!imageUri.isNullGlanceable()) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Profilbild",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (!initials.isNullOrBlank()) {
                Text(
                    text = initials.take(2).uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = (size.value * 0.35).sp,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimaryLight
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profilbild Platzhalter",
                    tint = PurplePrimaryLight.copy(alpha = 0.7f),
                    modifier = Modifier.size(size * 0.45f)
                )
            }

            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkSurface.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = PurplePrimaryLight,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(size * 0.3f)
                    )
                }
            }
        }

        if (showCameraBadge && !isProcessing) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-2).dp, y = (-2).dp)
                    .size(size * 0.32f)
                    .clip(CircleShape)
                    .background(PurplePrimary)
                    .border(2.dp, DarkSurface, CircleShape)
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (imageUri.isNullGlanceable()) Icons.Default.AddAPhoto else Icons.Default.CameraAlt,
                    contentDescription = "Foto ändern",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.16f)
                )
            }
        }
    }
}

private fun String?.isNullGlanceable(): Boolean {
    return this.isNullOrBlank() || this == "null"
}
