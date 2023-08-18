package com.github.sewerina.taxresident.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.sewerina.taxresident.MainViewState
import com.github.sewerina.taxresident.R
import com.github.sewerina.taxresident.data.RecordEntity
import com.github.sewerina.taxresident.ui.theme.TaxresidentTheme

class HomeScreenCallbacks(
    val onNewRecord: () -> Unit,
    val onEdit: (Int) -> Unit,
    val onRemove: (RecordEntity) -> Unit,
    val onSearch: () -> Unit,
    val onSettings: () -> Unit,
    val onAbout: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: State<MainViewState>, callbacks: HomeScreenCallbacks
) {
    val context = LocalContext.current
    val expandedMenuState = remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(modifier = Modifier.fillMaxWidth(),
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TaxresidentTheme.appBarColors,
                actions = {
                    PlainTooltipBox(tooltip = { Text(stringResource(R.string.tooltip_search)) }) {
                        IconButton(
                            modifier = Modifier.tooltipAnchor(),
                            onClick =  callbacks.onSearch,
                            content = {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "search icon"
                                )
                            }
                        )
                    }

                    PlainTooltipBox(tooltip = { Text(stringResource(R.string.tooltip_more)) }) {
                        IconButton(
                            modifier = Modifier.tooltipAnchor(),
                            onClick = { expandedMenuState.value = true },
                            content = {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = "more icon"
                                )
                            }
                        )
                    }

                    DropdownMenu(
                        offset = DpOffset(16.dp, 0.dp),
                        expanded = expandedMenuState.value,
                        onDismissRequest = { expandedMenuState.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menuItem_settings)) },
                            onClick = { callbacks.onSettings.invoke() },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = "settings icon"
                                )
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.primary,
                                leadingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menuItem_about)) },
                            onClick = { callbacks.onAbout.invoke() },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "info icon"
                                )
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.primary,
                                leadingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!state.value.loading) {
                FloatingActionButton(
                    onClick = callbacks.onNewRecord,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        hoveredElevation = 10.dp,
                        focusedElevation = 10.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
//                        modifier = Modifier.size(24.dp),
//                        painter = painterResource(id = R.drawable.ic_add),
                        imageVector = Icons.Filled.Add,
                        contentDescription = "add icon"
                    )
                }
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = it.calculateTopPadding(), start = 16.dp, end = 16.dp, bottom = 8.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.text_used),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = state.value.usedDays.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.text_left),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "${state.value.leftDays} / 182",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            ListView(state, callbacks.onEdit, callbacks.onRemove)
        }

        if (state.value.loading) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListView(
    mainViewState: State<MainViewState>, onEdit: (Int) -> Unit, onRemove: (RecordEntity) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(modifier = Modifier.fillMaxHeight(), state = listState) {
        items(
            items = mainViewState.value.list,
            key = { rec -> rec.id }
        ) { record ->
            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                onRemove.invoke(record)
            }

            SwipeToDismiss(state = dismissState,
                modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp),
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> Color.Transparent
                            else -> Color.Red
                        }
                    )

                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                    )
                    val tint by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> MaterialTheme.colorScheme.primary
                            else -> Color.White
                        }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 0.dp)
                            .background(color), contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "delete icon",
                            modifier = Modifier
                                .scale(scale)
                                .padding(horizontal = 24.dp),
                            tint = tint
                        )
                    }
                },
                dismissContent = { ListItem(record = record, onEdit = onEdit) })
        }
    }

    LaunchedEffect(key1 = mainViewState.value.list.size) {
        if (mainViewState.value.list.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(record: RecordEntity, onEdit: (Int) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 0.dp),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            )
        ),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        ),
        onClick = { onEdit.invoke(record.id) }) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
            Text(
                text = record.comment.show(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            )

            Row(modifier = Modifier.padding(vertical = 6.dp)) {
                Column() {
                    Row() {
                        Icon(
                            modifier = Modifier.padding(end = 16.dp),
                            painter = painterResource(id = R.drawable.ic_flight_dep),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "flight departure icon"
                        )
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = record.departureDate.show(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Row() {
                        Icon(
                            modifier = Modifier.padding(end = 16.dp),
                            painter = painterResource(id = R.drawable.ic_flight_arr),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "flight arrival icon"
                        )
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = record.arrivalDate.show(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterVertically),
                    text = "${record.days()} ${stringResource(R.string.text_days)}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}