package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
fun ExactBottomNav(navController: NavController, currentRoute: String) {
    NavigationBar {
        val items = listOf(
            Triple("setup",      "Setup",     Icons.Default.DirectionsBoat),
            Triple("cargo_plan", "Loading",   Icons.Outlined.Inventory2),
            Triple("stability",  "Stability", Icons.Outlined.AccountBalance),
            Triple("marine",     "Marine",    Icons.Outlined.Waves),
            Triple("reports",    "Reports",   Icons.Outlined.Assessment),
            Triple("profile",    "Profile",   Icons.Outlined.Person)
        )
        items.forEach { (route, label, icon) ->
            val selected = currentRoute == route
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selected,
                onClick = {
                    if (!selected) navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = OnActiveOrange,
                    selectedTextColor   = IndustrialPrimary,
                    indicatorColor      = ActiveOrange,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

// Menu de três pontos no canto superior direito — About e Help
@Composable
fun AppMenuActions(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = IndustrialPrimary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text        = { Text("Help") },
                onClick     = { expanded = false; navController.navigate("help") },
                leadingIcon = { Icon(Icons.Outlined.HelpOutline, contentDescription = null) }
            )
            DropdownMenuItem(
                text        = { Text("About") },
                onClick     = { expanded = false; navController.navigate("about") },
                leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
            )
        }
    }
}

fun countryName(code: String): String {
    return try {
        val name = Locale("", code).displayCountry
        name.ifBlank { code }
    } catch (e: Exception) {
        code
    }
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
        text = {
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCountrySelected(code) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = IndustrialPrimary) }
        },
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
    countries: List<String>,
    onPortSelected: (PortResult) -> Unit,
    onBrowseCountry: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(results) {
        if (results.isNotEmpty()) expanded = true
    }

    Column {
        ExposedDropdownMenuBox(
            expanded        = expanded && results.isNotEmpty(),
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
                expanded        = expanded && results.isNotEmpty(),
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

        TextButton(onClick = { showCountryDialog = true }) {
            Text(
                "Don't know the port name? Browse by country",
                style = MaterialTheme.typography.bodySmall,
                color = IndustrialPrimary
            )
        }
    }

    if (showCountryDialog) {
        CountryPickerDialog(
            countries         = countries,
            onDismiss         = { showCountryDialog = false },
            onCountrySelected = { code -> onBrowseCountry(code); showCountryDialog = false }
        )
    }
}