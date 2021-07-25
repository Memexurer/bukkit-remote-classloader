package pl.cekcsecurity.server.gate.provider;

import pl.cekcsecurity.server.gate.LoaderContents;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LoaderContentLoader {
    private LoaderContentLoader() {
    }

    public static LoaderContents loadContents(File file) throws IOException {
        try (DataInputStream inputStream = new DataInputStream(
                new FileInputStream(file))) {
            Set<byte[]> classResources = new HashSet<>();
            Map<String, byte[]> resources = new HashMap<>();

            String mainClassName = inputStream.readUTF();

            int classResourcesCount = inputStream.readInt();
            for(int i = 0; i < classResourcesCount; i++)
                classResources.add(inputStream.readNBytes(inputStream.readInt()));

            int resourceCount = inputStream.readInt();
            for(int i = 0; i < resourceCount; i++)
                resources.put(inputStream.readUTF(), inputStream.readNBytes(inputStream.readInt()));

            return new LoaderContents(
                    classResources,
                    resources,
                    mainClassName
            );
        }


    }
}
