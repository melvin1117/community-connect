package com.su.communityconnect.model.provider


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.Ticket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PDFProvider {
    suspend fun createTicketPdf(context: Context, ticket: Ticket, event: Event, qrCodeBitmap: Bitmap?): Result<String> {
        return try {
            // Initialize PDF Document
            val pdfDocument = PdfDocument()

            // Create a page
            val pageInfo = PdfDocument.PageInfo.Builder(450, 450, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint()

            // Draw Event Details
            paint.textSize = 12f
            canvas.drawText("Event: ${event.title}", 10f, 20f, paint)
            canvas.drawText("Location: ${event.location.displayName}, ${event.location.fullAddress}", 10f, 40f, paint)
            canvas.drawText("Date: ${event.eventTimestamp.date}", 10f, 60f, paint)
            canvas.drawText("Time: ${event.eventTimestamp.time}", 10f, 80f, paint)
            canvas.drawText("Tickets: ${ticket.quantity}", 10f, 100f, paint)

            // Draw QR Code
            qrCodeBitmap?.let { bitmap ->
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
                canvas.drawBitmap(scaledBitmap, 100f, 120f, paint)
            }

            // Finish page
            pdfDocument.finishPage(page)

            // Define the file path
            val filePath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "${event.title}_${ticket.id}.pdf"
            )

            // Write the document content
            withContext(Dispatchers.IO) {
                pdfDocument.writeTo(FileOutputStream(filePath))
            }

            // Close the document
            pdfDocument.close()

            // Return the file path on success
            Result.success(filePath.absolutePath)
        } catch (e: Exception) {
            // Return the exception message on failure
            Result.failure(e)
        }
    }
}

