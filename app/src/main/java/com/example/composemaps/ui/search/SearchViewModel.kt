package com.example.composemaps.ui.search

import androidx.lifecycle.ViewModel
import com.example.composemaps.data.LocationsDataLayer
import com.example.composemaps.ui.search.components.MultistageBottomSheetState
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
        SearchScreenState.FullMap
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
        mutableSearchScreenState.value = when (val current = mutableSearchScreenState.value) {
            is SearchScreenState.FullerList -> current.convertToHalfMap()
            is SearchScreenState.FullMap -> current.convertToHalfMap()
            is SearchScreenState.HalfMap -> throw FAB_EXCEPTION
            SearchScreenState.FullList -> current.convertToHalfMap()
        }
    }


    fun onDragToNewState(bottomSheetState: MultistageBottomSheetState) {
        if (bottomSheetState == mutableSearchScreenState.value.bottomSheetState) return
        mutableSearchScreenState.value = when (bottomSheetState) {
            MultistageBottomSheetState.Fuller -> SearchScreenState.FullerList
            MultistageBottomSheetState.Full -> SearchScreenState.FullList
            MultistageBottomSheetState.Half -> mutableSearchScreenState.value.convertToHalfMap()
            MultistageBottomSheetState.Gone -> SearchScreenState.FullMap
        }
    }

    private fun SearchScreenState.convertToHalfMap() = SearchScreenState.HalfMap(
        iconRes =  this.searchFabState.iconRes,
        textRes =  this.searchFabState.textRes,
    )

    companion object {
        private val FAB_EXCEPTION = IllegalStateException("There is no FAB in the HalfMap state")
    }
}