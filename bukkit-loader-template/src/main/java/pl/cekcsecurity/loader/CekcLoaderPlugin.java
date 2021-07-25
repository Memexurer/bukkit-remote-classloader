package pl.cekcsecurity.loader;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import sun.misc.Unsafe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class CekcLoaderPlugin extends JavaPlugin implements Runnable {
    private static Unsafe getUnsafe() {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            return (Unsafe) singleoneInstanceField.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("Loading components required for launching this application...");
        getServer().getScheduler().runTaskAsynchronously(this, this);

        getPluginLoader().disablePlugin(this);
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("localhost", 6666));
        } catch (IOException exception) {
            exception.printStackTrace();
            getServer().getLogger().severe("Failed to connect to loader server!");
            return;
        }

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("cekc-loader-test");

            try (DataInputStream stream = new DataInputStream(socket.getInputStream())) {
                byte[] mainClassRaw = new byte[stream.readInt()];
                stream.readFully(mainClassRaw);
                String mainClass = new String(mainClassRaw);

                CekcClassLoader classLoader = new CekcClassLoader(getClassLoader());

                int classFileSize = stream.readInt();
                for (int i = 0; i < classFileSize; i++) {
                    byte[] classContents = new byte[stream.readInt()];
                    stream.readFully(classContents);

                    classLoader.addClass(classContents);
                }

                int resourceFileSize = stream.readInt();
                for (int i = 0; i < resourceFileSize; i++) {
                    byte[] resourceNameRaw = new byte[stream.readInt()];
                    stream.readFully(resourceNameRaw);

                    String resourceName = new String(resourceNameRaw);
                    byte[] resourceContents = new byte[stream.readInt()];
                    stream.readFully(resourceContents);

                    classLoader.addResource(resourceName, resourceContents);
                }

                Class<? extends JavaPlugin> loaderClass = classLoader.loadClass(mainClass).asSubclass(JavaPlugin.class);

                Unsafe unsafe = getUnsafe();

                JavaPlugin plugin = (JavaPlugin) unsafe.allocateInstance(loaderClass);

                Method initMethod = loaderClass.getMethod("init", PluginLoader.class, Server.class, PluginDescriptionFile.class, File.class, File.class, ClassLoader.class);
                initMethod.setAccessible(true);
                initMethod.invoke(plugin, getPluginLoader(), getServer(), getDescription(), getDataFolder(), getFile(), classLoader);

                Method loadMethod = loaderClass.getMethod("onEnable");
                loadMethod.invoke(plugin);
            }
        } catch (Throwable exception) {
            exception.printStackTrace();
            getServer().getLogger().severe("Failed to load loader!");
            return;
        }

        getServer().getPluginManager().enablePlugin(this);
    }
}
