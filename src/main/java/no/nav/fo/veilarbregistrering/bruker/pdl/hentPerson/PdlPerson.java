package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

import java.util.List;
import java.util.Optional;

public class PdlPerson {
    private List<PdlPersonOpphold> opphold;
    private List<PdlStatsborgerskap> statsborgerskap;
    private List<PdlTelefonnummer> telefonnummer;
    private List<PdlFoedsel> foedsel;

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

    public Optional<PdlTelefonnummer> hoyestPrioriterteTelefonnummer() {
        if (this.telefonnummer.isEmpty()) {
            return Optional.empty();
        }
        return telefonnummer.stream()
                .sorted()
                .findFirst();
    }

    public List<PdlTelefonnummer> getTelefonnummer() {
        return this.telefonnummer;
    }

    public void setTelefonnummer(List<PdlTelefonnummer> telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public Optional<PdlFoedsel> getSistePdlFoedsel() {
        if (this.foedsel.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.foedsel.get(this.foedsel.size() - 1));
    }

    public List<PdlFoedsel> getFoedsel() {
        return this.foedsel;
    }

    public void setFoedsel(List<PdlFoedsel> foedsel) {
        this.foedsel = foedsel;
    }
}
