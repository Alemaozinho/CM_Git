package com.example.stabilityloadingplanner.ui.theme

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController, viewModel: VesselViewModel) {
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "reports") },
        containerColor = IndustrialBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Document Preview", fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(bottom = 8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Vessel: $vesselName", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Date: $currentDate", fontSize = 14.sp)
                    Text(
                        text = "Status: $statusText",
                        fontSize = 14.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (vesselName == "No Vessel Selected") {
                                Toast.makeText(context, "Please select a vessel first!", Toast.LENGTH_SHORT).show()
                            } else {
                                generatePdfReport(context, viewModel, currentDate, statusText)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Export to PDF", color = Color.White, fontWeight = FontWeight.Bold)
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
    paint.textSize = 26f
    paint.isFakeBoldText = true
    canvas.drawText("STABILITY & LOADING REPORT", 50f, 60f, paint)

    // Cabeçalho Geral
    paint.textSize = 14f
    paint.isFakeBoldText = false
    var y = 100f
    canvas.drawText("Vessel: ${viewModel.activeVessel.name} | IMO: ${viewModel.activeVessel.imo}", 50f, y, paint)
    y += 20f
    canvas.drawText("Report Date: $date", 50f, y, paint)
    y += 40f

    // --- VOYAGE DETAILS ---
    paint.isFakeBoldText = true
    canvas.drawText("--- VOYAGE DETAILS ---", 50f, y, paint)
    paint.isFakeBoldText = false
    y += 25f

    // Apenas com o que realmente existe
    canvas.drawText("Departure: ${viewModel.currentVoyage.departure}", 50f, y, paint)
    y += 20f
    canvas.drawText("Arrival: ${viewModel.currentVoyage.arrival}", 50f, y, paint)
    y += 40f

    // --- CARGO PLAN ---
    paint.isFakeBoldText = true
    canvas.drawText("--- CARGO PLAN ---", 50f, y, paint)
    paint.isFakeBoldText = false
    y += 25f
    viewModel.tanks.forEach { tank ->
        canvas.drawText("- ${tank.name}: ${tank.currentWeight} tons (${tank.selectedCargo.name})", 50f, y, paint)
        y += 20f
    }

    // Total
    val total = viewModel.tanks.sumOf { it.weightFloat.toDouble() }
    y += 10f
    paint.isFakeBoldText = true
    canvas.drawText("TOTAL CARGO LOADED: ${total.toInt()} tons", 50f, y, paint)
    y += 40f

    // --- STABILITY ANALYSIS ---
    canvas.drawText("--- STABILITY ANALYSIS ---", 50f, y, paint)
    paint.isFakeBoldText = false
    y += 25f
    canvas.drawText("Final KG: ${String.format("%.2f", viewModel.currentKG)} m", 50f, y, paint)
    y += 20f
    canvas.drawText("Final GM: ${String.format("%.2f", viewModel.currentGM)} m", 50f, y, paint)
    y += 20f

    paint.isFakeBoldText = true
    paint.color = if (status.contains("STABLE")) android.graphics.Color.BLACK else android.graphics.Color.RED
    canvas.drawText("VESSEL STATUS: $status", 50f, y, paint)
    paint.color = android.graphics.Color.BLACK

    pdfDocument.finishPage(page)

    // Gravação na pasta Downloads
    try {
        val publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(publicDir, "CargoReport_${viewModel.activeVessel.name.replace(" ", "_")}.pdf")
        FileOutputStream(file).use { pdfDocument.writeTo(it) }
        Toast.makeText(context, "Relatório completo gerado!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}