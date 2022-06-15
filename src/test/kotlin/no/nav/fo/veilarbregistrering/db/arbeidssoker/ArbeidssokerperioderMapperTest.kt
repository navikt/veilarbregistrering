package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppeperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.Periode
import no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerperioderMapper.map
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

class ArbeidssokerperioderMapperTest {
    @Test
    fun kun_siste_periode_kan_ha_blank_tildato() {
        val arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring(
                    "ARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(LocalDate.of(2020, 3, 19).atStartOfDay())
                ),
                Formidlingsgruppeendring(
                    "ISERV",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(LocalDate.of(2020, 4, 21).atStartOfDay())
                ),
                Formidlingsgruppeendring(
                    "ISERV",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(LocalDate.of(2020, 5, 30).atStartOfDay())
                ),
            )
        )

        Assertions.assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isNotNull
        Assertions.assertThat(funnetTilDatoForIndeks(1, arbeidssokerperioder)).isNotNull
        Assertions.assertThat(funnetTilDatoForSistePeriode(arbeidssokerperioder)).isNull()
    }

    @Test
    fun foerste_periode_skal_ha_tildato_lik_dagen_foer_andre_periode_sin_fradato() {
        val arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring(
                    "ARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(LocalDate.of(2020, 3, 19).atStartOfDay())
                ),
                Formidlingsgruppeendring(
                    "ISERV",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(LocalDate.of(2020, 4, 21).atStartOfDay())
                ),
            )
        )

        Assertions.assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 4, 20))
        Assertions.assertThat(funnetTilDatoForSistePeriode(arbeidssokerperioder)).isNull()
    }

    @Test
    fun skal_populere_tildato_korrekt_selv_om_listen_kommer_usortert() {
        val arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring(
                    "ARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(LocalDate.of(2020, 5, 30).atStartOfDay())
                ),
                Formidlingsgruppeendring(
                    "ARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(LocalDate.of(2020, 3, 19).atStartOfDay())
                ),
                Formidlingsgruppeendring(
                    "ISERV",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(LocalDate.of(2020, 4, 21).atStartOfDay())
                )
            )
        )

        Assertions.assertThat(funnetFraDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 3, 19))
        Assertions.assertThat(funnetFraDatoForIndeks(1, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 4, 21))
        Assertions.assertThat(funnetFraDatoForIndeks(2, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 5, 30))
        Assertions.assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 4, 20))
        Assertions.assertThat(funnetTilDatoForIndeks(1, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 5, 29))
        Assertions.assertThat(funnetTilDatoForSistePeriode(arbeidssokerperioder)).isNull()
    }

    private fun funnetFraDatoForIndeks(indeks: Int, arbeidssokerperioder: Arbeidssokerperioder): LocalDate {
        return arbeidssokerperioder.asList()[indeks].periode.fra
    }

    private fun funnetTilDatoForSistePeriode(arbeidssokerperioder: Arbeidssokerperioder): LocalDate? =
        arbeidssokerperioder.asList().last().periode.til


    private fun funnetTilDatoForIndeks(indeks: Int, arbeidssokerperioder: Arbeidssokerperioder): LocalDate? {
        return arbeidssokerperioder.asList()[indeks].periode.til
    }

    @Test
    fun skal_kun_beholde_siste_formidlingsgruppeendring_fra_samme_dag() {
        val now = LocalDateTime.now()
        val arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now)),
                Formidlingsgruppeendring(
                    "ARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(now.plusSeconds(2))
                ),
                Formidlingsgruppeendring(
                    "IARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(now.plusSeconds(4))
                ),
            )
        )

        Assertions.assertThat(arbeidssokerperioder.asList().size).isEqualTo(1)
        Assertions.assertThat(arbeidssokerperioder.asList()[0].formidlingsgruppe.kode).isEqualTo("IARBS")
        Assertions.assertThat(arbeidssokerperioder.asList()[0].periode.fra).isEqualTo(now.toLocalDate())
    }

    @Test
    fun skal_kun_beholde_siste_formidlingsgruppeendring_fra_samme_dag_flere_dager() {
        val now = LocalDateTime.now()
        val arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring("ISERV", 4397692, "AKTIV", Timestamp.valueOf(now)),
                Formidlingsgruppeendring(
                    "ARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(now.plusSeconds(2))
                ),
                Formidlingsgruppeendring(
                    "IARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(now.plusSeconds(4))
                ),
                Formidlingsgruppeendring(
                    "ISERV",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(now.plusDays(7))
                ),
                Formidlingsgruppeendring(
                    "ARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(now.plusDays(7).plusSeconds(3))
                ),
                Formidlingsgruppeendring(
                    "ISERV",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(now.plusDays(50))
                ),
                Formidlingsgruppeendring(
                    "ARBS",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(now.plusDays(50).plusSeconds(2))
                ),
                Formidlingsgruppeendring(
                    "ISERV",
                    4397692,
                    "AKTIV",
                    Timestamp.valueOf(now.plusDays(50).plusSeconds(5))
                ),
            )
        )

        Assertions.assertThat(arbeidssokerperioder.asList().size).isEqualTo(3)
        Assertions.assertThat(arbeidssokerperioder.asList()[0].formidlingsgruppe.kode).isEqualTo("IARBS")
        Assertions.assertThat(arbeidssokerperioder.asList()[1].formidlingsgruppe.kode).isEqualTo("ARBS")
        Assertions.assertThat(arbeidssokerperioder.asList()[2].formidlingsgruppe.kode).isEqualTo("ISERV")
        Assertions.assertThat(arbeidssokerperioder.asList()[0].periode.fra).isEqualTo(now.toLocalDate())
        Assertions.assertThat(arbeidssokerperioder.asList()[1].periode.fra).isEqualTo(now.plusDays(7).toLocalDate())
        Assertions.assertThat(arbeidssokerperioder.asList()[2].periode.fra).isEqualTo(now.plusDays(50).toLocalDate())
    }

    @Test
    fun skal_filtrere_bort_endringer_for_duplikate_identer() {
        val arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring(
                    "ISERV", 4397692, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2019, 3, 6, 10, 10)
                    )
                ),
                Formidlingsgruppeendring(
                    "ISERV", 4451554, "DUPLIKAT_TIL_BEH", Timestamp.valueOf(
                        LocalDateTime.of(2019, 9, 11, 10, 10)
                    )
                ),
                Formidlingsgruppeendring(
                    "ARBS", 4451554, "DUPLIKAT_TIL_BEH", Timestamp.valueOf(
                        LocalDateTime.of(2019, 9, 11, 10, 10)
                    )
                ),
                Formidlingsgruppeendring(
                    "ARBS", 4397692, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2019, 12, 9, 10, 10)
                    )
                ),
                Formidlingsgruppeendring(
                    "ISERV", 4451554, "DUPLIKAT_TIL_BEH", Timestamp.valueOf(
                        LocalDateTime.of(2019, 12, 18, 10, 10)
                    )
                )
            )
        )

        val iservAktiv = Formidlingsgruppeperiode.of(
            Formidlingsgruppe("ISERV"),
            Periode(LocalDate.of(2019, 3, 6), LocalDate.of(2019, 12, 8))
        )
        val arbsAktiv =
            Formidlingsgruppeperiode.of(Formidlingsgruppe("ARBS"), Periode(LocalDate.of(2019, 12, 9), null))
        Assertions.assertThat(arbeidssokerperioder.asList()).containsExactly(
            iservAktiv,
            arbsAktiv
        )
    }

    @Test
    fun skal_filtrere_bort_tekniske_ISERVendringer_for_ARBS() {

        var arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring(
                    "ARBS", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                    )
                ),
                Formidlingsgruppeendring(
                    "ISERV", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                    )
                ),
                Formidlingsgruppeendring(
                    "ISERV", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 9, 9, 9, 9, 9)
                    )
                ),
                Formidlingsgruppeendring(
                    "ARBS", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 9, 9, 9, 9, 9)
                    )
                ),
            )
        )

        var arbs1 = Formidlingsgruppeperiode.of(
            Formidlingsgruppe("ARBS"),
            Periode(LocalDate.of(2020, 8, 14), LocalDate.of(2020, 9, 8))
        )
        val arbs2 = Formidlingsgruppeperiode.of(Formidlingsgruppe("ARBS"), Periode(LocalDate.of(2020, 9, 9), null))
        Assertions.assertThat(arbeidssokerperioder.asList()).containsExactly(
            arbs1,
            arbs2
        )

        arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring(
                    "ARBS", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                    )
                ),
                Formidlingsgruppeendring(
                    "ISERV", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                    )
                ),
            )
        )

        arbs1 = Formidlingsgruppeperiode.of(Formidlingsgruppe("ARBS"), Periode(LocalDate.of(2020, 8, 14), null))
        Assertions.assertThat(arbeidssokerperioder.asList()).containsExactly(
            arbs1
        )

        arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring(
                    "ARBS", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                    )
                ),
            )
        )

        arbs1 = Formidlingsgruppeperiode.of(Formidlingsgruppe("ARBS"), Periode(LocalDate.of(2020, 8, 14), null))
        Assertions.assertThat(arbeidssokerperioder.asList()).containsExactly(
            arbs1
        )
    }

    @Test
    fun skal_filtrere_bort_tekniske_ISERVendringer_med_IARBS() {
        val arbeidssokerperioder = map(
            listOf(
                Formidlingsgruppeendring(
                    "IARBS", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                    )
                ),
                Formidlingsgruppeendring(
                    "ISERV", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                    )
                ),
                Formidlingsgruppeendring(
                    "ISERV", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 9, 9, 9, 9, 9)
                    )
                ),
                Formidlingsgruppeendring(
                    "IARBS", 4685858, "AKTIV", Timestamp.valueOf(
                        LocalDateTime.of(2020, 9, 9, 9, 9, 9)
                    )
                ),
            )
        )

        val arbs1 = Formidlingsgruppeperiode.of(
            Formidlingsgruppe("IARBS"),
            Periode(LocalDate.of(2020, 8, 14), LocalDate.of(2020, 9, 8))
        )
        val arbs2 = Formidlingsgruppeperiode.of(Formidlingsgruppe("IARBS"), Periode(LocalDate.of(2020, 9, 9), null))
        Assertions.assertThat(arbeidssokerperioder.asList()).containsExactly(
            arbs1,
            arbs2
        )
    }
}