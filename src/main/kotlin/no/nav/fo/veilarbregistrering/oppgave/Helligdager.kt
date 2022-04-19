package no.nav.fo.veilarbregistrering.oppgave

import java.time.LocalDate

/**
 * Inneholder kjente helligdager i Norge, til og med 2024.
 */
internal object Helligdager {

    fun erHelligdag(dag: LocalDate): Boolean {
        return HELLIGDAGER.contains(dag)
    }

    private val HELLIGDAGER = listOf(
        LocalDate.of(2020,1,1),
        LocalDate.of(2020,4,5),
        LocalDate.of(2020,4,9),
        LocalDate.of(2020,4,10),
        LocalDate.of(2020,4,12),
        LocalDate.of(2020,4,13),
        LocalDate.of(2020,5,1),
        LocalDate.of(2020,5,17),
        LocalDate.of(2020,5,21),
        LocalDate.of(2020,5,31),
        LocalDate.of(2020,6,1),
        LocalDate.of(2020,12,25),
        LocalDate.of(2020,12,26),
        LocalDate.of(2021,1,1),
        LocalDate.of(2021,3,28),
        LocalDate.of(2021,4,1),
        LocalDate.of(2021,4,2),
        LocalDate.of(2021,4,4),
        LocalDate.of(2021,4,5),
        LocalDate.of(2021,5,1),
        LocalDate.of(2021,5,13),
        LocalDate.of(2021,5,17),
        LocalDate.of(2021,5,23),
        LocalDate.of(2021,5,24),
        LocalDate.of(2021,12,25),
        LocalDate.of(2021,12,26),
        LocalDate.of(2022,1,1),
        LocalDate.of(2022,4,10),
        LocalDate.of(2022,4,14),
        LocalDate.of(2022,4,15),
        LocalDate.of(2022,4,17),
        LocalDate.of(2022,4,18),
        LocalDate.of(2022,5,1),
        LocalDate.of(2022,5,17),
        LocalDate.of(2022,5,26),
        LocalDate.of(2022,6,5),
        LocalDate.of(2022,6,6),
        LocalDate.of(2022,12,25),
        LocalDate.of(2022,12,26),
        LocalDate.of(2023,1,1),
        LocalDate.of(2023,4,2),
        LocalDate.of(2023,4,6),
        LocalDate.of(2023,4,7),
        LocalDate.of(2023,4,9),
        LocalDate.of(2023,4,10),
        LocalDate.of(2023,5,1),
        LocalDate.of(2023,5,17),
        LocalDate.of(2023,5,18),
        LocalDate.of(2023,5,28),
        LocalDate.of(2023,5,29),
        LocalDate.of(2023,12,25),
        LocalDate.of(2023,12,26),
        LocalDate.of(2024,1,1),
        LocalDate.of(2024,3,24),
        LocalDate.of(2024,3,28),
        LocalDate.of(2024,3,29),
        LocalDate.of(2024,3,31),
        LocalDate.of(2024,4,1),
        LocalDate.of(2024,5,1),
        LocalDate.of(2024,5,9),
        LocalDate.of(2024,5,17),
        LocalDate.of(2024,5,19),
        LocalDate.of(2024,5,20),
        LocalDate.of(2024,12,25),
        LocalDate.of(2024,12,2),
    )
}