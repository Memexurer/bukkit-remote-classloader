package pl.cekcsecurity.loader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
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
    public URL getResource(String name) {
        if(!resourceMap.containsKey(name))
            return null;

        try {
            return new URL(null, "gowno://jebalciepies", new Gowno(resourceMap.get(name)));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
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

    private static class Gowno extends URLStreamHandler {
        private final byte[] gownoJebane;

        private Gowno(byte[] gownoJebane) {
            this.gownoJebane = gownoJebane;
        }

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return new ByteUrlConnection(u, gownoJebane);
        }
    }

    private static class ByteUrlConnection extends URLConnection {
        private final byte[] bytes;
        public ByteUrlConnection(URL url, byte[] bytes) {
            super(url);
            this.bytes = bytes;
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(bytes);
        }
    }
}
