package com.stockary.common.repository.updater

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.io.File
import java.io.IOException
import kotlin.math.round
import kotlin.system.exitProcess

class VersionRepo {
    // setting up ktor's client: https://ktor.io/docs/client.html
    private val client = HttpClient(CIO) {
        if (!System.getenv("DEBUG").isNullOrBlank()) {
            install(Logging)
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }
    private var _os = ""

    companion object {
        private val UPDATER_URL = System.getenv("UPDATER_URL") ?: "http://localhost:1000"
    }

    init {
        // Get operating system's name
        // You can also add linux if you want to
        val os = System.getProperty("os.name")
        if (os.startsWith("Mac")) {
            _os = "mac"
        } else if (os.startsWith("Windows")) {
            _os = "windows"
        }
    }

    suspend fun checkForUpdate() = flow {
        try {
            val version: Version = client.get("$UPDATER_URL/update/$_os/latest") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                }
            }.body()
            emit(version)
        } catch (e: Throwable) {
            throw e
        }
    }


    suspend fun downloadUpdate(downloadUrl: String) = channelFlow {
        val response: HttpResponse = client.get(downloadUrl) {
            headers {
                append(HttpHeaders.Accept, "application/octet-stream")
                append(HttpHeaders.Connection, "keep-alive")
            }
            onDownload { bytesSentTotal, contentLength ->
                val percentage = round(((bytesSentTotal * 100) / contentLength).toDouble())
                val bar = round((25 * percentage) / 100)

                val formatted = "$percentage% [${"=".repeat(bar.toInt())}>${" ".repeat(25 - bar.toInt())}]"
                println("Downloading ($bytesSentTotal / ${contentLength}) [$formatted]")
                launch {
                    send(
                        DownloadProgress(
                            size = contentLength,
                            currentSize = bytesSentTotal,
                            percentage = percentage.toInt()
                        )
                    )
                }
            }
        }
        val body: ByteArray = response.body()
        val headers = response.headers

        var filename = ""
        val disposition = headers[HttpHeaders.ContentDisposition]
        if (!disposition.isNullOrBlank() && disposition.indexOf("attachment") != -1) {
            val filenameRegex = Regex("filename[^;=\\n]*=((['\"]).*?\\2|[^;\\n]*)")
            val matches = filenameRegex.find(disposition)
            if (matches != null) {
                filename = matches.value.replace(Regex("['\"]"), "").replace("filename=", "")
            }
        }
        val path = "${System.getProperty("java.io.tmpdir")}$filename"
        val file = File(path)
        file.writeBytes(body)
        withContext(Dispatchers.IO) {
            openInstaller(file)
        }
    }

    private fun openInstaller(file: File) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file)
                exitProcess(0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}