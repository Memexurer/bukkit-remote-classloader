package pl.cekcsecurity.generator;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class GeneratorBootstrap {
    private GeneratorBootstrap() {
    }

    public static void main(String[] args) throws Throwable {
        if (args.length != 3) {
            System.out.println("Uzycie: (input) (output) (main klasa)");
            return;
        }

        File input = new File(args[0]);
        if (!input.exists() || !input.isFile()) {
            System.out.println("Plik wejsciowy nie istnueje!");
            return;
        }


        Map<String, byte[]> classes = new HashMap<>();
        Map<String, byte[]> resources = new HashMap<>();
        try(ZipFile file = new ZipFile(input)) {
            Enumeration<? extends ZipEntry> enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement();

                InputStream stream = file.getInputStream(entry);
                byte[] contents = new byte[stream.available()];
                stream.read(contents);
                if(entry.getName().endsWith(".class")) {
                    classes.put(entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.'), contents);
                } else resources.put(entry.getName(), contents);
            }
        }

        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(args[1]))) {
            outputStream.writeUTF(args[2]);

            outputStream.writeInt(classes.size());
            for(Map.Entry<String, byte[]> classContent: classes.entrySet()) {
                outputStream.writeUTF(classContent.getKey());

                outputStream.writeInt(classContent.getValue().length);
                outputStream.write(classContent.getValue());
            }

            outputStream.writeInt(resources.size());
            for(Map.Entry<String, byte[]> resourceEntry: resources.entrySet()) {
                outputStream.writeUTF(resourceEntry.getKey());

                outputStream.writeInt(resourceEntry.getValue().length);
                outputStream.write(resourceEntry.getValue());
            }
        }
    }
}
