#!/usr/bin/env bash

if test -f /var/run/secrets/nais.io/oracle_creds/username;
then
   export SRVVEILARBREGISTRERING_USERNAME=$(cat /var/run/secrets/nais.io/oracle_creds/username)
   echo "Setting SRVVEILARBREGISTRERING_USERNAME to $SRVVEILARBREGISTRERING_USERNAME"
    
fi

if test -f /var/run/secrets/nais.io/oracle_creds/password;
then
    export SRVVEILARBREGISTRERING_PASSWORD=$(cat /var/run/secrets/nais.io/oracle_creds/password)
    echo "Setting SRVVEILARBREGISTRERING_PASSWORD"
fi
echo "INIT SCRIPT DONE"
