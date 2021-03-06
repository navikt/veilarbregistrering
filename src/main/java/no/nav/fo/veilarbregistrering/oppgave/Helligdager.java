package no.nav.fo.veilarbregistrering.oppgave;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Inneholder kjente helligdager i Norge, til og med 2024.
 */
class Helligdager {

    private static final DateTimeFormatter YYYY_MM_dd = DateTimeFormatter.ofPattern("YYYY-MM-dd");

    static boolean erHelligdag(LocalDate dag) {
        return HELLIGDAGER.contains(dag.format(YYYY_MM_dd));
    }

    private static final List<String> HELLIGDAGER = Arrays.asList(
            "2020-01-01",
            "2020-04-05",
            "2020-04-09",
            "2020-04-10",
            "2020-04-12",
            "2020-04-13",
            "2020-05-01",
            "2020-05-17",
            "2020-05-21",
            "2020-05-31",
            "2020-06-01",
            "2020-12-25",
            "2020-12-26",
            "2021-01-01",
            "2021-03-28",
            "2021-04-01",
            "2021-04-02",
            "2021-04-04",
            "2021-04-05",
            "2021-05-01",
            "2021-05-13",
            "2021-05-17",
            "2021-05-23",
            "2021-05-24",
            "2021-12-25",
            "2021-12-26",
            "2022-01-01",
            "2022-04-10",
            "2022-04-14",
            "2022-04-15",
            "2022-04-17",
            "2022-04-18",
            "2022-05-01",
            "2022-05-17",
            "2022-05-26",
            "2022-06-05",
            "2022-06-06",
            "2022-12-25",
            "2022-12-26",
            "2023-01-01",
            "2023-04-02",
            "2023-04-06",
            "2023-04-07",
            "2023-04-09",
            "2023-04-10",
            "2023-05-01",
            "2023-05-17",
            "2023-05-18",
            "2023-05-28",
            "2023-05-29",
            "2023-12-25",
            "2023-12-26",
            "2024-01-01",
            "2024-03-24",
            "2024-03-28",
            "2024-03-29",
            "2024-03-31",
            "2024-04-01",
            "2024-05-01",
            "2024-05-09",
            "2024-05-17",
            "2024-05-19",
            "2024-05-20",
            "2024-12-25",
            "2024-12-26");
}
