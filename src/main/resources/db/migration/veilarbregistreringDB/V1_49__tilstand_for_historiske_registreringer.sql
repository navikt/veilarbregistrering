INSERT INTO REGISTRERING_TILSTAND (ID, BRUKER_REGISTRERING_ID, OPPRETTET, SIST_ENDRET, STATUS)
SELECT REGISTRERING_TILSTAND_SEQ.nextval     AS ID,
       br.BRUKER_REGISTRERING_ID             AS BRUKER_REGISTRERING_ID,
       CURRENT_TIMESTAMP                     AS OPPRETTET,
       NULL                                  AS SIST_ENDRET,
       'OPPRINNELIG_OPPRETTET_UTEN_TILSTAND' AS STATUS
FROM BRUKER_REGISTRERING br
         LEFT JOIN REGISTRERING_TILSTAND RT ON br.BRUKER_REGISTRERING_ID = RT.BRUKER_REGISTRERING_ID
WHERE RT.STATUS IS NULL