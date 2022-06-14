package no.nav.fo.veilarbregistrering

import com.google.gson.GsonBuilder
import java.lang.Exception
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Paths

object FileToJson {
    fun toJson(json_file: String): String {
        return try {
            val bytes = Files.readAllBytes(Paths.get(FileToJson::class.java.getResource(json_file).toURI()))
            String(bytes)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

fun toJSON(dto: Any): String? {
    val GSON = GsonBuilder().serializeNulls().create()
    return GSON.toJson(dto)
}