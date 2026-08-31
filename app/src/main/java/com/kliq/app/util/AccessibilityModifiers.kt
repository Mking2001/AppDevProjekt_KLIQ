package com.kliq.app.util

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.accessibilityHeading(): Modifier = this.semantics {
    heading()
}

fun Modifier.talkBackDescription(
    description: String,
    role: Role? = null,
    stateDescription: String? = null
): Modifier = this.semantics {
    this.contentDescription = description
    if (role != null) {
        this.role = role
    }
    if (stateDescription != null) {
        this.stateDescription = stateDescription
    }
}

fun Modifier.ensureMinTouchTarget(minSize: Dp = 48.dp): Modifier = this.defaultMinSize(
    minWidth = minSize,
    minHeight = minSize
)
