package com.github.sewerina.taxresident.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.sewerina.taxresident.MainViewModel
import com.github.sewerina.taxresident.R
import com.github.sewerina.taxresident.SearchViewState
import com.github.sewerina.taxresident.data.RecordEntity

class SearchScreenCallbacks(
    val onEdit: (Int) -> Unit, val onRemove: (RecordEntity) -> Unit, val onBack: () -> Unit
)

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun SearchScreen(viewModel: MainViewModel, searchScreenCallbacks: SearchScreenCallbacks) {
    val searchState = viewModel.searchViewState.observeAsState(initial = SearchViewState.initial)

    val searchText = remember {
        mutableStateOf("")
    }
    val isActiveSearch = remember {
        mutableStateOf(false)
    }

    Surface() {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchBar(modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = if (searchState.value.suggestionList.isEmpty()) 68.dp else 128.dp),
                placeholder = { Text(text = stringResource(R.string.placeholder_search)) },
                shape = SearchBarDefaults.fullScreenShape,
                windowInsets = SearchBarDefaults.windowInsets,
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    dividerColor = MaterialTheme.colorScheme.onBackground
                ),
                query = searchText.value,
                onQueryChange = { changedValue -> searchText.value = changedValue },
                onSearch = {
                    isActiveSearch.value = false
                    viewModel.search(searchText.value)
                },
                active = isActiveSearch.value,
                onActiveChange = { value -> isActiveSearch.value = value },
                leadingIcon = {
                    // Кнопка-стрелка назад
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = "arrow back icon"
                            )
                        },
                        onClick = {
                            isActiveSearch.value = false
                            searchScreenCallbacks.onBack.invoke()
                        }
                    )
                },
                trailingIcon = {
                    // Кнопка "Очистить/Х"
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = "clear icon"
                            )
                        },
                        onClick = {
                            if (searchText.value.isNotEmpty()) {
                                searchText.value = ""
                                viewModel.clearSearch()
                            } else {
                                isActiveSearch.value = false
                            }
                        })
                }) {
                if (searchState.value.suggestionList.isNotEmpty()) {
                    // Отображение списка истории поиска
                    LazyRow() {
                        items(searchState.value.suggestionList) { item ->
                            SuggestionChip(modifier = Modifier.padding(4.dp),
                                enabled = true,
                                icon = null,
                                border = SuggestionChipDefaults.suggestionChipBorder(borderColor = MaterialTheme.colorScheme.onPrimaryContainer),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                                label = {
                                    Text(
                                        modifier = Modifier.padding(4.dp),
                                        text = item,
                                        fontSize = 16.sp
                                    )
                                },
                                onClick = {
                                    searchText.value = item
                                    isActiveSearch.value = false
                                    viewModel.search(searchText.value)
                                })
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                if (searchState.value.recordList.isNotEmpty()) {
                    //Список из Home - ListView или всего лишь один элемент списка
                    SearchListView(
                        searchState, searchScreenCallbacks.onEdit, searchScreenCallbacks.onRemove
                    )
                } else if (searchState.value.query.isNotEmpty()) {
                    EmptyList(text = stringResource(id = R.string.text_emptyList_search))
                }
            }
        }

        if (searchState.value.loading) {
            // Progress
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(56.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray,
                    strokeWidth = 4.dp,
                )
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.initSuggestionList()
    }
}

@Composable
@Preview
fun EmptyList(@PreviewParameter(EmptyListPreviewParameterProvider::class, limit = 2) text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(48.dp),
            imageVector = Icons.Outlined.Warning,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "empty list icon"
        )
        Text(
            textAlign = TextAlign.Center, text = text
        )
    }
}

class EmptyListPreviewParameterProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "Пока не создано ни одной записи", "Ничего по заданному запросу не найдено"
    )
}