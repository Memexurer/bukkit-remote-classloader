package pl.cekcsecurity.server.gate;

import java.util.*;

public class LoaderContents {
    private final Map<String, byte[]> classes;
    private final Map<String, byte[]> resources;
    private final String mainClassName;

    public LoaderContents(Map<String, byte[]> classes, Map<String, byte[]> resources, String mainClassName) {
        this.classes = classes;
        this.resources = resources;
        this.mainClassName = mainClassName;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public Map<String, byte[]> getClasses() {
        return classes;
    }

    public Map<String, byte[]> getResources() {
        return resources;
    }
}
