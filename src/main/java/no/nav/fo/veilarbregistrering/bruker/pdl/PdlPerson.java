package no.nav.fo.veilarbregistrering.bruker.pdl;

import java.util.List;
import java.util.Optional;

public class PdlPerson {
    private List<PdlPersonOpphold> opphold;
    private List<PdlStatsborgerskap> statsborgerskap;
    private PdlTelefonnummer telefonnummer;
    private PdlFoedsel foedsel;

    public PdlPerson() {
    }

    public List<PdlPersonOpphold> getOpphold() {
        return opphold;
    }

    public void setOpphold(List<PdlPersonOpphold> opphold) {
        this.opphold = opphold;
    }

    public List<PdlStatsborgerskap> getStatsborgerskap() {
        return statsborgerskap;
    }

    public void setStatsborgerskap(List<PdlStatsborgerskap> statsborgerskap) {
        this.statsborgerskap = statsborgerskap;
    }

    public Optional<PdlStatsborgerskap> getSisteStatsborgerskap() {
        if (this.statsborgerskap.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.statsborgerskap.get(this.statsborgerskap.size() - 1));
    }

    public Optional<PdlPersonOpphold> getSisteOpphold() {
        if (this.opphold.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.opphold.get(this.opphold.size() - 1));
    }

    public PdlTelefonnummer getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(PdlTelefonnummer telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public PdlFoedsel getFoedsel() {
        return foedsel;
    }

    public void setFoedsel(PdlFoedsel foedsel) {
        this.foedsel = foedsel;
    }
}
