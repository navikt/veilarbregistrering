package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.ArbeidssokerperioderMapper.map
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEventTestdataBuilder.formidlingsgruppeEndret
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperiode
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.Arbeidssokerperioder
import no.nav.fo.veilarbregistrering.bruker.Periode
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ArbeidssokerperioderMapperTest {

    @Test
    fun kun_perioder_med_arbs_skal_være_med_i_arbeidssokerperioder() {
        val arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 3, 19).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 4, 21).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "IARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 5, 30).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 6, 13).atStartOfDay()
                ),
            )
        )
        Assertions.assertThat(arbeidssokerperioder.asList().size).isEqualTo(2)
        Assertions.assertThat(arbeidssokerperioder.asList()[0].periode.fra).isEqualTo(LocalDate.of(2020, 3, 19))
        Assertions.assertThat(arbeidssokerperioder.asList()[1].periode.fra).isEqualTo(LocalDate.of(2020, 6, 13))
    }

    @Test
    fun perioder_skal_være_sortert_med_eldste_først() {
        val arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 3, 19).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 4, 21).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 5, 30).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "IARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 6, 13).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 8, 13).atStartOfDay()
                ),
            )
        )

        Assertions.assertThat(arbeidssokerperioder.asList().size).isEqualTo(3)
        Assertions.assertThat(arbeidssokerperioder.asList())
            .isEqualTo(arbeidssokerperioder.asList().sortedBy { it.periode.fra })
    }

    @Test
    fun kun_siste_periode_kan_ha_blank_tildato() {
        val arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 3, 19).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 4, 21).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 5, 30).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 6, 13).atStartOfDay()
                ),
            )
        )

        Assertions.assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isNotNull
        Assertions.assertThat(funnetTilDatoForSistePeriode(arbeidssokerperioder)).isNull()
    }

    @Test
    fun foerste_periode_skal_ha_tildato_lik_dagen_foer_andre_formidlingsgruppeendring_sin_fradato() {
        val arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 3, 19).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 4, 21).atStartOfDay()
                ),
            )
        )

        Assertions.assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 4, 20))
    }

    @Test
    fun skal_populere_tildato_korrekt_selv_om_listen_kommer_usortert() {
        val arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 5, 30).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "IARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 6, 12).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 3, 19).atStartOfDay()
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    LocalDate.of(2020, 4, 21).atStartOfDay()
                )
            )
        )

        Assertions.assertThat(funnetFraDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 3, 19))
        Assertions.assertThat(funnetFraDatoForIndeks(1, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 5, 30))
        Assertions.assertThat(funnetTilDatoForIndeks(0, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 4, 20))
        Assertions.assertThat(funnetTilDatoForIndeks(1, arbeidssokerperioder)).isEqualTo(LocalDate.of(2020, 6, 11))
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
                formidlingsgruppeEndret("ARBS", "4397692", "AKTIV", now),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    now.plusSeconds(2)
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    now.plusSeconds(4)
                ),
            )
        )

        Assertions.assertThat(arbeidssokerperioder.asList().size).isEqualTo(1)
        Assertions.assertThat(arbeidssokerperioder.asList()[0].periode.fra).isEqualTo(now.toLocalDate())
    }

    @Test
    fun skal_kun_beholde_siste_formidlingsgruppeendring_fra_samme_dag_flere_dager() {
        val now = LocalDateTime.now()
        val arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret("ISERV", "4397692", "AKTIV", now),
                formidlingsgruppeEndret(
                    "IARBS",
                    "4397692",
                    "AKTIV",
                    now.plusSeconds(2)
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    now.plusSeconds(4)
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    now.plusDays(7)
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    now.plusDays(7).plusSeconds(3)
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    now.plusMonths(1)
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    now.plusMonths(1).plusSeconds(2)
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    now.plusMonths(1).plusSeconds(5)
                ),
            )
        )

        Assertions.assertThat(arbeidssokerperioder.asList().size).isEqualTo(2)
        Assertions.assertThat(arbeidssokerperioder.asList()[0].periode.fra).isEqualTo(now.toLocalDate())
        Assertions.assertThat(arbeidssokerperioder.asList()[1].periode.fra).isEqualTo(now.plusDays(7).toLocalDate())
        Assertions.assertThat(arbeidssokerperioder.asList()[0].periode.til).isEqualTo(now.plusDays(6).toLocalDate())
        Assertions.assertThat(arbeidssokerperioder.asList()[1].periode.til).isEqualTo(now.plusMonths(1).minusDays(1).toLocalDate())
    }

    @Test
    fun skal_filtrere_bort_endringer_for_duplikate_identer() {
        val arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret(
                    "ISERV",
                    "4397692",
                    "AKTIV",
                    LocalDateTime.of(2019, 3, 6, 10, 10)
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4451554",
                    "DUPLIKAT_TIL_BEH",
                    LocalDateTime.of(2019, 9, 11, 10, 10)
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4451554",
                    "DUPLIKAT_TIL_BEH",
                    LocalDateTime.of(2019, 9, 11, 10, 10)
                ),
                formidlingsgruppeEndret(
                    "ARBS",
                    "4397692",
                    "AKTIV",
                    LocalDateTime.of(2019, 12, 9, 10, 10)
                ),
                formidlingsgruppeEndret(
                    "ISERV",
                    "4451554",
                    "DUPLIKAT_TIL_BEH",

                    LocalDateTime.of(2019, 12, 18, 10, 10)
                )
            )
        )

        val aktivArbeidssokerperiode =
            Arbeidssokerperiode.of(Periode(LocalDate.of(2019, 12, 9), null))
        Assertions.assertThat(arbeidssokerperioder.asList()).containsExactly(aktivArbeidssokerperiode)
    }

    @Test
    fun skal_filtrere_bort_tekniske_ISERVendringer_for_ARBS() {

        var arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret(
                    "ARBS", "4685858", "AKTIV",
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                ),
                formidlingsgruppeEndret(
                    "ISERV", "4685858", "AKTIV",
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                ),
                formidlingsgruppeEndret(
                    "ISERV", "4685858", "AKTIV",
                        LocalDateTime.of(2020, 9, 9, 9, 9, 9)
                ),
                formidlingsgruppeEndret(
                    "ARBS", "4685858", "AKTIV",
                        LocalDateTime.of(2020, 9, 9, 9, 9, 9)
                ),
            )
        )

        var arbeidssokerperiode1 = Arbeidssokerperiode.of(
            Periode(LocalDate.of(2020, 8, 14), LocalDate.of(2020, 9, 8))
        )
        val arbeidssokerperiode2 = Arbeidssokerperiode.of(Periode(LocalDate.of(2020, 9, 9), null))

        Assertions.assertThat(arbeidssokerperioder.asList()).containsExactly(
            arbeidssokerperiode1,
            arbeidssokerperiode2
        )

        arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret(
                    "ARBS", "4685858", "AKTIV",
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                ),
                formidlingsgruppeEndret(
                    "ISERV", "4685858", "AKTIV",
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                ),
            )
        )
        Assertions.assertThat(arbeidssokerperioder.asList().size).isEqualTo(1)

        arbeidssokerperioder = map(
            listOf(
                formidlingsgruppeEndret(
                    "ARBS", "4685858", "AKTIV",
                        LocalDateTime.of(2020, 8, 14, 22, 7, 15)
                ),
            )
        )

        arbeidssokerperiode1 = Arbeidssokerperiode.of(Periode(LocalDate.of(2020, 8, 14), null))
        Assertions.assertThat(arbeidssokerperioder.asList()).containsExactly(
            arbeidssokerperiode1
        )
    }
}