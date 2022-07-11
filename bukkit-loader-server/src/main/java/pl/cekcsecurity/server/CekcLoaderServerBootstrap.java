package pl.cekcsecurity.server;

import pl.cekcsecurity.server.gate.bot.BotGatekeeper;
import pl.cekcsecurity.server.gate.provider.DefaultResourceProvider;
import pl.cekcsecurity.server.server.LoaderServer;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.InetSocketAddress;

public final class CekcLoaderServerBootstrap {
    private CekcLoaderServerBootstrap() {
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: (bind port)");
            return;
        }

        BotGatekeeper botGatekeeper = null;
        try {
            botGatekeeper = new BotGatekeeper(
                    new DefaultResourceProvider(
                            new File("resources")
                    )
            );
        } catch (LoginException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        LoaderServer server = new LoaderServer(botGatekeeper);
        try {
            server.start(new InetSocketAddress(Integer.parseInt(args[0])));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
