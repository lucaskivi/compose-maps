package com.example.composemaps.ui.search.components

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.Surface
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.ThresholdConfig
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

sealed class BottomSheetState : Parcelable {
    abstract val key: Int
    abstract val percentOffset: Float

    fun toAnchor(height: Float): Pair<Float, BottomSheetState> = percentOffset * height to this

    @Parcelize
    object Full : BottomSheetState() {
        override val key: Int get() = 0
        override val percentOffset: Float get() = 1f
    }

    @Parcelize
    object Gone : BottomSheetState() {
        override val key: Int get() = 1
        override val percentOffset: Float get() = 0f
    }

    @Parcelize
    object Half : BottomSheetState() {
        override val key: Int get() = 2
        override val percentOffset: Float get() = .5f
    }
}

@ExperimentalMaterialApi
@Composable
fun AnchoredBottomSheet(
    dragToNewStateCallback: (BottomSheetState) -> Unit,
    state: BottomSheetState,
    maxHeight: Float,
    content: @Composable () -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = state)

    TargetStateChangeCallback(
        swipeableState = swipeableState,
        callback = dragToNewStateCallback,
    )
    AnimateToStateOnChange(
        state = state,
        swipeableState = swipeableState,
    )

    val anchors = mapOf(
        BottomSheetState.Gone.toAnchor(maxHeight),
        BottomSheetState.Half.toAnchor(maxHeight),
        BottomSheetState.Full.toAnchor(maxHeight),
    )

    Column(
        modifier = Modifier
            .swipeable(
                enabled = swipeableState.currentValue != BottomSheetState.Gone,
                resistance = ResistanceConfig(
                    basis = SwipeableDefaults.StiffResistanceFactor,
                    factorAtMin = SwipeableDefaults.StiffResistanceFactor,
                    factorAtMax = SwipeableDefaults.StiffResistanceFactor,
                ),
                thresholds = { _, _ -> FractionalThreshold(fraction = 0.5f) },
                reverseDirection = true,
                state = swipeableState,
                orientation = Orientation.Vertical,
                anchors = anchors,
            )
            .height(
                with(LocalDensity.current) {
                    swipeableState.offset.value.toDp()
                }
            )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> AnimateToStateOnChange(
    state: T,
    swipeableState: SwipeableState<T>,
) = LaunchedEffect(key1 = state) {
    swipeableState.animateTo(state)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> TargetStateChangeCallback(
    swipeableState: SwipeableState<T>,
    callback: (T) -> Unit,
) = LaunchedEffect(key1 = swipeableState.targetValue) {
    callback(swipeableState.targetValue)
}