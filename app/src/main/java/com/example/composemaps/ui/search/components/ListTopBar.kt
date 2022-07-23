package com.example.composemaps.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetHeader(
    listTopBarState: ListTopBarState,
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color = Color.LightGray)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 46.dp)
    ) {
        HideButton(
            visibilityButtonState = listTopBarState.visibilityButtonState,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

@Composable
private fun HideButton(
    visibilityButtonState: VisibilityButtonState,
    modifier: Modifier = Modifier,
) {
    when (visibilityButtonState) {
        VisibilityButtonState.Hidden -> {}
        is VisibilityButtonState.Visible -> Text(
            text = visibilityButtonState.text,
            modifier = modifier
                .clickable { visibilityButtonState.onClick() },
        )
    }
}

sealed class ListTopBarState {

    abstract val visibilityButtonState: VisibilityButtonState

    data class Open(
        val onVisibilityButtonClick: () -> Unit,
    ) : ListTopBarState() {
        override val visibilityButtonState: VisibilityButtonState
            get() = VisibilityButtonState.Visible(
                onClick = onVisibilityButtonClick,
                text = "Close",
            )
    }

    object Hidden : ListTopBarState() {
        override val visibilityButtonState: VisibilityButtonState get() = VisibilityButtonState.Hidden
    }
}

sealed class VisibilityButtonState {
    object Hidden : VisibilityButtonState()

    data class Visible(
        val onClick: () -> Unit,
        val text: String,
    ) : VisibilityButtonState()
}