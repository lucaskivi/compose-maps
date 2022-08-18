package com.example.composemaps.ui.search.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun SearchFab(
    searchFabState: SearchFabState,
    onClick: () -> Unit,
) {
    AnimatedVisibility(visible = searchFabState.isVisible, enter = fadeIn(), exit = fadeOut()) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    painter = painterResource(id = searchFabState.iconRes),
                    contentDescription = null,
                )
            },
            text = {
                Text(
                    text = stringResource(id = searchFabState.textRes),
                )
            },
            onClick = onClick,
        )
    }
}

data class SearchFabState(
    val iconRes: Int,
    val isVisible: Boolean,
    val textRes: Int,
)