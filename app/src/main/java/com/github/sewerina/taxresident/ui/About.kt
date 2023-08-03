package com.github.sewerina.taxresident.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.sewerina.taxresident.R
import com.github.sewerina.taxresident.ui.theme.TaxresidentTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(),
            title = { Text(text = stringResource(id = R.string.title_about)) },
            colors = TaxresidentTheme.appBarColors,
            navigationIcon = {
                IconButton(onClick = onBack, content = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = "back button"
                    )
                })
            })
    }) { paddingValues ->
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                ),
            textAlign = TextAlign.Justify,
            text = stringResource(id = R.string.text_about),
            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
        )
    }
}