package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.pdl.PdlHentPerson;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlPerson;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlPersonOpphold;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PdlServiceTest {

    private PdlOppslagService pdlOppslagService;
    private Provider<HttpServletRequest> requestProvider;

    @Before
    public void setUp() {
        requestProvider = mock(Provider.class);
        pdlOppslagService = new PdlOppslagService();
    }

    @Test
    @Disabled
    public void skalHenteOppholdTilPerson(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String tom = "16.08.2020";
        String fom = "01.01.2000";
        LocalDate localDatetom = LocalDate.parse(tom, formatter);
        LocalDate localDatefom = LocalDate.parse(fom, formatter);


        PdlResponse response = new PdlResponse();
        response.setData(PdlPerson.builder().pdlPersonOpphold(
                                    PdlPersonOpphold.builder()
                                            .oppholdFra(localDatefom)
                                            .oppholdTil(localDatetom)
                                            .build()).build());

        String fnr = "10108000398";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("fnr")).thenReturn("10108000398");
        when(requestProvider.get()).thenReturn(request);

        PdlPerson res = pdlOppslagService.hentPerson(fnr);
        Assert.assertEquals(res.getPdlPersonOpphold(), response.getData().getPdlPersonOpphold());


    }

    @Disabled
    @Test(expected = RuntimeException.class)
    public void skalFeileHvisBrukerIkkeFinnes(){

    }

}
