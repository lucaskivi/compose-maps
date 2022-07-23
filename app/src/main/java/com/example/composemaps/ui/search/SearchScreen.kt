package com.example.composemaps.ui.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composemaps.R
import com.example.composemaps.ui.search.components.AnchoredBottomSheet
import com.example.composemaps.ui.search.components.BottomSheetHeader
import com.example.composemaps.ui.search.components.BottomSheetState
import com.example.composemaps.ui.search.components.ListTopBarState
import com.example.composemaps.ui.search.components.SearchFloatingActionButton
import com.example.composemaps.ui.search.components.SearchList
import com.example.composemaps.ui.search.components.SearchListContent
import com.example.composemaps.ui.search.components.SearchListData
import com.example.composemaps.ui.search.components.SearchMap
import com.example.composemaps.ui.search.components.SearchMapContent
import com.example.composemaps.ui.search.components.SearchMapData
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchScreenContent by viewModel.searchScreenContent.collectAsState(initial = SearchScreenContent.DEFAULT)

    Scaffold(
        floatingActionButton = {
            if (searchScreenContent.searchScreenState !is SearchScreenState.HalfMap) {
                SearchFloatingActionButton(
                    textRes = searchScreenContent.searchScreenState.fabText,
                    iconRes = searchScreenContent.searchScreenState.iconRes,
                    onClick = viewModel::fabClicked,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        BoxWithConstraints(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            val maxHeight = with(LocalDensity.current) {
                this@BoxWithConstraints.maxHeight.toPx()
            }
            SearchMap(
                paddingValues = paddingValues,
                searchMapContent = SearchMapContent(
                    data = searchScreenContent.mapData,
                )
            )
            AnchoredBottomSheet(
                state = searchScreenContent.searchScreenState.bottomSheetState,
                dragToNewStateCallback = viewModel::onDragToNewState,
                maxHeight = maxHeight,
            ) {
                BottomSheetHeader(
                    listTopBarState = searchScreenContent.searchScreenState.listTopBarState,
                )
                SearchList(
                    searchListContent = SearchListContent(
                        data = searchScreenContent.listData,
                        bottomSheetState = searchScreenContent.searchScreenState.bottomSheetState,
                        listTopBarState = searchScreenContent.searchScreenState.listTopBarState,
                    ),
                    modifier = Modifier
                        .clickable { Log.d("DAVID", "clicked") }
                        .fillMaxSize()
                        .background(Color.White),
                )
            }
        }
    }
}

@Composable
fun SearchHeader() {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .wrapContentHeight()
            .padding(start = 16.dp)
    ) {
        Text(
            text = "Compose Map",
            fontSize = 32.sp,
        )
        Text(
            text = "Using google maps.",
            fontSize = 16.sp,
        )
    }
}

/**
 * A total state object for the search screen.
 */
data class SearchScreenContent(
    val listData: SearchListData,
    val mapData: SearchMapData,
    val searchScreenState: SearchScreenState,
) {
    companion object {
        val DEFAULT = SearchScreenContent(
            listData = SearchListData.Loading,
            mapData = SearchMapData.Loading,
            searchScreenState = SearchScreenState.FullMap(
                onVisibilityButtonClick = {},
            ),
        )
    }
}

/**
 * A sealed class to represent search screen UI states.
 */
sealed class SearchScreenState {

    abstract val fabText: Int

    abstract val iconRes: Int

    abstract val bottomSheetState: BottomSheetState

    abstract val listTopBarState: ListTopBarState

    data class FullMap(
        val onVisibilityButtonClick: () -> Unit,
    ) : SearchScreenState() {
        override val fabText: Int get() = R.string.search_fab_list
        override val iconRes: Int get() = R.drawable.ic_list
        override val bottomSheetState: BottomSheetState get() = BottomSheetState.Gone
        override val listTopBarState: ListTopBarState get() = ListTopBarState.Hidden
    }

    object FullList : SearchScreenState() {
        override val fabText: Int get() = R.string.search_fab_map
        override val iconRes: Int get() = R.drawable.ic_map
        override val bottomSheetState: BottomSheetState get() = BottomSheetState.Full
        override val listTopBarState: ListTopBarState get() = ListTopBarState.Hidden
    }

    data class HalfMap(
        val onVisibilityButtonClick: () -> Unit,
    ) : SearchScreenState() {
        override val bottomSheetState: BottomSheetState get() = BottomSheetState.Half
        override val fabText: Int get() = R.string.search_fab_list
        override val iconRes: Int get() = R.drawable.ic_list
        override val listTopBarState: ListTopBarState = ListTopBarState.Open(
            onVisibilityButtonClick = onVisibilityButtonClick,
        )
    }
}
