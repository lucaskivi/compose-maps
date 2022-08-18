package com.example.composemaps.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composemaps.R
import com.example.composemaps.ui.search.components.BottomSheetState
import com.example.composemaps.ui.search.components.SearchFab
import com.example.composemaps.ui.search.components.SearchFabState
import com.example.composemaps.ui.search.components.SearchListContent
import com.example.composemaps.ui.search.components.SearchListData
import com.example.composemaps.ui.search.components.SearchMap
import com.example.composemaps.ui.search.components.SearchMapContent
import com.example.composemaps.ui.search.components.SearchMapData
import com.example.composemaps.ui.search.components.TripleAnchoredSheet
import com.example.composemaps.ui.search.components.searchListItems

private val SubheaderHeightDp = 56.dp
private val BottomSheetBarHeightDp = 40.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchScreenContent by viewModel.searchScreenContent.collectAsState(initial = SearchScreenContent.DEFAULT)
    val mutableSubheaderHeight = remember { mutableStateOf(searchScreenContent.searchScreenState.subHeaderHeightDp) }

    Scaffold(
        floatingActionButton = {
            SearchFab(
                searchFabState = searchScreenContent.searchScreenState.searchFabState,
                onClick = viewModel::fabClicked,
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->

        Column {
            SearchHeader(
                subHeaderHeight = mutableSubheaderHeight.value,
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(SubheaderHeightDp - mutableSubheaderHeight.value)
            )
            BoxWithConstraints(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                val currentMaxHeightDp = this@BoxWithConstraints.maxHeight
                val maxHeightWithFullSubheader = remember {
                    currentMaxHeightDp + SubheaderHeightDp - searchScreenContent.searchScreenState.subHeaderHeightDp
                }

                SearchMap(
                    paddingValues = paddingValues,
                    searchMapContent = SearchMapContent(
                        data = searchScreenContent.mapData,
                    )
                )

                TripleAnchoredSheet(
                    bottomSheetHeader = {
                        BottomSheetHeader(modifier = Modifier.height(BottomSheetBarHeightDp))
                    },
                    bottomSheetHeaderHeightDp = BottomSheetBarHeightDp,
                    state = searchScreenContent.searchScreenState.bottomSheetState,
                    dragToNewStateCallback = viewModel::onDragToNewState,
                    maxHeightWithFullSubheaderDp = maxHeightWithFullSubheader,
                    collapsibleSubheaderDp = SubheaderHeightDp,
                    scrollableBody = {
                        searchListItems(
                            searchListContent = SearchListContent(
                                data = searchScreenContent.listData,
                                bottomSheetState = searchScreenContent.searchScreenState.bottomSheetState,
                            )
                        )
                    },
                ) { offsetY ->
                    mutableSubheaderHeight.value = offsetY
                }
            }
        }
    }
}

@Composable
fun BottomSheetHeader(
    modifier: Modifier,
) {
    val roundedCornerShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.LightGray,
                shape = roundedCornerShape
            )
            .shadow(
                elevation = 32.dp,
                shape = roundedCornerShape,
            ),
    ) {
        Spacer(
            modifier = Modifier
                .size(
                    width = 60.dp,
                    height = 6.dp
                )
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(100.dp)
                )
        )
    }
}

@Composable
fun SearchHeader(
    subHeaderHeight: Dp,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .zIndex(3f)
            .background(color = Color.Gray)
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(96.dp),
        ) {
            Text(
                text = "Compose Map",
                fontSize = 32.sp,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(subHeaderHeight),
        ) {
            Text(
                text = "Using google maps.",
                fontSize = 16.sp,
            )
        }
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
            searchScreenState = SearchScreenState.FullMap
        )
    }
}

/**
 * A sealed class to represent search screen UI states.
 */
sealed class SearchScreenState {

    abstract val bottomSheetState: BottomSheetState

    abstract val searchFabState: SearchFabState

    abstract val subHeaderHeightDp: Dp

    object FullMap : SearchScreenState() {
        override val bottomSheetState: BottomSheetState get() = BottomSheetState.Gone
        override val searchFabState: SearchFabState
            get() = SearchFabState(
                iconRes = R.drawable.ic_list,
                isVisible = true,
                textRes = R.string.search_fab_list,
            )
        override val subHeaderHeightDp: Dp get() = SubheaderHeightDp
    }

    object FullerList : SearchScreenState() {
        override val bottomSheetState: BottomSheetState get() = BottomSheetState.Fuller
        override val searchFabState: SearchFabState
            get() = SearchFabState(
                iconRes = R.drawable.ic_map,
                isVisible = true,
                textRes = R.string.search_fab_map,
            )
        override val subHeaderHeightDp: Dp get() = 0.dp
    }

    object FullList : SearchScreenState() {
        override val bottomSheetState: BottomSheetState get() = BottomSheetState.Full
        override val searchFabState: SearchFabState
            get() = SearchFabState(
                iconRes = R.drawable.ic_map,
                isVisible = true,
                textRes = R.string.search_fab_map,
            )
        override val subHeaderHeightDp: Dp get() = SubheaderHeightDp
    }

    data class HalfMap(
        val iconRes: Int,
        val textRes: Int,
    ) : SearchScreenState() {
        override val bottomSheetState: BottomSheetState get() = BottomSheetState.Half
        override val searchFabState: SearchFabState = SearchFabState(
            iconRes = iconRes,
            isVisible = false,
            textRes = textRes,
        )
        override val subHeaderHeightDp: Dp get() = SubheaderHeightDp
    }
}