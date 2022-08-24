package com.example.composemaps.ui.search.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

sealed class ScaffoldHeaderState {

    abstract val heightDeltaDp: Dp

    data class Static(
        val content: @Composable () -> Unit,
    ) : ScaffoldHeaderState() {
        override val heightDeltaDp: Dp get() = 0.dp
    }

    data class Collapsible(
        val content: @Composable (Float) -> Unit,
        override val heightDeltaDp: Dp,
    ) : ScaffoldHeaderState()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MultistageBottomSheetScaffold(
    bottomSheetDraggedToNewStateCallback: (MultistageBottomSheetState) -> Unit,
    bottomSheetHeader: @Composable () -> Unit,
    bottomSheetHeaderHeightDp: Dp,
    bottomSheetScrollableContent: LazyListScope.() -> Unit,
    bottomSheetState: MultistageBottomSheetState,
    headerState: ScaffoldHeaderState,
    fab: @Composable () -> Unit,
    fabPosition: FabPosition,
    content: @Composable () -> Unit,
) {
    Scaffold(
        floatingActionButton = fab,
        floatingActionButtonPosition = fabPosition,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val targetHeaderDeltaDp = headerState.heightDeltaDp * bottomSheetState.expansionPercentage

        val mutableHeaderExpansionPercentage: MutableState<Float>?
        val bottomSheetHeaderState: MultistageBottomSheetHeaderState
        val header: @Composable () -> Unit
        when (headerState) {
            is ScaffoldHeaderState.Static -> {
                mutableHeaderExpansionPercentage = null
                bottomSheetHeaderState = MultistageBottomSheetHeaderState.Static
                header = { headerState.content() }
            }
            is ScaffoldHeaderState.Collapsible -> {
                mutableHeaderExpansionPercentage = remember { mutableStateOf(targetHeaderDeltaDp / headerState.heightDeltaDp) }
                bottomSheetHeaderState = MultistageBottomSheetHeaderState.Collapsible(
                    heightDeltaDp = headerState.heightDeltaDp,
                    onChangeExpansionPercentage = { mutableHeaderExpansionPercentage.value = it }
                )
                header = {
                    headerState.content(mutableHeaderExpansionPercentage.value)
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .alpha(0f)
                            .height(headerState.heightDeltaDp * (1f - mutableHeaderExpansionPercentage.value))
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(paddingValues),
        ) {

            Column(
                modifier = Modifier.zIndex(4f),
            ) {
                header()
            }

            BoxWithConstraints(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize(),
            ) {
                val currentMaxHeightDp = this@BoxWithConstraints.maxHeight
                val maxHeightWithFullHeader = remember {
                    currentMaxHeightDp + headerState.heightDeltaDp - targetHeaderDeltaDp
                }
                content()
                MultistageBottomSheet(
                    bottomSheetHeader = bottomSheetHeader,
                    bottomSheetHeaderHeightDp = bottomSheetHeaderHeightDp,
                    headerState = bottomSheetHeaderState,
                    dragToNewStateCallback = bottomSheetDraggedToNewStateCallback,
                    bodyHeightDp = maxHeightWithFullHeader,
                    scrollableBody = bottomSheetScrollableContent,
                    state = bottomSheetState,
                )
            }
        }
    }
}