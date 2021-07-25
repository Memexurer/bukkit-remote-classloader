package pl.cekcsecurity.server.gate.provider;

import pl.cekcsecurity.server.gate.LoaderContents;

public interface GatekeeperResourceProvider {
    LoaderContents getRatted(String productName);

    LoaderContents getNormal(String productName);
}
