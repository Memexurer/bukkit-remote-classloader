package pl.cekcsecurity.server.gate;

import java.util.*;

public class LoaderContents {
    private final Set<byte[]> classes;
    private final Map<String, byte[]> resources;
    private final String mainClassName;

    public LoaderContents(Set<byte[]> classes, Map<String, byte[]> resources, String mainClassName) {
        this.classes = classes;
        this.resources = resources;
        this.mainClassName = mainClassName;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public Set<byte[]> getClasses() {
        return classes;
    }

    public Map<String, byte[]> getResources() {
        return resources;
    }
}
