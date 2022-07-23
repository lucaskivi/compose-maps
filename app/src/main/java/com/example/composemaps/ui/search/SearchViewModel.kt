package com.example.composemaps.ui.search

import androidx.lifecycle.ViewModel
import com.example.composemaps.data.LocationsDataLayer
import com.example.composemaps.ui.search.components.BottomSheetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    dataLayer: LocationsDataLayer,
    transformer: SearchTransformer,
) : ViewModel() {

    /**
     * A flow that is the source of truth for screen state.
     */
    private val mutableSearchScreenState: MutableStateFlow<SearchScreenState> = MutableStateFlow(
        SearchScreenState.FullMap { onClickVisibilityButton() }
    )

    /**
     * A flow to emit all UI data through.
     */
    val searchScreenContent: Flow<SearchScreenContent> = combine(
        dataLayer.locationsFlow,
        mutableSearchScreenState,
    ) { locationData, searchScreenState ->
        SearchScreenContent(
            listData = transformer.transformListData(locationData.data),
            mapData = transformer.transformMapData(locationData.data),
            searchScreenState = searchScreenState,
        )
    }

    fun fabClicked() {
        mutableSearchScreenState.value = when (mutableSearchScreenState.value) {
            is SearchScreenState.FullMap -> SearchScreenState.HalfMap { onClickVisibilityButton() }
            is SearchScreenState.HalfMap -> throw FAB_EXCEPTION
            SearchScreenState.FullList -> SearchScreenState.HalfMap { onClickVisibilityButton() }
        }
    }

    private fun onClickVisibilityButton() {
        mutableSearchScreenState.value = when (mutableSearchScreenState.value) {
            SearchScreenState.FullList -> throw EXCEPTION
            is SearchScreenState.HalfMap -> SearchScreenState.FullMap { onClickVisibilityButton() }
            is SearchScreenState.FullMap -> SearchScreenState.HalfMap { onClickVisibilityButton() }
        }
    }

    fun onDragToNewState(bottomSheetState: BottomSheetState) {
        mutableSearchScreenState.value = when (bottomSheetState) {
            BottomSheetState.Full -> SearchScreenState.FullList
            BottomSheetState.Half -> SearchScreenState.HalfMap { onClickVisibilityButton() }
            BottomSheetState.Gone -> SearchScreenState.FullMap { onClickVisibilityButton() }
        }
    }

    companion object {
        private val EXCEPTION = IllegalStateException("There is no visibility button in the FullList state")
        private val FAB_EXCEPTION = IllegalStateException("There is no FAB in the HalfMap state")
    }
}