package pl.cekcsecurity.server.gate.provider;

import pl.cekcsecurity.server.gate.LoaderContents;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LoaderContentLoader {
    private LoaderContentLoader() {
    }

    public static LoaderContents loadContents(File file) throws IOException {
        try (DataInputStream inputStream = new DataInputStream(
                new FileInputStream(file)
        )) {
            Map<String, byte[]> classResources = new HashMap<>();
            Map<String, byte[]> resources = new HashMap<>();

            String mainClassName = inputStream.readUTF();

            int classResourcesCount = inputStream.readInt();
            for(int i = 0; i < classResourcesCount; i++)
                classResources.put(inputStream.readUTF(), readNBytes(inputStream, inputStream.readInt()));

            int resourceCount = inputStream.readInt();
            for(int i = 0; i < resourceCount; i++)
                resources.put(inputStream.readUTF(), readNBytes(inputStream, inputStream.readInt()));
            return new LoaderContents(
                    classResources,
                    resources,
                    mainClassName
            );
        }


    }

    private static byte[] readNBytes(InputStream stream, int count) throws IOException {
        byte[] bits = new byte[count];
        stream.read(bits);
        return bits;
    }
}
