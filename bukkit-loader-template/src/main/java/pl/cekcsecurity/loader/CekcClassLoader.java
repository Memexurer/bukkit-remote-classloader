package pl.cekcsecurity.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CekcClassLoader extends ClassLoader {
    private final Map<String, byte[]> resourceMap = new HashMap<>();
    private final Map<String, byte[]> classMap = new HashMap<>();

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

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        byte[] classContents = classMap.get(name);
        if(classContents == null)
        {
            throw new ClassNotFoundException(name);
        }
        return defineClass(name, classMap.get(name), 0, classContents.length);
    }

    public void addClass(String name, byte[] contents) {
        classMap.put(name, contents);
    }
}
