package no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson;

import java.util.List;
import java.util.Optional;

public class PdlPerson {
    private List<PdlTelefonnummer> telefonnummer;
    private List<PdlFoedsel> foedsel;
    private List<PdlAdressebeskyttelse> adressebeskyttelse;

    public PdlPerson() {
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

    public Optional<PdlAdressebeskyttelse> strengesteAdressebeskyttelse() {
        if (adressebeskyttelse == null || adressebeskyttelse.isEmpty()) {
            return Optional.empty();
        }

        return adressebeskyttelse.stream()
                .sorted()
                .findFirst();
    }

    public List<PdlAdressebeskyttelse> getAdressebeskyttelse() {
        return adressebeskyttelse;
    }

    public void setAdressebeskyttelse(List<PdlAdressebeskyttelse> adressebeskyttelse) {
        this.adressebeskyttelse = adressebeskyttelse;
    }
}
