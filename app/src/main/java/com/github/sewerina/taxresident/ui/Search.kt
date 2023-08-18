package com.github.sewerina.taxresident.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.github.sewerina.taxresident.R
import com.github.sewerina.taxresident.ui.theme.TaxresidentTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
//    Text(text = "Search will be here", color = Color.Blue, textAlign = TextAlign.Justify)

    val searchText = remember {
        mutableStateOf("")
    }
    val isActiveSearch = remember {
        mutableStateOf(false)
    }
    val searchHistoryList = remember {
        mutableStateOf(listOf("January", "March", "May", "July", "August"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {},
                colors = TaxresidentTheme.appBarColors,
                actions = {
                    SearchBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text(text = stringResource(R.string.placeholder_search)) },
                        shape = MaterialTheme.shapes.large,
                        colors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.background,
                            dividerColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        query = searchText.value,
                        onQueryChange = { changedValue -> searchText.value = changedValue },
                        onSearch = {
                            isActiveSearch.value = false
                            /*TODO*/
                        },
                        active = isActiveSearch.value,
                        onActiveChange = { value -> isActiveSearch.value = value },
//        leadingIcon = {
//            IconButton(
//                content = {
//                    Icon(
//                        imageVector = Icons.Outlined.Search,
//                        contentDescription = "search icon"
//                    )
//                },
//                onClick = {
//                    isActiveSearch.value = false
//                    /*TODO*/
//                }
//            )
//        },
                        trailingIcon = {
                            IconButton(
                                content = {
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "clear icon"
                                    )
                                },
                                onClick = { searchText.value = "" }
                            )
                        }
                    ) {
                        // Отображение списка истории поиска
                        LazyColumn() {
                            items(searchHistoryList.value) { item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchText.value = item
                                            isActiveSearch.value = false
                                            /*TODO*/
                                        },
                                    content = {
                                        Text(
                                            modifier = Modifier.padding(16.dp),
                                            text = item,
                                            fontFamily = FontFamily.Cursive,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                )
        ) {

        }
    }
}