package no.nav.fo.veilarbregistrering;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileToJson {

    public static String toJson(String json_file, Class c) {
        try {
            //TODO: SpringContextTest bør erstattes med noe annet - dirty måte å finne path på
            byte[] bytes = Files.readAllBytes(Paths.get(SpringContextTest.class.getResource(json_file).toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
