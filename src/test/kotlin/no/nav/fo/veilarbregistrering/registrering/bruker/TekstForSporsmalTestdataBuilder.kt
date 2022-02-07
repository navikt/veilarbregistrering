package no.nav.fo.veilarbregistrering.registrering.bruker

object TekstForSporsmalTestdataBuilder {

    fun gyldigeTeksterForBesvarelse(): List<TekstForSporsmal> {
        val tekster: MutableList<TekstForSporsmal> = ArrayList()
        tekster.add(
            TekstForSporsmal(
                "utdanning",
                "Hva er din høyeste fullførte utdanning?",
                "Høyere utdanning (5 år eller mer)"
            )
        )
        tekster.add(TekstForSporsmal("utdanningBestatt", "Er utdanningen din bestått?", "Ja"))
        tekster.add(TekstForSporsmal("utdanningGodkjent", "Er utdanningen din godkjent i Norge?", "Nei"))
        tekster.add(
            TekstForSporsmal(
                "helseHinder",
                "Trenger du oppfølging i forbindelse med helseutfordringer?",
                "Nei"
            )
        )
        tekster.add(
            TekstForSporsmal(
                "andreForhold",
                "Trenger du oppfølging i forbindelse med andre utfordringer?",
                "Nei"
            )
        )
        tekster.add(TekstForSporsmal("sisteStilling", "Din siste jobb", "Har hatt jobb"))
        tekster.add(
            TekstForSporsmal(
                "dinSituasjon",
                "Hvorfor registrerer du deg?",
                "Jeg er permittert eller vil bli permittert"
            )
        )
        return tekster
    }

    fun gyldigeTeksterForSykmeldtBesvarelse(): List<TekstForSporsmal> {
        val tekster: MutableList<TekstForSporsmal> = ArrayList()
        tekster.add(
            TekstForSporsmal(
                "fremtidigSituasjon",
                "Hva tenker du om din fremtidige situasjon?",
                "Jeg skal tilbake til jobben jeg har"
            )
        )
        tekster.add(
            TekstForSporsmal(
                "tilbakeIArbeid",
                "Tror du at du kommer tilbake i jobb før du har vært sykmeldt i 52 uker?",
                "Nei"
            )
        )
        return tekster
    }
}
