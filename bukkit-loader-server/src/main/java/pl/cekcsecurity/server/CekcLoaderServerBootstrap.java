package pl.cekcsecurity.server;

import pl.cekcsecurity.server.gate.bot.BotGatekeeper;
import pl.cekcsecurity.server.gate.provider.DefaultResourceProvider;
import pl.cekcsecurity.server.server.LoaderServer;

import java.io.File;
import java.net.InetSocketAddress;

public final class CekcLoaderServerBootstrap {
    private CekcLoaderServerBootstrap() {
    }

    public static void main(String[] args) throws Throwable {

        BotGatekeeper botGatekeeper = new BotGatekeeper(
                new DefaultResourceProvider(
                        new File("resources")
                )
        );

        LoaderServer server = new LoaderServer(botGatekeeper);
        server.start(new InetSocketAddress(6666));
    }
}
