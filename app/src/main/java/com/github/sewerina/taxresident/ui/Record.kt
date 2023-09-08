package com.github.sewerina.taxresident.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.sewerina.taxresident.R
import com.github.sewerina.taxresident.data.RecordEntity
import com.github.sewerina.taxresident.ui.theme.ErrorCardWithText
import com.github.sewerina.taxresident.ui.theme.TaxresidentTheme
import java.time.Instant

class RecordScreenCallbacks(
    val onBack: () -> Unit, val onSave: (RecordEntity) -> Unit, val onDelete: (RecordEntity) -> Unit
)

data class NonCrossingDateValidation(val valid: Boolean = true, val comment: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    record: RecordEntity, list: List<RecordEntity>, callbacks: RecordScreenCallbacks
) {
    val departureDateState = rememberSaveable { mutableStateOf(record.departureDate) }
    val arrivalDateState = rememberSaveable { mutableStateOf(record.arrivalDate) }
    var commentText by rememberSaveable { mutableStateOf(record.comment) }

    val validDepartureDateState = remember {
        derivedStateOf {
            if (departureDateState.value != null && arrivalDateState.value != null) {
                val arr = arrivalDateState.value ?: 0L
                val dep = departureDateState.value ?: 0L
                if (arr <= dep) return@derivedStateOf false
            }
            return@derivedStateOf true
        }
    }
    val validArrivalDateState = remember {
        derivedStateOf {
            if (arrivalDateState.value != null) {
                val arr = arrivalDateState.value ?: 0L
                val dep = departureDateState.value ?: 0L
                if (arr <= dep) return@derivedStateOf false
            }
            return@derivedStateOf true
        }
    }

    val validNonCrossingDateState = remember {
        derivedStateOf {
            if (departureDateState.value == null || arrivalDateState.value == null) {
                return@derivedStateOf NonCrossingDateValidation()
            }

            for (entity: RecordEntity in list) {
                if (entity.id == record.id) continue

                val arrE = entity.arrivalDate ?: 0L
                if (arrE == 0L) continue
                val depE = entity.departureDate ?: 0L

                val dep = departureDateState.value ?: 0L
                val arr = arrivalDateState.value ?: 0L

                if (arr < depE || dep > arrE) continue

                return@derivedStateOf NonCrossingDateValidation(false, entity.comment ?: "")
            }

            return@derivedStateOf NonCrossingDateValidation()
        }
    }

    val validSingleOpenArrivalDateEntityState = remember {
        derivedStateOf {
            // For new record
            if (departureDateState.value == null) return@derivedStateOf true

            // If we have already arrival date
            if (arrivalDateState.value != null) return@derivedStateOf true

            for (entity: RecordEntity in list) {
                if (entity.id == record.id) continue

                if (entity.arrivalDate == null) return@derivedStateOf false
            }

            return@derivedStateOf true
        }
    }

    val validProhibitDepartureDateInFutureState = remember {
        derivedStateOf {
            // For new record
            if (departureDateState.value == null) return@derivedStateOf true

            val depDateInMilliSec = departureDateState.value ?: 0L
            val departureInstant = Instant.ofEpochMilli(depDateInMilliSec)
            val nowInstant = Instant.now()
            if (departureInstant > nowInstant) return@derivedStateOf false

            return@derivedStateOf true
        }
    }

    val validState = remember {
        derivedStateOf {
            departureDateState.value != null && validDepartureDateState.value && validProhibitDepartureDateInFutureState.value && validArrivalDateState.value && validNonCrossingDateState.value.valid && validSingleOpenArrivalDateEntityState.value
        }
    }

    Scaffold(topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(),
            title = { Text(text = stringResource(id = R.string.title_record)) },
            colors = TaxresidentTheme.appBarColors,
            navigationIcon = {
                IconButton(onClick = callbacks.onBack, content = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "back button"
                    )
                })
            },
            actions = {
                PlainTooltipBox(tooltip = { Text(stringResource(R.string.tooltip_delete)) }) {
                    if (record.id > 0) IconButton(modifier = Modifier.tooltipAnchor(),
                        onClick = { callbacks.onDelete.invoke(record) },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "delete icon"
                            )
                        })
                }
            })
    }, floatingActionButton = {
        if (validState.value) {
            ExtendedFloatingActionButton(containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    hoveredElevation = 10.dp,
                    focusedElevation = 10.dp,
                    pressedElevation = 12.dp
                ),
                onClick = {
                    val updated = RecordEntity(
                        id = record.id,
                        departureDate = departureDateState.value,
                        arrivalDate = arrivalDateState.value,
                        comment = commentText
                    )
                    callbacks.onSave.invoke(updated)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Create, contentDescription = "create icon"
                    )
                },
                text = {
                    Text(
                        stringResource(
                            id = if (record.id == 0) R.string.btn_create else R.string.btn_edit
                        )
                    )
                })
        }
    }) { paddingValues ->
        // My content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 16.dp, start = 48.dp, end = 48.dp
                ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val departureInteractionSource = remember { MutableInteractionSource() }
            val arrivalInteractionSource = remember { MutableInteractionSource() }

            if (!validProhibitDepartureDateInFutureState.value) {
                ErrorCardWithText(stringResource(R.string.text_date_in_future))
            }

            if (!validNonCrossingDateState.value.valid) {
                ErrorCardWithText("${stringResource(R.string.text_dates_crossing)} ${validNonCrossingDateState.value.comment}")
            }

            if (!validSingleOpenArrivalDateEntityState.value) {
                ErrorCardWithText(stringResource(R.string.text_specify_arr_date))
            }

            OutlinedTextField(modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
                label = { Text(text = stringResource(R.string.label_dep_date)) },
                value = departureDateState.value.show(),
                onValueChange = { },
                readOnly = true,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_flight_dep),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "flight departure icon"
                    )
                },
                interactionSource = departureInteractionSource,
                isError = !validDepartureDateState.value,
                supportingText = { if (!validDepartureDateState.value) Text(text = stringResource(R.string.supportText_correct_dep_date)) })

            OutlinedTextField(modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
                label = { Text(text = stringResource(R.string.label_arr_date)) },
                value = arrivalDateState.value.show(),
                onValueChange = { },
                readOnly = true,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_flight_arr),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "flight arrival icon"
                    )
                },
                interactionSource = arrivalInteractionSource,
                isError = !validArrivalDateState.value,
                supportingText = { if (!validArrivalDateState.value) Text(text = stringResource(R.string.supportText_correct_arr_date)) })

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.label_comment)) },
                value = commentText.show(),
                onValueChange = { commentText = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "comment icon"
                    )
                },
                singleLine = false,
                minLines = 1,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, autoCorrect = true
                )
            )

            val openDialogDepartureDate = remember { mutableStateOf(false) }
            val openDialogArrivalDate = remember { mutableStateOf(false) }

            PickDateDialog(openDialogDepartureDate, departureDateState)
            PickDateDialog(openDialogArrivalDate, arrivalDateState)

            LaunchedEffect(key1 = departureInteractionSource, block = {
                departureInteractionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> {
                            openDialogDepartureDate.value = true
                        }
                    }
                }
            })
            LaunchedEffect(key1 = arrivalInteractionSource, block = {
                arrivalInteractionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> {
                            openDialogArrivalDate.value = true
                        }
                    }
                }
            })


        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PickDateDialog(openDialog: MutableState<Boolean>, dateState: MutableState<Long?>) {
    // To read the selected date from the state.
    if (openDialog.value) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateState.value)
        val confirmEnabled =
            remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
        DatePickerDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        dateState.value = datePickerState.selectedDateMillis
                    },
                    enabled = confirmEnabled.value,
                    content = { Text(stringResource(R.string.btn_confirm)) }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog.value = false },
                    content = { Text(stringResource(R.string.btn_dismiss)) }
                )
            }
        ) {
            DatePicker(
                state = datePickerState, title = null, headline = null, showModeToggle = false
            )
        }
    }
}