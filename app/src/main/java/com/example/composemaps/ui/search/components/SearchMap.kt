package com.example.composemaps.ui.search.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private val DEFAULT_START_POSITION = LatLng(0.0, 0.0)

@Composable
fun SearchMap(
    paddingValues: PaddingValues,
    searchMapContent: SearchMapContent,
) {
    val modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    when (searchMapContent.data) {
        is SearchMapData.Loaded -> {
            val highlightedMarker = searchMapContent.data.markers.firstOrNull { it.isHighlighted }
            val startingPosition = highlightedMarker?.latLng ?: DEFAULT_START_POSITION
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(startingPosition, searchMapContent.data.zoom)
            }
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = modifier,
            ) {
                searchMapContent.data.markers.map {
                    Marker(
                        state = MarkerState(position = it.latLng),
                        title = it.title,
                    )
                }
            }
        }
        else -> {
            CircularProgressIndicator(
                modifier = modifier,
            )
        }
    }
}

data class SearchMapContent(
    val data: SearchMapData,
)

sealed class SearchMapData {
    val zoom: Float = 10f

    object Error : SearchMapData()

    data class Loaded(
        val markers: List<MarkerData>,
    ) : SearchMapData()

    object Loading : SearchMapData()
}

data class MarkerData(
    val isHighlighted: Boolean,
    val latLng: LatLng,
    val title: String,
)
