package pl.cekcsecurity.server.gate;

public class LoaderGateRequest {
    private final String name;
    private final String requestAddress;

    public LoaderGateRequest(String name, String requestAddress) {
        this.name = name;
        this.requestAddress = requestAddress;
    }

    public String getName() {
        return name;
    }

    public String getRequestAddress() {
        return requestAddress;
    }
}
