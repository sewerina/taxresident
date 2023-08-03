package com.github.sewerina.taxresident.ui

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.github.sewerina.taxresident.R
import com.github.sewerina.taxresident.ui.theme.TaxresidentTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class SettingsScreenCallbacks(
    val onSwitchTheme: (Boolean) -> Unit,
    val onSaveUserName: (String) -> Unit,
    val onLoadUserName: () -> String,
    var onBack: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    darkTheme: Boolean, callbacks: SettingsScreenCallbacks
) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider",
        file
    )

    Scaffold(topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(),
            title = { Text(text = stringResource(id = R.string.title_settings)) },
            colors = TaxresidentTheme.appBarColors,
            navigationIcon = {
                IconButton(onClick = callbacks.onBack, content = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = "back button"
                    )
                })
            })
    }) { paddingValues ->
        Column(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Для сохранения захвач изображения
                var capturedImageUri by remember {
                    mutableStateOf<Uri>(Uri.EMPTY)
                }

                Box() {
                    if (capturedImageUri.path?.isNotEmpty() == true) {
                        Image(
                            modifier = Modifier.size(96.dp),
                            painter = rememberAsyncImagePainter(capturedImageUri),
                            contentDescription = "user avatar"
                        )
                    }
                    else if (file.exists() && file.length() > 0) {
                        Image(
                            modifier = Modifier.size(96.dp),
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "user avatar"
                        )
                    }
                    else {
                        Icon(
                            modifier = Modifier.size(96.dp),
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "user avatar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Box() {
                    // Для показа/скрывания PopupMenu(DropdownMenu)
                    val expandedAvatarMenuState = remember {
                        mutableStateOf(false)
                    }

                    // Для сохранения захвач изображения
                    val cameraLauncher =
                        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
                            capturedImageUri = uri
                        }


                    // Для учета permission на камеру
                    var permissionGranted by remember {
                        mutableStateOf(isPermissionGranted(context))
                    }
                    val permissionLauncher =
                        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { permissionGranted_ ->
                            // this is called when the user selects allow or deny
                            permissionGranted = permissionGranted_

                            if (permissionGranted_) {
                                cameraLauncher.launch(uri)
                            }
                        }
                    SmallEditIconBtn(96) { expandedAvatarMenuState.value = true }
                    DropdownMenu(
                        expanded = expandedAvatarMenuState.value,
                        offset = DpOffset(96.dp, 0.dp),
                        onDismissRequest = { expandedAvatarMenuState.value = false },
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.menuItem_photo_fromGallery)) },
                            onClick = {
                                expandedAvatarMenuState.value = false
                                // Todo! - 1) перейти в галерею и выбрать фото 2) сохранить выбранное фото

                            })
                        DropdownMenuItem(text = { Text(stringResource(R.string.menuItem_new_photo)) },
                            onClick = {
                                expandedAvatarMenuState.value = false
                                // Todo! - 1) открыть камеру и сделать фото 2) сохранить сделанное фото

                                if (!permissionGranted) {
                                    // ask for permission
                                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                } else {
                                    cameraLauncher.launch(uri)
                                }
                            })
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = callbacks.onLoadUserName.invoke(),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                    )
                )

                var showUserNameDialog by remember { mutableStateOf(false) }
                if (showUserNameDialog) {
                    val userName = remember {
                        mutableStateOf(callbacks.onLoadUserName.invoke())
                    }
                    AlertDialog(onDismissRequest = { showUserNameDialog = false },
                        title = { Text(stringResource(R.string.title_user_name)) },
                        text = {
                            OutlinedTextField(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp)
                                    .fillMaxWidth(),
                                label = { Text(stringResource(R.string.label_input_name)) },
                                value = userName.value,
                                onValueChange = {
                                    userName.value = it
                                },
                                readOnly = false,
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                callbacks.onSaveUserName.invoke(userName.value)
                                showUserNameDialog = false

                            }) {
                                Text(stringResource(id = R.string.btn_confirm))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showUserNameDialog = false }) {
                                Text(stringResource(R.string.btn_dismiss))
                            }
                        })
                }

                SmallEditIconBtn(16) { showUserNameDialog = true }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.textSwitch_style),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = darkTheme,
                    onCheckedChange = callbacks.onSwitchTheme,
                    colors = SwitchDefaults.colors(uncheckedBorderColor = MaterialTheme.colorScheme.surfaceVariant)
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
            Divider(thickness = 1.dp)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
            val helpTextId = if (darkTheme) R.string.text_dark_theme else R.string.text_light_theme
            Text(
                text = stringResource(helpTextId),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )


//            OutlinedTextField(
//                modifier = Modifier
//                    .padding(top = 8.dp, bottom = 8.dp)
//                    .fillMaxWidth(),
//                label = { Text(text = "Имя пользователя") },
//                value = callbacks.onLoadUserName.invoke(),
//                onValueChange = { callbacks.onSaveUserName.invoke(it) },
//                readOnly = false,
//                singleLine = true
////                interactionSource = departureInteractionSource,
////                isError = !validDepartureDateState.value,
////                supportingText = { if (!validDepartureDateState.value) Text(text = stringResource(R.string.supportText_correct_dep_date)) }
//            )
        }
    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val fileName = "avatar.jpg"

    val image = File(
        externalCacheDir, /* directory */
        fileName
    )
//    if (image.exists()){
//        image.delete()
//    }
//    image.createNewFile()
    return image
}

private fun isPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallEditIconBtn(horizontalPad: Int, onClick: () -> Unit) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        IconButton(modifier = Modifier
            .padding(start = horizontalPad.dp)
            .size(24.dp)
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraSmall
            ), onClick = onClick, content = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "edit avatar",
                tint = MaterialTheme.colorScheme.primary
            )
        })
    }
}

@Preview()
@Composable
private fun Settings() {
    SettingsScreen(
        darkTheme = true,
        callbacks = SettingsScreenCallbacks({}, {}, { return@SettingsScreenCallbacks "" }, {})
    )
}