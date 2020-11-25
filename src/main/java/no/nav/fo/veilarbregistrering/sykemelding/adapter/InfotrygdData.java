package no.nav.fo.veilarbregistrering.sykemelding.adapter;

public class InfotrygdData {
    public String maksDato;

    public InfotrygdData(String maksDato) {
        this.maksDato = maksDato;
    }

    public InfotrygdData() {
    }

    public String getMaksDato() {
        return this.maksDato;
    }

    public void setMaksDato(String maksDato) {
        this.maksDato = maksDato;
    }

    public InfotrygdData withMaksDato(String maksDato) {
        return this.maksDato == maksDato ? this : new InfotrygdData(maksDato);
    }
}