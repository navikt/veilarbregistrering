package no.nav.fo.veilarbregistrering.bruker.pdl;

import java.util.List;

public class PdlError {
    private String message;
    private List<PdlErrorLocation> locations;
    private List<String> path;
    private List<PdlErrorExtension> extensions;

    public PdlError() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PdlErrorLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<PdlErrorLocation> locations) {
        this.locations = locations;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<PdlErrorExtension> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<PdlErrorExtension> extensions) {
        this.extensions = extensions;
    }
}
