package pl.cekcsecurity.generator;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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


        Set<byte[]> classes = new HashSet<>();
        Map<String, byte[]> resources = new HashMap<>();
        try(ZipFile file = new ZipFile(input)) {
            Enumeration<? extends ZipEntry> enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement();
                byte[] contents = file.getInputStream(entry).readAllBytes();
                if(entry.getName().endsWith(".class")) {
                    classes.add(contents);
                } else resources.put(entry.getName(), contents);
            }
        }

        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(args[1]))) {
            outputStream.writeUTF(args[2]);

            outputStream.writeInt(classes.size());
            for(byte[] classContent: classes) {
                outputStream.writeInt(classContent.length);
                outputStream.write(classContent);
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
