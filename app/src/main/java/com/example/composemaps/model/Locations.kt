package com.example.composemaps.model

import com.google.android.gms.maps.model.LatLng

/**
 * A data class to represent a set of locations.
 */
class LocationData(
    val locations: List<Location>,
) {
    companion object {
        val DEFAULT = LocationData(
            emptyList(),
        )
    }
}

/**
 * A data class to represent locations.
 */
data class Location(
    val name: String,
    val latLng: LatLng,
)