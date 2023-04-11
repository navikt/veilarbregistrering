# Reparering av arbeidssøkerperioder etter feil i data fra Arena

## Intro til feilen

Da vi bygde opp vårt register av arbeidssøkerperioder, måtte vi bruke både våre egne registreringer, og formidlingsgrupper
fra Arena. Formidlingsgruppene ble brukt for å identifisere avslutning på periode, samt start på periode for de som har
vært arbeidssøkere før vår egen registrering kom på lufta (september 2018). Da vi hadde fylt opp registeret med historiske
data, oppdaget vi at en del personer hadde startet en arbeidssøkerperiode helt tilbake i 2001 og aldri avsluttet den.
Det vil si at de har stått oppført som arbeidssøker kontinuerlig i 22 år i følge formidlingsgruppene vi har fått fra Arena. 
Dette stusset vi på, og vi stilte et spørsmål til Arena med noen stikkprøver. Se relevant Slack-tråd
[her](https://nav-it.slack.com/archives/CC9GYTA2C/p1677755329600699). Da viste det seg at disse personene ikke er
arbeidssøkere i Arena. Denne feilen skyldes at vi ikke har fått alle formidlingsgrupper fra Arena. 

### Mer detaljert informasjon for spesielt interesserte
Vi får formidlingsgrupper fra Arena på et kafka-topic. Dette topicet er basert på tabellen `HIST_PERSON`, som er en tabell
med historikk over alle formidlingsgrupper en person har hatt i Arena. Denne tabellen har ikke blitt oppdatert med alle 
endringer i formidlingsgruppe, slik at nåværende formidlingsgruppe ikke gjenspeiles i historikken og dermed heller ikke har blitt
oversendt til oss. Det er noe uklart for meg hvorfor dette har skjedd, men jeg legger ved et sitat fra slack-diskusjonen som er lenket
over: 
"historikk-tabellen som blant annet skal inneholde alle formidlingsgrupper en person har og har hatt, viser seg å ikke være
komplett for noen personer. Det er heller ikke innslag om at personene har blitt inaktivert (skal ligge i personloggen),
så jeg mistenker at det har vært en glipp i funksjonalitet en eller annen gang tilbake i tid, eller noe script-oppdateringer
som har omgått noe av funksjonaliteten når en person endrer formidlingsgruppe."

## Uttrekk for å finne hvilke personer som er truffet av feilen

For å identifisere personene som er truffet av feilen, hentet vi alle person_id-er i formidlingsgruppe som vi kun
har mottatt én formidlingsgruppe for, og den formidlingsgruppen er ARBS. Resultatene ligger ikke tilgjengelig fordi de
inneholder fødselsnummer, men kan reproduseres med følgende SQL:

```sql
SELECT
    formidlingsgruppe.person_id,
    formidlingsgruppe.foedselsnummer,
    formidlingsgruppe.formidlingsgruppe,
    formidlingsgruppe.formidlingsgruppe_endret
FROM (
         SELECT person_id, COUNT(*) as antall_rader
         FROM formidlingsgruppe
         GROUP BY person_id
         HAVING COUNT(*) = 1
     ) p
         INNER JOIN formidlingsgruppe ON p.person_id = formidlingsgruppe.person_id
WHERE formidlingsgruppe.formidlingsgruppe = 'ARBS' AND person_id_status = 'AKTIV'; 
```

Dette gir, per 31.03.2023, 14281 treff. Det er sannsynlig at en stor andel av disse personene har startet en
arbeidssøkerperiode nylig, og at det dermed er riktig at vi kun har mottatt én formidlingsgruppe fra Arena, som er ARBS. 
Likevel vurderte vi at det beste var å sjekke alle 14281 selv om en del er "falske positive", fordi vi ikke vet hvor
lenge feilen varte i Arena og ikke hadde eksakte tall fra Arena på hvilke personer som er truffet. 

## Løsning

Vi trenger å finne korrekt formidlingsgruppe i Arena for disse personene, slik at vi kan avslutte arbeidssøkerperiode for
de som ikke lenger er arbeidssøkere i Arena. Arena har tilbudt seg å rette opp i feilen på sin side og lage et nytt initielt
uttrekk for formidlingsgruppe på Kafka. Dette vil imidlertid ta veldig lang tid, og vi må da bygge opp alle arbeidssøkerperioder
på nytt. Vi har nå rundt 5 millioner arbeidssøkerperioder i vårt register, og denne feilen treffer sannsynligvis under 10 000
av dem. Derfor vurderte vi at det er bedre å finne riktig status på de som er truffet av feilen "manuelt", fordi dette tar
mye kortere tid. 

Vi har tilgang til Arena sin Q0-database som får datalast fra produksjon hver uke. Vi brukte denne for å finne riktig
formidlingsgruppe, skrev denne dataen til en tabell i BigQuery, og eksporterte den som en csv-fil. Deretter leste vi inn
denne filen og avsluttet arbeidssøkerperioder via en batch-jobb i veilarbregistrering. 

Vi ble tipset om at den enkleste måten å finne riktig status for person på er å sjekke Arena sin `PERSON`-tabell. Her finner
man gjeldende formidlingsgruppe for en person, samt datoen for når personen sist ble inaktivert (dersom den har vært eller er det). 
Vi slo opp alle 14281 personene i Person-tabellen, og hentet ut alle som har ISERV-status i dag med tilhørende dato for 
inaktivering for disse. Dette var totalt 5153 personer. Vi fant ingen personer med IARBS-status.

Av de 14281 personene, fant vi også 313 personer som har ISERV-status i Person-tabellen, men ingen dato for når de sist ble
inaktivert. Dette viser seg å være en annen feil i Arena. For disse resterende personene, leita vi i tabellen `LOGGLINJE`
for å finne ut når personene ble inaktivert. Logglinje er en logg over alle hendelser på en person, og det er 11 ulike 
hendelsestyper som omhandler inaktivering: MANIKAK, MSKINAK, MKINAKT, BESINAK, FRINAKT, INAKAEV, INAKINF, INAKT, INAKTSF, SBLINAK, SPERINA
I vår datafortelling om inaktivering bruker vi kun de to første for å identifisere inaktivering (maskinell- og manuell inaktivering),
så vi valgte å gjøre det samme her i frykt for at de andre er duplikater av disse. (Har for øvrig spurt Arena om en
forklaring på hvilke av disse statusene vi bør bruke uten å få svar). I logglinje-tabellen fant vi spor av 33 inaktiveringer
for 10 personer, men alle disse personene hadde vi i mellomtiden mottatt formidlingsgruppe for og allerede avsluttet
arbeidssøkerperioden. 

## Tall på personer som har fått fikset sin arbeidssøkerperiode

Vi har avsluttet arbeidssøkerperiode for totalt 4244 personer. De resterende 909 personene har enten fått avsluttet sin
arbeidssøkerperiode på "vanlig" måte i tiden mellom det første uttrekket og repareringen, eller så har vi ikke klart å 
finne arbeidssøkerperiode på noen av personens fødselsnumre. 

Antall fordelt på år:

| Årstall fra_og_med | Antall reparert |
|--------------------|-----------------|
| 2001               | 374             |
| 2002               | 1332            |
| 2003               | 1326            |
| 2004               | 973             |
| 2005               | 197             |
| 2006               | 38              |
| 2007               | 4               |

| Årstall til_og_med | Antall reparert |
|--------------------|-----------------|
| 2002               | 601             |
| 2003               | 1413            |
| 2004               | 1425            |
| 2005               | 689             |
| 2006               | 91              |
| 2007               | 23              |
| 2008               | 2               |

Disse resultatene kan reproduseres med følgende spørring:
```psql
SELECT date_trunc('year', til_og_med) as år, count(*) as antall_rader
FROM arbeidssokerperiode
where endret between '2023-03-23' and '2023-03-24' and til_og_med < '2023-03-23'::timestamp
GROUP BY år;
```

## Tall på personene vi ikke har fikset

Det gjenstår å sjekke og evt. fikse arbeidssøkerperioder for 9128 personer. 8815 av disse står oppført som arbeidssøker 
i dag, og de resterende 313 har vi ikke klart å finne inaktiveringsdato for selv om de har ISERV-status. Sistnevnte bør 
sendes til Arena slik at de kan finne riktig data, mens personene med ARBS bør vi ta en vurdering på om vi skal forsøke 
å fikse. Under er en fordeling på hvilket år disse personene ble arbeidssøker:

| Årstall til_og_med | Antall |
|--------------------|--------|
| 2001               | 8      |
| 2002               | 3      |
| 2003               | 4      |
| 2004               | 7      |
| 2005               | 12     |
| 2006               | 28     |
| 2007               | 25     |
| 2008               | 65     |
| 2009               | 129    |
| 2010               | 118    |
| 2011               | 22     |
| 2012               | 33     |
| 2013               | 32     |
| 2014               | 22     |
| 2015               | 30     |
| 2016               | 47     |
| 2017               | 73     |
| 2018               | 114    |
| 2019               | 919    |
| 2020               | 1364   |
| 2021               | 1893   |
| 2022               | 3452   |
| 2023               | 232    |

