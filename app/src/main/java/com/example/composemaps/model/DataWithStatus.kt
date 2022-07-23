package com.example.composemaps.model

/**
 * A sealed class representing the data and its current status.
 */
sealed class DataWithStatus<out T : Any> {
    abstract val data: T?

    /**
     * Data has been fully loaded and is presentable in the UI.
     */
    data class Loaded<T : Any>(
        override val data: T,
    ) : DataWithStatus<T>()

    /**
     * May have previously loaded data but is currently loading.
     */
    data class Loading<T : Any>(
        override val data: T?,
    ) : DataWithStatus<T>()

    /**
     * May have previously loaded data but currently has an error.
     */
    data class Error<T : Any>(
        override val data: T?,
        val error: Throwable,
    ) : DataWithStatus<T>()
}