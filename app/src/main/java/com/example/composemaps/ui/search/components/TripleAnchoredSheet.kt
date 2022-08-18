package com.example.composemaps.ui.search.components

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

/**
 * Todo:
 * * bug: If you catch the sheet while it is animating the bottom sheet catches the click and you cannot scroll the list
 * * bug: configuration change fab is absolutely fucked
 */


sealed class BottomSheetState : Parcelable {
    abstract val percentOffset: Float

    /**
     * Get the additional additive offset that is required for the given [BottomSheetState].
     *
     *  The [LazyColumn] is the size of the parent composable. Therefore the full bottom sheet must be larger than the
     *  parent. The additional height comes from the draggable bar which is of size [bottomSheetBarPx] and the
     *  collapsible subheader which is of size [collapsibleSubheaderPx].
     *
     *  It is important to know that this height overflow is split between the bottom and top of the parent. Therefore,
     *  the [Gone] state must be offset by the size of the entire bottom sheet minus half of any other height bearing
     *  components (in this case the collapsible subheader and bottom sheet bar.
     */
    abstract fun calculateAdditionalOffset(
        bottomSheetBarPx: Float,
        collapsibleSubheaderPx: Float,
    ): Float

    fun toAnchor(
        maxHeightPx: Float,
        bottomSheetBarPx: Float,
        collapsibleSubheaderPx: Float,
    ): Pair<Float, BottomSheetState> = this.getOffset(
        bottomSheetBarPx = bottomSheetBarPx,
        collapsibleSubheaderPx = collapsibleSubheaderPx,
        maxHeightPx = maxHeightPx,
    ) to this

    /**
     * Get the offset for the anchor of the given state.
     */
    fun getOffset(
        bottomSheetBarPx: Float,
        collapsibleSubheaderPx: Float,
        maxHeightPx: Float,
    ): Float = maxHeightPx * percentOffset + this.calculateAdditionalOffset(
        bottomSheetBarPx = bottomSheetBarPx,
        collapsibleSubheaderPx = collapsibleSubheaderPx,
    )

    @Parcelize
    object Fuller : BottomSheetState() {
        override val percentOffset: Float get() = 0f
        override fun calculateAdditionalOffset(
            bottomSheetBarPx: Float,
            collapsibleSubheaderPx: Float,
        ): Float = -bottomSheetBarPx / 2f - collapsibleSubheaderPx / 2f
    }

    @Parcelize
    object Full : BottomSheetState() {
        override val percentOffset: Float get() = 0f
        override fun calculateAdditionalOffset(
            bottomSheetBarPx: Float,
            collapsibleSubheaderPx: Float,
        ): Float = -bottomSheetBarPx / 2f + collapsibleSubheaderPx / 2f
    }

    @Parcelize
    object Gone : BottomSheetState() {
        override val percentOffset: Float get() = 1f
        override fun calculateAdditionalOffset(
            bottomSheetBarPx: Float,
            collapsibleSubheaderPx: Float,
        ): Float = -bottomSheetBarPx / 2f - collapsibleSubheaderPx / 2f
    }

    @Parcelize
    object Half : BottomSheetState() {
        override val percentOffset: Float get() = .5f
        override fun calculateAdditionalOffset(
            bottomSheetBarPx: Float,
            collapsibleSubheaderPx: Float,
        ) = 0f
    }

    companion object {
        fun getAnchors(
            bottomSheetBarPx: Float,
            collapsibleSubheaderPx: Float,
            maxHeightPx: Float,
        ): Map<Float, BottomSheetState> = mapOf(
            Gone.toAnchor(
                bottomSheetBarPx = bottomSheetBarPx,
                collapsibleSubheaderPx = collapsibleSubheaderPx,
                maxHeightPx = maxHeightPx,
            ),
            Half.toAnchor(
                bottomSheetBarPx = bottomSheetBarPx,
                collapsibleSubheaderPx = collapsibleSubheaderPx,
                maxHeightPx = maxHeightPx,
            ),
            Full.toAnchor(
                bottomSheetBarPx = bottomSheetBarPx,
                collapsibleSubheaderPx = collapsibleSubheaderPx,
                maxHeightPx = maxHeightPx,
            ),
            Fuller.toAnchor(
                bottomSheetBarPx = bottomSheetBarPx,
                collapsibleSubheaderPx = collapsibleSubheaderPx,
                maxHeightPx = maxHeightPx,
            ),
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun TripleAnchoredSheet(
    bottomSheetHeader: @Composable () -> Unit,
    bottomSheetHeaderHeightDp: Dp,
    dragToNewStateCallback: (BottomSheetState) -> Unit,
    scrollableBody: LazyListScope.() -> Unit,
    state: BottomSheetState,
    collapsibleSubheaderDp: Dp,
    maxHeightWithFullSubheaderDp: Dp,
    onScrollPastFull: (Dp) -> Unit,
) {
    // Setup remembered state
    val swipeableState = rememberSwipeableState(initialValue = state)
    val lazyListState = rememberLazyListState()
    val bottomListInteractionSource = remember { MutableInteractionSource() }
    val nestedScrollConnection = remember {
        BottomSheetNestedScrollConnection(
            lazyListState = lazyListState,
            swipeableState = swipeableState,
        )
    }

    // Setup required values
    val bottomSheetMaxHeightDp = maxHeightWithFullSubheaderDp + bottomSheetHeaderHeightDp + collapsibleSubheaderDp
    val fullOffset = BottomSheetState.Full.getOffset(
        bottomSheetBarPx = bottomSheetHeaderHeightDp.toPx(),
        collapsibleSubheaderPx = collapsibleSubheaderDp.toPx(),
        maxHeightPx = bottomSheetMaxHeightDp.toPx(),
    )

    // Setup animation on state change
    LaunchedEffect(key1 = state) {
        swipeableState.animateTo(state)
    }

    // Setup callbacks
    OverscrollCallback(
        collapsibleSubheaderDp = collapsibleSubheaderDp,
        fullOffset = fullOffset,
        swipeableState = swipeableState,
    ) { overscrollDp ->
        onScrollPastFull(overscrollDp)
    }
    TargetStateChangeCallback(
        interactionSource = bottomListInteractionSource,
        lazyListInteractionSource = lazyListState.interactionSource,
        swipeableState = swipeableState,
    ) { bottomSheetState ->
        dragToNewStateCallback(bottomSheetState)
    }


    Column(
        modifier = Modifier
            .requiredHeight(bottomSheetMaxHeightDp)
            .offset { swipeableState.offset.value.toIntOffset() }
            .swipeable(
                interactionSource = bottomListInteractionSource,
                resistance = ResistanceConfig,
                thresholds = { _, _ -> FractionalThreshold(fraction = 0.5f) },
                state = swipeableState,
                orientation = Orientation.Vertical,
                anchors = BottomSheetState.getAnchors(
                    bottomSheetBarPx = bottomSheetHeaderHeightDp.toPx(),
                    collapsibleSubheaderPx = collapsibleSubheaderDp.toPx(),
                    maxHeightPx = bottomSheetMaxHeightDp.toPx(),
                ),
            )
            .nestedScroll(nestedScrollConnection)
    ) {
        bottomSheetHeader()
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .background(color = Color.White)
                .requiredHeight(maxHeightWithFullSubheaderDp + collapsibleSubheaderDp)
        ) {
            scrollableBody.invoke(this)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private class BottomSheetNestedScrollConnection(
    val lazyListState: LazyListState,
    val swipeableState: SwipeableState<BottomSheetState>,
) : NestedScrollConnection {

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset = if (available.y < 0) {
        swipeableState.performDrag(available.y).toOffset()
    } else {
        Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = swipeableState.performDrag(available.y).toOffset()

    override suspend fun onPreFling(available: Velocity): Velocity = if (
        available.y < 0 && lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
    ) {
        swipeableState.performFling(velocity = available.y)
        available
    } else {
        Velocity.Zero
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        swipeableState.performFling(velocity = available.y)
        return super.onPostFling(consumed, available)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TargetStateChangeCallback(
    interactionSource: InteractionSource,
    lazyListInteractionSource: InteractionSource,
    swipeableState: SwipeableState<BottomSheetState>,
    callback: (BottomSheetState) -> Unit,
) {
    val isBottomSheetDragged by interactionSource.collectIsDraggedAsState()
    val isLazyListDragged by lazyListInteractionSource.collectIsDraggedAsState()

    Log.d("DAVID", "${swipeableState.targetValue} & ${swipeableState.currentValue}")

    if (isBottomSheetDragged.not() && isLazyListDragged.not()) {
        callback(swipeableState.targetValue)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OverscrollCallback(
    collapsibleSubheaderDp: Dp,
    fullOffset: Float,
    swipeableState: SwipeableState<BottomSheetState>,
    onOverscroll: (Dp) -> Unit,
) {
    val density = LocalDensity.current

    LaunchedEffect(key1 = swipeableState, key2 = fullOffset) {
        snapshotFlow { swipeableState.offset.value }.collect {
            if (swipeableState.offset.value < fullOffset) {
                onOverscroll(collapsibleSubheaderDp - (fullOffset + -swipeableState.offset.value).toDp(density))
            } else {
                onOverscroll(collapsibleSubheaderDp)
            }
        }
    }
}

private val ResistanceConfig = ResistanceConfig(
    basis = SwipeableDefaults.StiffResistanceFactor,
    factorAtMin = SwipeableDefaults.StiffResistanceFactor,
    factorAtMax = SwipeableDefaults.StiffResistanceFactor,
)

private fun Float.toOffset() = Offset(
    x = 0f,
    y = this,
)

private fun Float.toIntOffset() = IntOffset(
    x = 0,
    y = this.roundToInt(),
)

@Composable
fun Float.toDp() = with(LocalDensity.current) {
    this@toDp.toDp()
}

@Composable
fun Dp.toPx() = with(LocalDensity.current) {
    this@toPx.toPx()
}

fun Float.toDp(density: Density) = with(density) { this@toDp.toDp() }

// mapOf(
// maxHeightPx - bottomSheetBarPx / 2f - collapsibleSubheaderPx / 2f to Gone,
// maxHeightPx / 2f to Half,
// 0f + bottomSheetBarPx / 2f + collapsibleSubheaderPx / 2f - bottomSheetBarPx to Full,
// 0f + bottomSheetBarPx / 2f + collapsibleSubheaderPx / 2f - collapsibleSubheaderPx - bottomSheetBarPx to Fuller,
// )