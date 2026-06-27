package com.example.stabilityloadingplanner.ui.theme

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController, viewModel: VesselViewModel, authViewModel: AuthViewModel) {
    val context = LocalContext.current

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val currentDate = sdf.format(Date())

    val vesselName = viewModel.activeVessel.name.ifEmpty { "No Vessel Selected" }
    val isStable = viewModel.currentGM > 0.3
    val statusText = if (isStable) "STABLE (GO)" else "UNSTABLE (NO-GO)"
    val statusColor = if (isStable) Color(0xFF2E7D32) else Color(0xFFC62828)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                actions = { AppMenuActions(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "reports") },
        containerColor = IndustrialBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            if (!viewModel.hasVesselSelected) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Icon(Icons.Outlined.DirectionsBoat, contentDescription = null, modifier = Modifier.size(64.dp), tint = OutlineVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No Report Available", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Select a vessel and fill in the cargo plan to generate a report.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                    }
                }
                return@Scaffold
            }

            Text("Document Preview", fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(bottom = 8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Vessel: $vesselName", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("IMO: ${viewModel.activeVessel.imo}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Date: $currentDate", fontSize = 14.sp)
                    Text(text = "Status: $statusText", fontSize = 14.sp, color = statusColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Voyage: ${viewModel.currentVoyage.departurePort} → ${viewModel.currentVoyage.arrivalPort}", fontSize = 13.sp, color = TextSecondary)
                    Text("Total cargo: ${viewModel.tanks.sumOf { it.weightFloat.toDouble() }.toInt()} t / ${viewModel.activeVessel.deadweight.toInt()} t DWT", fontSize = 13.sp, color = TextSecondary)
                    Text("GM: ${"%.2f".format(viewModel.currentGM)} m   |   KG: ${"%.2f".format(viewModel.currentKG)} m", fontSize = 13.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(24.dp))

                    if (authViewModel.isPro) {
                        // Conta PRO — exportação disponível
                        Button(
                            onClick = { generatePdfReport(context, viewModel, currentDate, statusText) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Export to PDF", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Conta FREE — botão bloqueado com caminho para upgrade
                        OutlinedButton(
                            onClick = { navController.navigate("profile") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export to PDF — PRO only", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Upgrade to PRO (1€/month) to unlock PDF export.", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

fun generatePdfReport(context: Context, viewModel: VesselViewModel, date: String, status: String) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    // Título
    paint.textSize = 22f
    paint.isFakeBoldText = true
    canvas.drawText("STABILITY & LOADING REPORT", 50f, 55f, paint)

    // Cabeçalho Geral
    paint.textSize = 13f
    paint.isFakeBoldText = false
    var y = 90f
    canvas.drawText("Vessel: ${viewModel.activeVessel.name}   |   IMO: ${viewModel.activeVessel.imo}", 50f, y, paint)
    y += 18f
    canvas.drawText("Report Date: $date", 50f, y, paint)
    y += 32f

    // --- VOYAGE DETAILS ---
    paint.isFakeBoldText = true
    canvas.drawText("--- VOYAGE DETAILS ---", 50f, y, paint)
    paint.isFakeBoldText = false
    y += 22f

    // Apenas com o que realmente existe
    canvas.drawText("Departure: ${viewModel.currentVoyage.departurePort}", 50f, y, paint)
    y += 18f
    canvas.drawText("Arrival:   ${viewModel.currentVoyage.arrivalPort}", 50f, y, paint)
    y += 32f

    // --- CARGO PLAN ---
    paint.isFakeBoldText = true
    canvas.drawText("--- CARGO PLAN ---", 50f, y, paint)
    paint.isFakeBoldText = false
    y += 22f
    viewModel.tanks.forEach { tank ->
        canvas.drawText("  ${tank.name}: ${tank.currentWeight.ifEmpty { "0" }} t  (${tank.selectedCargo.name})", 50f, y, paint)
        y += 18f
    }

    // Total
    val total = viewModel.tanks.sumOf { it.weightFloat.toDouble() }
    y += 8f
    paint.isFakeBoldText = true
    canvas.drawText("TOTAL CARGO LOADED: ${total.toInt()} t / ${viewModel.activeVessel.deadweight.toInt()} t DWT", 50f, y, paint)
    y += 32f

    // --- STABILITY ANALYSIS ---
    canvas.drawText("--- STABILITY ANALYSIS ---", 50f, y, paint)
    paint.isFakeBoldText = false
    y += 22f
    canvas.drawText("KG (centre of gravity):   ${String.format("%.3f", viewModel.currentKG)} m", 50f, y, paint)
    y += 18f
    canvas.drawText("GM (metacentric height):  ${String.format("%.3f", viewModel.currentGM)} m", 50f, y, paint)
    y += 18f
    canvas.drawText("Min. safe GM:             0.300 m", 50f, y, paint)
    y += 28f

    paint.isFakeBoldText = true
    paint.textSize = 15f
    paint.color = if (status.contains("STABLE") && !status.contains("UN"))
        android.graphics.Color.rgb(0x2E, 0x7D, 0x32)
    else
        android.graphics.Color.rgb(0xC6, 0x28, 0x28)
    canvas.drawText("VESSEL STATUS: $status", 50f, y, paint)
    paint.color = android.graphics.Color.BLACK

    pdfDocument.finishPage(page)

    // Nome do ficheiro com data para não sobrescrever os anteriores
    val fileName = "CargoReport_${viewModel.activeVessel.name.replace(" ", "_")}_${
        SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
    }.pdf"

    // Gravação na pasta Downloads
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ já não deixa escrever directamente no armazenamento externo
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { pdfDocument.writeTo(it) }
                Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Error: could not create file.", Toast.LENGTH_LONG).show()
            }
        } else {
            val publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(publicDir, fileName)
            FileOutputStream(file).use { pdfDocument.writeTo(it) }
            Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}