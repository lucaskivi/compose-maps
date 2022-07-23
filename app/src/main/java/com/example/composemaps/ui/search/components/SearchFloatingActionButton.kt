package com.example.composemaps.ui.search.components

import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun SearchFloatingActionButton(
    textRes: Int,
    iconRes: Int,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        icon = {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
            )
        },
        text = {
            Text(
                text = stringResource(id = textRes),
            )
        },
        onClick = onClick,
    )
}