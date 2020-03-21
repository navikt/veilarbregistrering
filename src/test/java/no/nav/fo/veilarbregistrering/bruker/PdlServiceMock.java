package no.nav.fo.veilarbregistrering.bruker;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

public class PdlServiceMock extends PdlOppslagService{

    public PdlServiceMock(){
        super();
    }


    public String getFnr() {
        return "10108000398"; //Aremark fiktivt fnr.
    }



}
