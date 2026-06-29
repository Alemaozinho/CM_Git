package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.stabilityloadingplanner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VesselSetupScreen(navController: NavController, viewModel: VesselViewModel) {
    var imoNumber by remember { mutableStateOf("") }
    val searchResult  = viewModel.searchResult
    val isSearching   = viewModel.isSearchingVessel
    val searchError   = viewModel.vesselSearchError
    val hasEstimates  = viewModel.searchHasEstimates
    val searchHistory = viewModel.searchHistory

    Scaffold(
        topBar = {
            TopAppBar(
                title   = { Text(stringResource(R.string.setup_title), fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                actions = { AppMenuActions(navController) },
                colors  = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "setup") },
        containerColor = IndustrialBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick        = { navController.navigate("vessel_registration") },
                containerColor = IndustrialPrimary,
                contentColor   = Color.White,
                text           = { Text(stringResource(R.string.setup_register_btn)) },
                icon           = { Icon(Icons.Default.Add, contentDescription = null) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── 1. Histórico ────────────────────────────────────────────
            if (searchHistory.isNotEmpty()) {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    colors    = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape     = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.History, null, tint = IndustrialPrimary, modifier = Modifier.size(18.dp))
                            Text(stringResource(R.string.setup_recent), fontWeight = FontWeight.Bold, color = IndustrialPrimary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        searchHistory.forEachIndexed { index, item ->
                            if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
                                    Text("IMO ${item.imo}", fontSize = 12.sp, color = TextSecondary)
                                }
                                TextButton(onClick = { imoNumber = item.imo; viewModel.searchVesselByImo(item.imo) }) {
                                    Text(stringResource(R.string.setup_use_again), color = IndustrialPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // ── 2. Pesquisa por IMO ─────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = IndustrialSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape     = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.setup_find_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.setup_find_hint), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value           = imoNumber,
                        onValueChange   = { imoNumber = it },
                        label           = { Text(stringResource(R.string.setup_imo_label)) },
                        leadingIcon     = { Icon(Icons.Outlined.DirectionsBoat, null, tint = IndustrialPrimary) },
                        modifier        = Modifier.fillMaxWidth(),
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors          = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndustrialPrimary,
                            focusedLabelColor  = IndustrialPrimary,
                            cursorColor        = IndustrialPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick  = { viewModel.searchVesselByImo(imoNumber) },
                        enabled  = !isSearching && imoNumber.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(8.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.setup_searching), fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.setup_search_btn), fontWeight = FontWeight.Bold)
                        }
                    }

                    if (searchError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(searchError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // ── 3. Resultado ────────────────────────────────────────────
            AnimatedVisibility(
                visible = searchResult != null,
                enter   = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit    = fadeOut()
            ) {
                val result         = viewModel.searchResult
                val photoUrl       = viewModel.vesselPhotoUrl
                val isLoadingPhoto = viewModel.isLoadingPhoto

                if (result != null) {
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        colors    = CardDefaults.cardColors(containerColor = IndustrialSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape     = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(stringResource(R.string.setup_vessel_found), fontWeight = FontWeight.Bold, color = IndustrialPrimary, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))

                            when {
                                isLoadingPhoto -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(200.dp)
                                            .clip(RoundedCornerShape(8.dp)).background(SurfaceContainerLow),
                                        contentAlignment = Alignment.Center
                                    ) { CircularProgressIndicator(color = IndustrialPrimary) }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                photoUrl != null -> {
                                    val context = LocalContext.current
                                    SubcomposeAsyncImage(
                                        model              = ImageRequest.Builder(context).data(photoUrl).crossfade(true).build(),
                                        contentDescription = result.name,
                                        modifier           = Modifier.fillMaxWidth().height(200.dp)
                                            .clip(RoundedCornerShape(8.dp)).background(SurfaceContainerLow),
                                        contentScale       = ContentScale.Fit,
                                        loading = {
                                            Box(modifier = Modifier.fillMaxSize().background(SurfaceContainerLow), contentAlignment = Alignment.Center) {
                                                CircularProgressIndicator(color = IndustrialPrimary)
                                            }
                                        },
                                        error = {
                                            Box(modifier = Modifier.fillMaxSize().background(SurfaceContainerLow), contentAlignment = Alignment.Center) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Icon(Icons.Default.BrokenImage, null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                                                    Text(stringResource(R.string.setup_photo_unavailable), fontSize = 12.sp, color = TextSecondary)
                                                }
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }

                            Text("${stringResource(R.string.setup_name)}${result.name}", fontWeight = FontWeight.Bold)
                            Text("${stringResource(R.string.setup_imo)}${result.imo}")
                            if (result.deadweight > 0)    Text("${stringResource(R.string.setup_dwt)}${result.deadweight.toInt()} t")
                            if (result.loa  > 0)          Text("${stringResource(R.string.setup_loa)}${"%.1f".format(result.loa)} m")
                            if (result.beam > 0)          Text("${stringResource(R.string.setup_beam)}${"%.1f".format(result.beam)} m")
                            if (result.numberOfHolds > 0) Text("${stringResource(R.string.setup_holds)}${result.numberOfHolds}")

                            if (hasEstimates) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                    Text(stringResource(R.string.setup_estimated), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick  = { viewModel.selectVessel(result); navController.navigate("cargo_plan") },
                                modifier = Modifier.fillMaxWidth(),
                                colors   = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                            ) { Text(stringResource(R.string.setup_use_vessel), fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}