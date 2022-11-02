package com.wolandpl.nordcodingtask.ui.compose

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.wolandpl.nordcodingtask.ui.R
import com.wolandpl.nordcodingtask.ui.model.PhoneCallViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    phoneCallViewModel: PhoneCallViewModel = viewModel()
) {

    val uiState = phoneCallViewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp
                    ),
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name)
                    )
                },
                actions = {
                    if (uiState.roleGranted && uiState.permissionsGranted) {
                        Button(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag(START_STOP_BUTTON_TAG),
                            onClick = {
                                phoneCallViewModel.switchServerState()
                            }
                        ) {
                            Text(
                                text = stringResource(
                                    if (uiState.isServerStarted) {
                                        R.string.stop_server
                                    } else {
                                        R.string.start_server
                                    }
                                )
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        } else {
            val context = LocalContext.current

            var showRoleMessage by remember { mutableStateOf(false) }
            var showPermissionsMessage by remember { mutableStateOf(false) }

            var roleButtonOnClick = {}

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val roleManager =
                    context.getSystemService(ComponentActivity.ROLE_SERVICE) as RoleManager

                if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                    if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                        phoneCallViewModel.onRoleGranted()
                    } else {
                        val roleRequestIntent =
                            roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)

                        val requestRoleLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.StartActivityForResult()
                        ) {
                            if (it.resultCode == Activity.RESULT_OK) {
                                phoneCallViewModel.onRoleGranted()
                            } else {
                                showRoleMessage = true
                            }
                        }

                        roleButtonOnClick = {
                            requestRoleLauncher.launch(roleRequestIntent)
                        }

                        LaunchedEffect(Unit) {
                            requestRoleLauncher.launch(roleRequestIntent)
                        }
                    }
                }
            } else {
                phoneCallViewModel.onRoleGranted()
            }

            if (uiState.roleGranted) {
                var showRationaleDialog by remember { mutableStateOf(false) }

                val permissionsState = rememberMultiplePermissionsState(permissions) { result ->
                    result.forEach {
                        if (it.value.not()) {
                            if ((context as Activity).shouldShowRequestPermissionRationale(it.key)) {
                                showRationaleDialog = true
                            } else {
                                showPermissionsMessage = true
                            }
                        }
                    }
                }

                if (permissionsState.allPermissionsGranted) {
                    phoneCallViewModel.onPermissionsGranted()
                } else {
                    LaunchedEffect(Unit) {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }

                if (showRationaleDialog) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = {
                            Text(stringResource(R.string.rationale_title))
                        },
                        text = {
                            Text(stringResource(R.string.rationale_message))
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showRationaleDialog = false

                                    permissionsState.launchMultiplePermissionRequest()
                                }
                            ) {
                                Text(stringResource(android.R.string.ok))
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    showRationaleDialog = false
                                    showPermissionsMessage = true
                                }
                            ) {
                                Text(stringResource(R.string.rationale_deny))
                            }
                        }
                    )
                }
            }

            if (uiState.roleGranted && uiState.permissionsGranted) {
                showRoleMessage = false
                showPermissionsMessage = false
            }

            if (showRoleMessage || showPermissionsMessage) {
                val permissionsFromSettingsLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) {
                    if (permissions.all {
                            context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
                        }
                    ) {
                        phoneCallViewModel.onPermissionsGranted()
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                ) {
                    OutlinedTextField(
                        stringResource(
                            id = when (uiState.roleGranted) {
                                false -> R.string.role_message
                                else -> R.string.permissions_message
                            }
                        ),
                        onValueChange = {},
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = {
                            when (uiState.roleGranted) {
                                false -> roleButtonOnClick()
                                else -> permissionsFromSettingsLauncher.launch(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                )
                            }
                        }
                    ) {
                        Text(
                            stringResource(
                                id = when (uiState.roleGranted) {
                                    false -> R.string.grant_role
                                    else -> R.string.grant_permission
                                }
                            )
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                ) {
                    uiState.serverAddress?.let {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .testTag(SERVER_ADDRESS_TAG),
                            value = stringResource(R.string.server_address, it),
                            onValueChange = {},
                            readOnly = true,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        uiState.phoneCalls.forEach { phoneCall ->
                            item {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 2.dp)
                                        .fillMaxWidth(),
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 8.dp),
                                        text = "${phoneCall.name ?: phoneCall.number} (${phoneCall.duration} s)",
                                        fontSize = 18.sp
                                    )
                                }

                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}

private val permissions = listOf(
    android.Manifest.permission.READ_PHONE_STATE,
    android.Manifest.permission.READ_CALL_LOG,
    android.Manifest.permission.READ_CONTACTS,
    android.Manifest.permission.INTERNET,
    android.Manifest.permission.ACCESS_NETWORK_STATE
)

const val START_STOP_BUTTON_TAG = "START_STOP_BUTTON_TAG"
const val SERVER_ADDRESS_TAG = "SERVER_ADDRESS_TAG"
