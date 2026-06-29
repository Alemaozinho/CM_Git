package com.example.stabilityloadingplanner.ui.theme

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stabilityloadingplanner.R
import com.example.stabilityloadingplanner.data.models.PortResult
import java.util.Locale

@Composable
fun StabilityMetric(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AppMenuActions(navController: NavController) {
    val context      = LocalContext.current
    var showMenu     by remember { mutableStateOf(false) }
    var showLangDialog by remember { mutableStateOf(false) }
    val prefs        = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var currentLang  by remember { mutableStateOf(prefs.getString("lang", "auto") ?: "auto") }

    IconButton(onClick = { showMenu = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = null, tint = IndustrialPrimary)
    }

    DropdownMenu(
        expanded         = showMenu,
        onDismissRequest = { showMenu = false },
        modifier         = Modifier.background(IndustrialSurface)
    ) {
        DropdownMenuItem(
            text    = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = IndustrialPrimary)
                    Text("Language / Idioma", color = IndustrialPrimary, fontWeight = FontWeight.Medium)
                }
            },
            onClick = { showMenu = false; showLangDialog = true }
        )
        HorizontalDivider(color = OutlineVariant)
        DropdownMenuItem(
            text    = { Text("About", color = TextPrimary) },
            onClick = { showMenu = false; navController.navigate("about") }
        )
        DropdownMenuItem(
            text    = { Text("Help", color = TextPrimary) },
            onClick = { showMenu = false; navController.navigate("help") }
        )
    }

    // Diálogo de selecção de idioma
    if (showLangDialog) {
        val options = listOf(
            Triple("auto", "🌐 System default", "Automático (sistema)"),
            Triple("pt",   "🇵🇹 Português",     "Portuguese"),
            Triple("en",   "🇬🇧 English",        "Inglês")
        )
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title            = {
                Text("Language / Idioma", fontWeight = FontWeight.Bold, color = IndustrialPrimary)
            },
            text = {
                Column {
                    options.forEach { (code, label, sublabel) ->
                        Row(
                            modifier  = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentLang = code
                                    prefs.edit().putString("lang", code).apply()
                                    showLangDialog = false
                                    (context as? Activity)?.recreate()
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Column {
                                Text(label, fontWeight = FontWeight.Medium, color = TextPrimary)
                                Text(sublabel, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            RadioButton(
                                selected = currentLang == code,
                                onClick  = null,
                                colors   = RadioButtonDefaults.colors(selectedColor = IndustrialPrimary)
                            )
                        }
                        if (code != "en") HorizontalDivider(color = OutlineVariant)
                    }
                }
            },
            confirmButton  = {},
            dismissButton  = {
                TextButton(onClick = { showLangDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = IndustrialSurface
        )
    }
}

@Composable
fun ExactBottomNav(navController: NavController, currentRoute: String) {
    NavigationBar(containerColor = IndustrialSurface) {
        val items = listOf(
            Triple("setup",      stringResource(R.string.nav_setup),      Icons.Default.DirectionsBoat),
            Triple("cargo_plan", stringResource(R.string.nav_loading),    Icons.Outlined.Inventory2),
            Triple("stability",  stringResource(R.string.nav_stability),  Icons.Outlined.AccountBalance),
            Triple("marine",     stringResource(R.string.nav_marine),     Icons.Outlined.Waves),
            Triple("reports",    stringResource(R.string.nav_reports),    Icons.Outlined.Assessment),
            Triple("profile",    stringResource(R.string.nav_profile),    Icons.Outlined.Person)
        )
        items.forEach { (route, label, icon) ->
            val selected = currentRoute == route
            NavigationBarItem(
                icon     = { Icon(icon, contentDescription = label) },
                label    = { Text(label) },
                selected = selected,
                onClick  = {
                    if (!selected) navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = IndustrialPrimary,
                    selectedTextColor   = IndustrialPrimary,
                    indicatorColor      = IndustrialPrimary.copy(alpha = 0.12f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

// ── Utilitários ──────────────────────────────────────────────────────────────

fun countryName(code: String): String {
    return try {
        val name = Locale("", code).displayCountry
        name.ifBlank { code }
    } catch (e: Exception) { code }
}

@Composable
fun CountryPickerDialog(
    countries: List<String>,
    onDismiss: () -> Unit,
    onCountrySelected: (String) -> Unit
) {
    var filter by remember { mutableStateOf("") }
    val filtered = remember(filter, countries) {
        countries.filter { code -> countryName(code).contains(filter, ignoreCase = true) }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose a country", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
        text  = {
            Column {
                OutlinedTextField(
                    value         = filter,
                    onValueChange = { filter = it },
                    label         = { Text("Filter") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(320.dp)) {
                    items(filtered) { code ->
                        Text(
                            text     = countryName(code),
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onCountrySelected(code) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton  = { TextButton(onClick = onDismiss) { Text("Cancel", color = IndustrialPrimary) } },
        containerColor = IndustrialSurface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortSearchField(
    label: String,
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<PortResult>,
    isSearching: Boolean = false,
    countries: List<String> = emptyList(),
    onPortSelected: (PortResult) -> Unit,
    onBrowseCountry: ((String) -> Unit)? = null
) {
    var expanded          by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(results) {
        if (results.isNotEmpty()) expanded = true
    }

    Column {
        ExposedDropdownMenuBox(
            expanded         = expanded && results.isNotEmpty(),
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value         = query,
                onValueChange = { onQueryChange(it); expanded = true },
                label         = { Text(label) },
                modifier      = Modifier.fillMaxWidth().menuAnchor(),
                singleLine    = true,
                trailingIcon  = {
                    if (isSearching) CircularProgressIndicator(
                        modifier    = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color       = IndustrialPrimary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
            )
            ExposedDropdownMenu(
                expanded         = expanded && results.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                results.forEach { port ->
                    DropdownMenuItem(
                        text    = { Text("${port.name}  (${countryName(port.country)})") },
                        onClick = { onPortSelected(port); expanded = false }
                    )
                }
            }
        }

        if (onBrowseCountry != null) {
            TextButton(onClick = { showCountryDialog = true }) {
                Text(
                    "Don't know the port name? Browse by country",
                    style = MaterialTheme.typography.bodySmall,
                    color = IndustrialPrimary
                )
            }
        }
    }

    if (showCountryDialog && onBrowseCountry != null) {
        CountryPickerDialog(
            countries         = countries,
            onDismiss         = { showCountryDialog = false },
            onCountrySelected = { code -> onBrowseCountry(code); showCountryDialog = false }
        )
    }
}