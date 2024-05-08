package com.example.fitnesszone

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.random.Random

/**
 * Implementation of App Widget functionality.
 */
// Clase utilitaria para funciones compartidas.
object Utilidades {
    fun generarTextoAleatorio(): String {
        val opciones = arrayOf("Hola", "¡Bienvenido!", "¿Qué tal?", "¡Hola mundo!")
        return opciones[Random.nextInt(opciones.size)]
    }
}

// Clase principal del widget, que extiende de AppWidgetProvider.
class MiWidgetDinamico : AppWidgetProvider() {

    // Método que se llama cuando se deben actualizar los widgets.
    override fun onUpdate(contexto: Context, administradorAppWidget: AppWidgetManager, idsAppWidget: IntArray) {
        // Itera sobre todos los IDs de widgets recibidos.
        idsAppWidget.forEach { idAppWidget ->
            actualizaWidget(contexto, administradorAppWidget, idAppWidget)
        }
    }

    fun actualizaWidget(contexto: Context, administradorAppWidget: AppWidgetManager, idAppWidget: Int){
        // Crea un intento para abrir MainActivity cuando se haga clic en el widget.
        val intento = Intent(contexto, MainActivity::class.java)
        // Crea un PendingIntent que envuelve nuestro intento, necesario para que el clic funcione.
        val intencionPendiente = PendingIntent.getActivity(contexto, 0, intento, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Crea un objeto RemoteViews para poder manipular la interfaz del widget.
        val vistas = RemoteViews(contexto.packageName, R.layout.mi_widget_dinamico)
        // Asocia el PendingIntent al widget de manera que se active cuando se haga clic en el elemento con ID texto_widget.
        vistas.setOnClickPendingIntent(R.id.texto_widget, intencionPendiente)

        // Genera un texto aleatorio para el widget.
        val textoAleatorio = Utilidades.generarTextoAleatorio()
        // Establece el texto en el widget.
        vistas.setTextViewText(R.id.texto_widget, textoAleatorio)
        // Actualiza el widget con la nueva configuración de vistas.
        administradorAppWidget.updateAppWidget(idAppWidget, vistas)

        Log.d("WidgetUpdate", "Setting text: $textoAleatorio")
    }

}

// Clase Worker para actualizar el widget de fondo.
class WorkerActualizarWidget(contexto: Context, parametrosTrabajador: WorkerParameters) :
    Worker(contexto, parametrosTrabajador) {

    // Método que realiza el trabajo de fondo.
    override fun doWork(): Result {
        // Obtiene el AppWidgetManager para acceder a los widgets.
        val administradorAppWidget = AppWidgetManager.getInstance(applicationContext)
        // Define el componente que representa nuestro widget.
        val esteWidget = ComponentName(applicationContext, MiWidgetDinamico::class.java)
        // Obtiene todos los IDs de los widgets activos de este tipo.
        val todosLosIdsWidget = administradorAppWidget.getAppWidgetIds(esteWidget)

        // Itera sobre cada ID de widget y actualiza su contenido.
        todosLosIdsWidget.forEach { idAppWidget ->
            MiWidgetDinamico().actualizaWidget(applicationContext, administradorAppWidget, idAppWidget)
        }

        // Devuelve un resultado de éxito.
        return Result.success()
    }
}