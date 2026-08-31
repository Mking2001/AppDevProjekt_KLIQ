package com.kliq.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.DarkOnBackground
import com.kliq.app.ui.theme.DarkOnSurface
import com.kliq.app.ui.theme.DarkOutline
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.ErrorRed
import com.kliq.app.ui.theme.PurpleContainer
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.TealSecondary
import com.kliq.app.viewmodel.RatingSubmitStatus
import com.kliq.app.viewmodel.RatingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingBottomSheet(
    isVisible: Boolean,
    uiState: RatingUiState,
    onRatingChanged: (Int) -> Unit,
    onReviewTextChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Bewertung abgeben",
    subtitle: String = "Teile deine Erfahrung mit der Kliq-Community"
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = DarkSurface,
        scrimColor = DarkBackground.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = DarkOnBackground
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkOnSurface.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Schließen",
                        tint = DarkOnBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = getRatingLabel(uiState.rating),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = if (uiState.rating > 0) PurplePrimary else DarkOutline
            )

            Spacer(modifier = Modifier.height(8.dp))

            InteractiveStarRating(
                rating = uiState.rating,
                onRatingChanged = onRatingChanged,
                starSize = 40.dp,
                starSpacing = 12.dp,
                isReadOnly = uiState.status is RatingSubmitStatus.Submitting || uiState.status is RatingSubmitStatus.Success
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = uiState.reviewText,
                onValueChange = onReviewTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                enabled = uiState.status !is RatingSubmitStatus.Submitting && uiState.status !is RatingSubmitStatus.Success,
                placeholder = {
                    Text(
                        text = "Schreibe einen optionalen Erfahrungsbericht...",
                        color = DarkOutline
                    )
                },
                supportingText = {
                    Text(
                        text = "${uiState.reviewText.length} / ${uiState.maxTextLength} Zeichen",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = if (uiState.remainingCharacters < 20) ErrorRed else DarkOnSurface.copy(alpha = 0.6f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    disabledContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = DarkOutline,
                    focusedTextColor = DarkOnBackground,
                    unfocusedTextColor = DarkOnBackground
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = uiState.status is RatingSubmitStatus.Error,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (uiState.status is RatingSubmitStatus.Error) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ErrorRed.copy(alpha = 0.15f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Fehler",
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(
                            text = uiState.status.message,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.status is RatingSubmitStatus.Success,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TealSecondary.copy(alpha = 0.15f))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Erfolg",
                        tint = TealSecondary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Vielen Dank für deine Bewertung!",
                        color = TealSecondary,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            if (uiState.status is RatingSubmitStatus.Success) {
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealSecondary
                    )
                ) {
                    Text(
                        text = "Fertig",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = onSubmit,
                    enabled = uiState.isSubmitEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        disabledContainerColor = PurpleContainer.copy(alpha = 0.4f),
                        disabledContentColor = DarkOnSurface.copy(alpha = 0.4f)
                    )
                ) {
                    if (uiState.status is RatingSubmitStatus.Submitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = if (uiState.rating == 0) "Stern auswählen zum Absenden" else "Bewertung absenden",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

private fun getRatingLabel(rating: Int): String {
    return when (rating) {
        1 -> "1 Stern - Enttäuschend"
        2 -> "2 Sterne - Mäßig"
        3 -> "3 Sterne - Gut"
        4 -> "4 Sterne - Sehr gut"
        5 -> "5 Sterne - Hervorragend!"
        else -> "Bitte wähle eine Sterne-Bewertung (1 bis 5)"
    }
}
