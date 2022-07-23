package com.example.composemaps.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchList(
    searchListContent: SearchListContent,
    modifier: Modifier,
) {
        List(
            searchListContent = searchListContent,
            modifier = modifier
        )
}

@Composable
fun List(
    searchListContent: SearchListContent,
    modifier: Modifier,
) {
    when (searchListContent.data) {
        is SearchListData.Loaded -> {
            LazyColumn(
                modifier = modifier.background(color = Color.White),
            ) {
                itemsIndexed(searchListContent.data.listRowData) { index, item ->
                    SearchListRow(
                        displayDivider = index == searchListContent.data.listRowData.size,
                        searchListRowData = item,
                    )
                }
            }
        }
        else -> {
            Row(
                modifier.background(color = Color.White),
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun SearchListRow(
    displayDivider: Boolean,
    searchListRowData: SearchListRowData,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = searchListRowData.title,
            color = Color.Black,
            modifier = Modifier.background(color = searchListRowData.backgroundColor),
        )
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth(.9f)
        ).takeIf { displayDivider }
    }
}

data class SearchListContent(
    val data: SearchListData,
    val bottomSheetState: BottomSheetState,
    val listTopBarState: ListTopBarState,
)

sealed class SearchListData {
    object Error : SearchListData()

    data class Loaded(
        val listRowData: List<SearchListRowData>,
    ) : SearchListData()

    object Loading : SearchListData()
}

data class SearchListRowData(
    val backgroundColor: Color,
    val isHighlighted: Boolean,
    val title: String,
)