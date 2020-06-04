package no.nav.fo.veilarbregistrering;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileToJson {

    public static String toJson(String json_file) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(FileToJson.class.getResource(json_file).toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
