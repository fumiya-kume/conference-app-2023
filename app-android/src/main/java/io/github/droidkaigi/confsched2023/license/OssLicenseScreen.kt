package io.github.droidkaigi.confsched2023.license

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ossLicenseScreenRoute = "osslicense"
fun NavGraphBuilder.ossLicenseScreen() {
    composable(ossLicenseScreenRoute) {
        OssLicenseScreen()
    }
}

fun NavController.navigateOssLicenseScreen() {
    navigate(ossLicenseScreenRoute)
}

data class OssLicenseScreenUiState(
    val ossLicense: OssLicense = OssLicense(),
)

@SuppressLint("ComposeModifierMissing")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OssLicenseScreen(
    viewModel: OssLicenseViewModel = hiltViewModel<OssLicenseViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadLicenseList()
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "OSS ライセンス")
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back button",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            OssLicenseScreen(
                uiState = uiState,
            ) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OssLicenseScreen(
    uiState: OssLicenseScreenUiState,
    onLibraryClick: (License) -> Unit,
) {
    LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
        items(items = uiState.ossLicense.groupList) { group ->
            var expand by remember {
                mutableStateOf(false)
            }
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onClick = { expand = !expand },
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(modifier = Modifier.padding(start = 8.dp), text = group.title, style = MaterialTheme.typography.headlineMedium)
                    AnimatedContent(targetState = expand, label = "") { targetState ->
                        if (targetState) {
                            Column {
                                group.licenses.forEach { license ->
                                    key(license.name) {
                                        TextButton(onClick = { onLibraryClick(license) }) {
                                            Text(text = license.name)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
