package pl.cekcsecurity.server.gate.provider;

import pl.cekcsecurity.server.gate.LoaderContents;

public interface GatekeeperResourceProvider {
    LoaderContents getRatted();

    LoaderContents getNormal(String productName);
}
