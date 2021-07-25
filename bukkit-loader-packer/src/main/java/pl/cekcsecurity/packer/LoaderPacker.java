package pl.cekcsecurity.packer;

import org.objectweb.asm.tree.*;

import java.io.*;
import java.nio.channels.Channels;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class LoaderPacker {
    private static final String DEFAULT_PRODUCT_NAME = "cekc-loader-test";

    private final File output;
    private final String productName;
    private final String address;
    private final int port;

    public LoaderPacker(File output, String productName, String address, int port) {
        this.output = output;
        this.productName = productName;
        this.address = address;
        this.port = port;
    }

    public void process() throws IOException {
        File templateFile = File.createTempFile("loader-packer-template", ".jar");
        try (FileOutputStream fos = new FileOutputStream(templateFile)) {
            fos.getChannel()
                    .transferFrom(Channels.newChannel(getTemplateResource()), 0, Long.MAX_VALUE);
        }

        try (ZipFile zipFile = new ZipFile(templateFile)) {
            try (ZipOutputStream outputStream = new ZipOutputStream(
                    new FileOutputStream(output))) {
                AsmUtils.applyTransformer(zipFile, outputStream, this::transform);
            }
        }
    }

    public void transform(ClassNode node) {
        for (MethodNode methodNode : node.methods)
            for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
                if (abstractInsnNode instanceof LdcInsnNode) {
                    LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNode;
                    if (ldcInsnNode.cst.equals(DEFAULT_PRODUCT_NAME))
                        ldcInsnNode.cst = productName;
                } else if (abstractInsnNode instanceof MethodInsnNode &&
                        ((MethodInsnNode) abstractInsnNode).owner.equals("java/net/InetSocketAddress")) {
                    methodNode.instructions.set(abstractInsnNode.getPrevious(), AsmUtils.createIntNode(port));
                    ((LdcInsnNode) abstractInsnNode.getPrevious().getPrevious()).cst = this.address;
                }
            }
    }

    private InputStream getTemplateResource() {
        return getClass().getClassLoader().getResourceAsStream("template.jar");
    }
}
