
Dette har vi allerede i dag
/api/arbeidssoker/periode : GET -> List<Arbeidssøkerperioder (fraDato - tilDato)>
--> I fremtiden skal dette hente persisterte data hos oss

Men dette endepunktet henter data synkront fra Arena sin ORDS-tjeneste (Oracle REST DataSource)

Tidligere steg:
Arena har publisert alle endringer av formidlingsgruppe siden 2010 på Kafka + alle som hadde ARBS 1.1.2010.
Vi har lest av og lagret ned alle disse endringene i egen tabell.

Neste steg:
Bygge en lokal kopi av disse arbeidssøkerperiodene basert på formidlingsgruppeendringer som vi leser fra Kafka
- dette vil mest sannsynlig gjenskape "mangel" ved at vi er noen minutter (?) forsinket

Utsetter denne utvidelsen til formidlingsgrupper er OK
Inkludere egen inngang (ordinær registrering og reaktivering)

/api/fullfoerordinaerregistrering : POST -> lagrer jo ned en ordinær registrering + aktiverer bruker i Arena
/api/fullfoerreaktiering : POST -> lagrere ned en reaktivering + reaktiverer bruker i Arena
Kafka: vi mottar en ISERV/IARBS fra Arena

Begge disse POST-kallene vil kunne starte en ny periode
 - hvis OK så startes ny periode som lagres ned
 - og da vil jo /api/arbeidssøker/periode få med seg ny periode

 - [start på en periode utledes av ordinær registrering (våre data) .... slutt på periode utledes av formidlingsgruppe ISERV/IARBS] [ ]

Vi eier fremdeles ikke slutten - den fanger vi opp ved å lytte på formidlingsgrupper.



(/api/fullfoersykmeldt : POST -> lagrer jo ned en ordinær registrering + aktiverer bruker i Arena -> denne håper vi å kvitte oss med) 

Fremtidsbildet:
[start på en periode utledes av ordinær registrering (våre data) .... slutt på periode utledes utmelding (våre data)] [ ]


