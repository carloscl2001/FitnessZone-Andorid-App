package com.example.fitnesszone.ui.salas

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesszone.R
import java.io.FileOutputStream

// Define la clase SalasAdapter y extiende la clase
// RecyclerView.Adapter<SalasAdapter.SalasViewHolder>. Esta clase maneja el conjunto de datos
// de Salas y lo une con la vista que se va a mostrar en la lista.
class SalasAdapter(private var salas: List<Salas>) :

// Se define la clase interna SalasViewHolder, que extiende de la clase
// RecyclerView.ViewHolder.
    RecyclerView.Adapter<SalasAdapter.SalasViewHolder>() {

    // Esta clase almacena las referencias de los elementos de la vista
    // (los widgets o views) que se muestran en cada elemento de la lista.
    inner class SalasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.tituloSala)
        val imagenImageView: ImageView = itemView.findViewById(R.id.imageViewImagen)
        val descripcionTextView: TextView = itemView.findViewById(R.id.textViewDescripcion)
        val materialesTextView: TextView = itemView.findViewById(R.id.textViewMateriales)
        val capacidadTextView: TextView = itemView.findViewById(R.id.textViewCantidad)
        val precioTextView: TextView = itemView.findViewById(R.id.textViewPrecio)
        val descargarPDFButton: Button = itemView.findViewById(R.id.buttonDescargarPDF)
    }

    // El método onCreateViewHolder() se encarga de crear una nueva instancia de SalasViewHolder,
    // inflando el diseño de vista desde el archivo de diseño XML que se proporciona en el
    // parámetro viewType.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalasViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_salas, parent, false)
        return SalasViewHolder(itemView)
    }

    // El método onBindViewHolder() se llama para establecer los datos del objeto Salas en la
    // vista SalasViewHolder.
    override fun onBindViewHolder(holder: SalasAdapter.SalasViewHolder, position: Int) {
        val currentSalas = salas[position]
        holder.tituloTextView.text = currentSalas.titulo
        holder.imagenImageView.setImageResource(currentSalas.imagen)
        holder.descripcionTextView.text = currentSalas.descripcion
        holder.materialesTextView.text =  "%s %s".format("Materiales: ", currentSalas.materiales)
        holder.capacidadTextView.text =  "%d".format(currentSalas.capacidad)
        holder.precioTextView.text = "%d".format(currentSalas.precio)


        holder.descargarPDFButton.setOnClickListener {
            generateAndSavePDF(currentSalas, holder.itemView.context)
        }
    }

    private fun generateAndSavePDF(currentSalas: Salas, context: Context) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            val textPaint = TextPaint()
            textPaint.textSize = 16f
            val width = page.info.pageWidth - 20 // Reducir el ancho para dejar un margen de 10 en cada lado

            // Calcular el tamaño de la imagen para que quepa en la página del PDF
            val bitmap = BitmapFactory.decodeResource(context.resources, currentSalas.imagen)
            val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val scaledWidth = width.toFloat()
            val scaledHeight = scaledWidth / aspectRatio


            // Dibujar el título
            canvas.drawText("${currentSalas.titulo}", 10f, 50f, textPaint)


            // Dibujar la imagen debajo del título
            canvas.drawBitmap(bitmap, null, Rect(10, 80, (scaledWidth + 10).toInt(), (scaledHeight + 80).toInt()), paint)


            // Dibujar la descripción
            val descriptionText = currentSalas.descripcion
            val descriptionLayout = StaticLayout.Builder.obtain(descriptionText, 0, descriptionText.length, textPaint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .build()
            canvas.save()
            canvas.translate(10f, scaledHeight + 90f) // Ajustar la posición de la descripción
            descriptionLayout.draw(canvas)
            canvas.restore()


            // Dibujar los materiales
            val materialsText = currentSalas.materiales
            val materialsLayout = StaticLayout.Builder.obtain(materialsText, 0, materialsText.length, textPaint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .build()
            canvas.save()
            canvas.translate(10f, scaledHeight + 100f + descriptionLayout.height) // Ajustar la posición de los materiales
            materialsLayout.draw(canvas)
            canvas.restore()


            val capacityText = "Capacidad: ${currentSalas.capacidad}"
            canvas.drawText(capacityText, 10f, scaledHeight + 110f + descriptionLayout.height + materialsLayout.height, textPaint)


            // Dibujar el precio
            val priceText = "Precio: ${currentSalas.precio}"
            canvas.drawText(priceText, 10f, scaledHeight + 120f + descriptionLayout.height + materialsLayout.height + textPaint.textSize, textPaint)


            // Guardar y cerrar el PDF
            pdfDocument.finishPage(page)

            val resolver = context.contentResolver
            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            val displayName = "${currentSalas.titulo}.pdf"
            val mimeType = "application/pdf"

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = resolver.insert(collection, contentValues)

            uri?.let { pdfUri ->
                resolver.openOutputStream(pdfUri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            }

            pdfDocument.close()

            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "¡El PDF ha sido descargado!", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("Error", "Error al generar y guardar el PDF: ${e.message}")
            e.printStackTrace()
        }

    }



    // El método getItemCount() devuelve el número de elementos en la lista de Salass
    // proporcionado en el constructor de SalasAdapter.
    override fun getItemCount() = salas.size

    fun setSalas(salas: List<Salas>) {
        this.salas = salas
    }
}


