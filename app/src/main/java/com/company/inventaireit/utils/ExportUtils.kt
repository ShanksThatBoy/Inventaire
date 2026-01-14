package com.company.inventaireit.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.company.inventaireit.data.entity.ScannedItemEntity
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    /**
     * Génère le contenu CSV personnalisé
     * Séparateur : ; (Excel FR)
     */
    fun generateCsvData(items: List<ScannedItemEntity>): String {
        val sb = StringBuilder()
        // En-têtes personnalisables
        sb.append("CODE_BARRES;SITE;ETAGE;BUREAU;DATE_HEURE;STATUT\n")
        
        for (item in items) {
            val status = if (item.isDuplicate) "DOUBLON" else "NORMAL"
            sb.append("${item.codeValue};")
            sb.append("${item.site};")
            sb.append("${item.floor};")
            sb.append("${item.room};")
            sb.append("${dateFormat.format(Date(item.scannedAt))};")
            sb.append("$status\n")
        }
        return sb.toString()
    }

    /**
     * Génère le contenu TXT (Tabulé)
     */
    fun generateTxtData(items: List<ScannedItemEntity>): String {
        val sb = StringBuilder()
        sb.append("INVENTAIRE ACOSS 2026 - RAPPORT\n")
        sb.append("=" .repeat(40) + "\n")
        sb.append("CODE\tEMPLACEMENT\tDATE\n")
        
        for (item in items) {
            sb.append("${item.codeValue}\t${item.floor}-${item.room}\t${dateFormat.format(Date(item.scannedAt))}\n")
        }
        return sb.toString()
    }

    /**
     * Génère un PDF simple
     */
    fun generatePdfDocument(items: List<ScannedItemEntity>): PdfDocument {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        
        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("Inventaire Acoss 2026", 40f, 50f, paint)
        
        paint.textSize = 10f
        paint.isFakeBoldText = false
        var y = 100f
        
        for (item in items.take(35)) {
            canvas.drawText("${item.codeValue} | ${item.floor} | ${item.room} | ${dateFormat.format(Date(item.scannedAt))}", 40f, y, paint)
            y += 20f
        }
        
        document.finishPage(page)
        return document
    }
}