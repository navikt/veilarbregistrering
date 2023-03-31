# Reparering av arbeidssøkerperioder etter feil i data fra Arena

## Intro til feilen

## Løsning

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

## Tall på personer som har fått fikset sin arbeidssøkerperiode

Vi har avsluttet arbeidssøkerperiode for totalt 4244 personer. De resterende 919 personene har enten fått avsluttet sin
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