package pl.cekcsecurity.server.gate;

import java.util.concurrent.CompletableFuture;

public interface LoaderGatekeeper {
    CompletableFuture<LoaderGateResponse> request(LoaderGateRequest request);
}
