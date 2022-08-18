package com.example.composemaps.data

import com.example.composemaps.model.DataWithStatus
import com.example.composemaps.model.Location
import com.example.composemaps.model.LocationData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data layer for exposing [Location]s
 */
interface LocationsDataLayer {
    /**
     * A flow of locations.
     */
    val locationsFlow: Flow<DataWithStatus<LocationData>>
}

@Singleton
class LocationsDataLayerImpl @Inject constructor() : LocationsDataLayer {
    private val locations = LocationData(
        locations = listOf(
            Location(
                name = "Minneapolis, MN, USA",
                latLng = LatLng(44.9778, -93.2650),
            ),
            Location(
                name = "Madison, WI, USA",
                latLng = LatLng(43.0722, -89.4008)
            ),
            Location(
                name = "New Prague, MN, USA",
                latLng = LatLng(44.5433, -93.5761)
            ),
            Location(
                name = "Duluth, MN, USA",
                latLng = LatLng(46.7867, -92.1005),
            ),
            Location(
                name = "Minneapolis, MN, USA",
                latLng = LatLng(44.9778, -93.2650),
            ),
            Location(
                name = "Madison, WI, USA",
                latLng = LatLng(43.0722, -89.4008)
            ),
            Location(
                name = "New Prague, MN, USA",
                latLng = LatLng(44.5433, -93.5761)
            ),
            Location(
                name = "Duluth, MN, USA",
                latLng = LatLng(46.7867, -92.1005),
            ),
            Location(
                name = "Minneapolis, MN, USA",
                latLng = LatLng(44.9778, -93.2650),
            ),
            Location(
                name = "Madison, WI, USA",
                latLng = LatLng(43.0722, -89.4008)
            ),
            Location(
                name = "New Prague, MN, USA",
                latLng = LatLng(44.5433, -93.5761)
            ),
            Location(
                name = "Duluth, MN, USA",
                latLng = LatLng(46.7867, -92.1005),
            ),Location(
                name = "Minneapolis, MN, USA",
                latLng = LatLng(44.9778, -93.2650),
            ),
            Location(
                name = "Madison, WI, USA",
                latLng = LatLng(43.0722, -89.4008)
            ),
            Location(
                name = "New Prague, MN, USA",
                latLng = LatLng(44.5433, -93.5761)
            ),
            Location(
                name = "Duluth, MN, USA",
                latLng = LatLng(46.7867, -92.1005),
            ),
            Location(
                name = "Minneapolis, MN, USA",
                latLng = LatLng(44.9778, -93.2650),
            ),
            Location(
                name = "Madison, WI, USA",
                latLng = LatLng(43.0722, -89.4008)
            ),
            Location(
                name = "New Prague, MN, USA",
                latLng = LatLng(44.5433, -93.5761)
            ),
            Location(
                name = "Duluth, MN, USA",
                latLng = LatLng(46.7867, -92.1005),
            )
        )
    )

    override val locationsFlow: Flow<DataWithStatus<LocationData>> = flowOf(DataWithStatus.Loaded(locations))
}