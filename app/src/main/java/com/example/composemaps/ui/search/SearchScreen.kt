package com.example.composemaps.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composemaps.R
import com.example.composemaps.ui.search.components.MultistageBottomSheetScaffold
import com.example.composemaps.ui.search.components.MultistageBottomSheetState
import com.example.composemaps.ui.search.components.SearchFab
import com.example.composemaps.ui.search.components.SearchFabState
import com.example.composemaps.ui.search.components.SearchListContent
import com.example.composemaps.ui.search.components.SearchListData
import com.example.composemaps.ui.search.components.SearchMap
import com.example.composemaps.ui.search.components.SearchMapContent
import com.example.composemaps.ui.search.components.SearchMapData
import com.example.composemaps.ui.search.components.searchListItems

private val SubheaderHeightDp = 56.dp
private val BottomSheetBarHeightDp = 40.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchScreenContent by viewModel.searchScreenContent.collectAsState(initial = SearchScreenContent.DEFAULT)

    MultistageBottomSheetScaffold(
        bottomSheetDraggedToNewStateCallback = viewModel::onDragToNewState,
        bottomSheetHeader = { BottomSheetHeader(modifier = Modifier.height(BottomSheetBarHeightDp)) },
        bottomSheetHeaderHeightDp = BottomSheetBarHeightDp,
        bottomSheetScrollableContent = {
            searchListItems(
                searchListContent = SearchListContent(
                    data = searchScreenContent.listData,
                    bottomSheetState = searchScreenContent.searchScreenState.bottomSheetState,
                )
            )
        },
        bottomSheetState = searchScreenContent.searchScreenState.bottomSheetState,
        header = { SearchHeader(it) },
        collapsibleHeaderDelta = SubheaderHeightDp,
        fab = {
            SearchFab(
                searchFabState = searchScreenContent.searchScreenState.searchFabState,
                onClick = viewModel::fabClicked
            )
        },
        fabPosition = FabPosition.End,
    ) {
        SearchMap(
            searchMapContent = SearchMapContent(
                data = searchScreenContent.mapData,
            )
        )
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
    subHeader: Dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(96.dp)
            .fillMaxWidth()
            .background(color = Color.Gray)
        ,
    ) {
        Text(
            text = "Compose Map",
            fontSize = 32.sp,
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(subHeader)
            .fillMaxWidth()
            .background(color = Color.Gray),
    ) {
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
            searchScreenState = SearchScreenState.FullMap
        )
    }
}

/**
 * A sealed class to represent search screen UI states.
 */
sealed class SearchScreenState {

    abstract val bottomSheetState: MultistageBottomSheetState

    abstract val searchFabState: SearchFabState

    abstract val subHeaderHeightDp: Dp

    object FullMap : SearchScreenState() {
        override val bottomSheetState: MultistageBottomSheetState get() = MultistageBottomSheetState.Gone
        override val searchFabState: SearchFabState
            get() = SearchFabState(
                iconRes = R.drawable.ic_list,
                isVisible = true,
                textRes = R.string.search_fab_list,
            )
        override val subHeaderHeightDp: Dp get() = SubheaderHeightDp
    }

    object FullerList : SearchScreenState() {
        override val bottomSheetState: MultistageBottomSheetState get() = MultistageBottomSheetState.Fuller
        override val searchFabState: SearchFabState
            get() = SearchFabState(
                iconRes = R.drawable.ic_map,
                isVisible = true,
                textRes = R.string.search_fab_map,
            )
        override val subHeaderHeightDp: Dp get() = 0.dp
    }

    object FullList : SearchScreenState() {
        override val bottomSheetState: MultistageBottomSheetState get() = MultistageBottomSheetState.Full
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
        override val bottomSheetState: MultistageBottomSheetState get() = MultistageBottomSheetState.Half
        override val searchFabState: SearchFabState = SearchFabState(
            iconRes = iconRes,
            isVisible = false,
            textRes = textRes,
        )
        override val subHeaderHeightDp: Dp get() = SubheaderHeightDp
    }
}