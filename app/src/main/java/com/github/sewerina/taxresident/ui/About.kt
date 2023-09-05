package com.github.sewerina.taxresident.ui

import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebView
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
        HtmlText(htmlName = "about.html", topPadding = paddingValues.calculateTopPadding())
    }
}

// htmlName - имя файла с html-размеченным текстом в папке assets
@Composable
fun HtmlText(htmlName: String, topPadding: Dp) {
    // Наш html-файл записываем в буффер, а потом этот буффер в string
    val context = LocalContext.current
    val assetManager = context.assets
    val inputStream = assetManager.open("html/$htmlName")
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)

    val backgroundColor = MaterialTheme.colorScheme.background.toArgb().hexToString()
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb().hexToString()
    val linkColor = MaterialTheme.colorScheme.primary.toArgb().hexToString()
    val htmlString = String(buffer)
        .replace("{background}", backgroundColor)
        .replace("{color}", textColor)
        .replace("{linkColor}", linkColor)

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = topPadding,
                start = 0.dp,
                end = 0.dp,
                bottom = 0.dp
            ),
        factory = { it: Context ->
            WebView(it).apply {
                webChromeClient = object : WebChromeClient() {}
                loadDataWithBaseURL(null, htmlString, "text/html; charset=utf-8", "utf-8", null)
            }
        })
}

fun Int.hexToString() = String.format("#%06X", 0xFFFFFF and this)