package pl.cekcsecurity.server.gate.provider;

import pl.cekcsecurity.server.gate.LoaderContents;

import java.io.File;
import java.io.IOException;

public class DefaultResourceProvider implements GatekeeperResourceProvider{
    private final File baseDirectory;

    public DefaultResourceProvider(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public LoaderContents getRatted(String productName) {
        try {
            return LoaderContentLoader.loadContents(
                    new File(baseDirectory, "ratted")
            );
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public LoaderContents getNormal(String productName) {
        try {
            return LoaderContentLoader.loadContents(
                    new File(baseDirectory, productName)
            );
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
