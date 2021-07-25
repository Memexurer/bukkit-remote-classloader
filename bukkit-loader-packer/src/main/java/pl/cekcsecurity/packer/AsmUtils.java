package pl.cekcsecurity.packer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class AsmUtils {
    private AsmUtils() {
    }

    public static void applyTransformer(ZipFile input, ZipOutputStream output, Transformer transformer) throws IOException {
        Enumeration<? extends ZipEntry> enumeration = input.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            byte[] entryContents = input.getInputStream(entry).readAllBytes();

            output.putNextEntry(new ZipEntry(entry.getName()));
            if (entry.getName().endsWith(".class")) {
                ClassReader reader = new ClassReader(entryContents);
                ClassNode node = new ClassNode();
                reader.accept(node, ClassReader.EXPAND_FRAMES);

                transformer.transform(node);

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                node.accept(writer);
                output.write(writer.toByteArray());
            } else {
                output.write(entryContents);
            }
            output.closeEntry();
        }
    }

    /**
     *
     * @author samczsun xdd
     */
    public static AbstractInsnNode createIntNode(int number) {
        if (number >= -1 && number <= 5)
            return new InsnNode(number + 3);
        else if (number >= -128 && number <= 127)
            return new IntInsnNode(Opcodes.BIPUSH, number);
        else if (number >= -32768 && number <= 32767)
            return new IntInsnNode(Opcodes.SIPUSH, number);
        else
            return new LdcInsnNode(number);
    }
}
