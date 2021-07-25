import org.junit.jupiter.api.Test;
import pl.cekcsecurity.loader.CekcClassLoader;
import sun.misc.Unsafe;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CekcLoaderTest {
    private static final byte[] PRODUCT_NAME = "cekc-loader-test".getBytes(StandardCharsets.UTF_8);
    private static final InetSocketAddress SERVER_ADDRESS = new InetSocketAddress("localhost", 6666);

    @Test
    public void run() {
        Socket socket = new Socket();
        try {
            socket.connect(SERVER_ADDRESS);
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.write(PRODUCT_NAME);

            try (DataInputStream stream = new DataInputStream(socket.getInputStream())) {
                byte[] mainClassRaw = new byte[stream.readInt()];
                stream.readFully(mainClassRaw);
                String mainClass = new String(mainClassRaw);

                System.out.println(mainClass);

                int classFileSize = stream.readInt();
                for (int i = 0; i < classFileSize; i++) {
                    byte[] classContents = new byte[stream.readInt()];
                    stream.readFully(classContents);

                    System.out.println(new String(classContents));
                }

                int resourceFileSize = stream.readInt();
                for (int i = 0; i < resourceFileSize; i++) {
                    byte[] resourceNameRaw = new byte[stream.readInt()];
                    stream.readFully(resourceNameRaw);

                    byte[] resourceContents = new byte[stream.readInt()];
                    stream.readFully(resourceContents);

                    System.out.println(new String(resourceNameRaw) + " - " + resourceFileSize);
                }
            }
        } catch (Throwable exception) {
            exception.printStackTrace();
        }
    }
}
