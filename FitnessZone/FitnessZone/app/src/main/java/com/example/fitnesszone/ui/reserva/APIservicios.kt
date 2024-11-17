package com.example.fitnesszone.ui.reserva

import android.util.Log
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.Files.append
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.formUrlEncode
import kotlinx.serialization.json.JsonNull.content


class APIservicios {
    private val client = HttpClient(Android)
    private  val IP_PORT="192.168.0.105"
    // suspend indica que esta función es una función de suspensión, lo que
    // significa que puede hacer llamadas de red u otras operaciones de E/S
    // de manera asíncrona sin bloquear el subproceso principal.

    suspend fun putReserva(id: String, nickname: String, numCarnet: Int, telefono: Int, email: String, sala: String, fecha: String, hora: String, numPers: Int, comentario: String): HttpResponse {
        val formParameters = Parameters.build {
            append("nickname", nickname)
            append("numCarnet", numCarnet.toString())
            append("telefono", telefono.toString())
            append("email", email)
            append("sala", sala)
            append("fecha", fecha)
            append("hora", hora)
            append("numPers", numPers.toString())
            append("comentario", comentario)
        }

        return client.put("http://$IP_PORT:3000/reservas/$id") {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }
            setBody(formParameters.formUrlEncode())
        }
    }





    suspend fun createReserva(nickname: String, numCarnet: Int, telefono: Int, email: String, sala: String, fecha: String, hora: String, numPers: Int, comentario: String): HttpResponse {
        return client.submitForm(
            url = "http://$IP_PORT:3000/reserva",
            formParameters = Parameters.build {

                append("nickname", nickname)
                append("numCarnet", numCarnet.toString())
                append("telefono", telefono.toString())
                append("email", email)
                append("sala", sala)
                append("fecha", fecha)
                append("hora", hora)
                append("numPers", numPers.toString())
                append("comentario", comentario)
            },
            encodeInQuery = false
        )

    }


    suspend fun get(): HttpResponse {
        // Obtiene la respuesta HTTP como antes
        val response = client.get("http://"+IP_PORT+":3000/reservas")
        return response
    }

    // Función para obtener una reserva por su ID
    suspend fun getReserva(id: String): HttpResponse {
        // Utiliza la función `get` del cliente HTTP para hacer una solicitud GET al servidor

        return client.get("http://$IP_PORT:3000/reservas/$id") {
            // Puedes agregar encabezados u otros parámetros de solicitud aquí si es necesario
        }
    }

    suspend fun deleteReserva(id: String): HttpResponse {
        // Utiliza la función `delete` del cliente HTTP para hacer una solicitud DELETE al servidor

        return client.delete("http://$IP_PORT:3000/reservas/$id") {
            // Puedes agregar encabezados u otros parámetros de solicitud aquí si es necesario
        }
    }

}
