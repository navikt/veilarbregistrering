package no.nav.fo.veilarbregistrering.enhet.adapter;

public class ForretningsAdresseDto {

    private final String kommunenummer;
    private final GyldighetsperiodeDto gyldighetsperiode;

    public ForretningsAdresseDto(String kommunenummer, GyldighetsperiodeDto gyldighetsperiode) {
        this.kommunenummer = kommunenummer;
        this.gyldighetsperiode = gyldighetsperiode;
    }

    public String getKommunenummer() {
        return kommunenummer;
    }

    public GyldighetsperiodeDto getGyldighetsperiode() {
        return gyldighetsperiode;
    }
}