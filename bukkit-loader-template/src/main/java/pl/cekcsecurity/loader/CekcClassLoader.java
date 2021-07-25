package pl.cekcsecurity.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CekcClassLoader extends ClassLoader {
    private final Map<String, byte[]> resourceMap = new HashMap<>();

    protected CekcClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public void addResource(String name, byte[] contents) {
        resourceMap.put(name, contents);
    }

    @Override
    public InputStream getResourceAsStream(String s) {
        return new ByteArrayInputStream(resourceMap.get(s));
    }

    public void addClass(byte[] contents) {
        defineClass(null, contents, 0, contents.length);
    }
}
