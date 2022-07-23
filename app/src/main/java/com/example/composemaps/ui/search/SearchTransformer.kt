package com.example.composemaps.ui.search

import androidx.compose.ui.graphics.Color
import com.example.composemaps.model.LocationData
import com.example.composemaps.ui.search.components.SearchMapData
import com.example.composemaps.ui.search.components.MarkerData
import com.example.composemaps.ui.search.components.SearchListData
import com.example.composemaps.ui.search.components.SearchListRowData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A transformer for search screen data transformations.
 */
@Singleton
class SearchTransformer @Inject constructor() {
    /**
     * Transform [LocationData] into [SearchMapData] data.
     */
    fun transformMapData(
        locationData: LocationData?,
    ): SearchMapData = locationData?.locations?.let { locations ->
        SearchMapData.Loaded(
            markers = locations.mapIndexed { index, location ->
                MarkerData(
                    isHighlighted = index == 0,
                    latLng = location.latLng,
                    title = location.name,
                )
            }
        )
    } ?: SearchMapData.Error

    fun transformListData(
        locationData: LocationData?,
    ): SearchListData = locationData?.locations?.let { locations ->
        SearchListData.Loaded(
            listRowData = locations.mapIndexed { index, location ->
                SearchListRowData(
                    backgroundColor = if (index == 0) Color.Yellow else Color.White, // todo fix this
                    isHighlighted = index == 0,
                    title = location.name,
                )
            }
        )
    } ?: SearchListData.Error
}

