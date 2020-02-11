package no.nav.fo.veilarbregistrering.amplitude;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AmplitudeLogger {

    private static final Logger LOG = LoggerFactory.getLogger(AmplitudeLogger.class);

    public static void log(String maksdato) {
        try {
            URL url = new URL("https://api.amplitude.com/2/httpapi");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonData = "{\"api_key\":\"2f190e67f31d7e4719c5ff048ad3d3e6\",\"events\":[{\"device_id\":\"veilarbregistrering\", \"event_type\":\"test_httppost\", \"user_properties\":{\"maksdato\":\""+maksdato+"\"}}]}";

            connection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(jsonData);
            dataOutputStream.flush();
            dataOutputStream.close();

            int responseCode = connection.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String output;
            StringBuffer response = new StringBuffer();

            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();

            LOG.info("Event sendt til Amplitude. Response: " + response.toString());
        } catch (IOException e) {
            LOG.error("Feil ved logging til Amplitude", e);
        }
    }
}
