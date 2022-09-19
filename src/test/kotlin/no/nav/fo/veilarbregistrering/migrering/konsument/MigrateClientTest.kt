package no.nav.fo.veilarbregistrering.migrering.konsument

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, object: TypeToken<T>() {}.type)

class MigrateClientTest {

    @Test
    fun `serialiserer liste av registrering tilstand ok`() {
        val list = Gson().fromJson<List<Map<String, Any>>>(okJson)
        assertEquals(8, list.size)
        assertTrue { list.all { it["status"] == "PUBLISERT_KAFKA" } }
    }
}

private val okJson = """[
  {
    "id": 1103445,
    "brukerRegistreringId": 1226526,
    "opprettet": "2021-10-06T13:42:46.970459+02:00",
    "sistEndret": "2021-10-06T13:42:50.227291+02:00",
    "status": "PUBLISERT_KAFKA"
  },
  {
    "id": 1103448,
    "brukerRegistreringId": 1226529,
    "opprettet": "2021-10-06T13:47:32.202021+02:00",
    "sistEndret": "2021-10-06T13:47:40.243282+02:00",
    "status": "PUBLISERT_KAFKA"
  },
  {
    "id": 1103451,
    "brukerRegistreringId": 1226532,
    "opprettet": "2021-10-06T13:48:50.326334+02:00",
    "sistEndret": "2021-10-06T13:49:00.249077+02:00",
    "status": "PUBLISERT_KAFKA"
  },
  {
    "id": 1103452,
    "brukerRegistreringId": 1226533,
    "opprettet": "2021-10-06T13:48:57.428094+02:00",
    "sistEndret": "2021-10-06T13:49:10.219139+02:00",
    "status": "PUBLISERT_KAFKA"
  },
  {
    "id": 1103454,
    "brukerRegistreringId": 1226535,
    "opprettet": "2021-10-06T13:52:02.049726+02:00",
    "sistEndret": "2021-10-06T13:52:10.214316+02:00",
    "status": "PUBLISERT_KAFKA"
  },
  {
    "id": 1103456,
    "brukerRegistreringId": 1226537,
    "opprettet": "2021-10-06T13:54:22.401876+02:00",
    "sistEndret": "2021-10-06T13:54:30.24033+02:00",
    "status": "PUBLISERT_KAFKA"
  },
  {
    "id": 1103457,
    "brukerRegistreringId": 1226538,
    "opprettet": "2021-10-06T13:55:23.406529+02:00",
    "sistEndret": "2021-10-06T13:55:30.277823+02:00",
    "status": "PUBLISERT_KAFKA"
  },
  {
    "id": 1103458,
    "brukerRegistreringId": 1226539,
    "opprettet": "2021-10-06T13:55:43.770051+02:00",
    "sistEndret": "2021-10-06T13:55:50.163148+02:00",
    "status": "PUBLISERT_KAFKA"
  }
]""".trimIndent()