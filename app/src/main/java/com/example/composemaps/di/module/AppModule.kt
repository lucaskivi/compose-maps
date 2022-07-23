package com.example.composemaps.di.module

import com.example.composemaps.data.LocationsDataLayer
import com.example.composemaps.data.LocationsDataLayerImpl
import com.example.composemaps.ui.search.SearchTransformer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

/**
 * A singleton Hilt module responsible for setting up app dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun bindsLocationDataLayer() : LocationsDataLayer = LocationsDataLayerImpl()

    @Provides
    fun providesSearchTransformer() = SearchTransformer()
}