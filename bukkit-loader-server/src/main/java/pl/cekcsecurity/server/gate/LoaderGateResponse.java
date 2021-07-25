package pl.cekcsecurity.server.gate;

public class LoaderGateResponse {
    private final boolean allowed;
    private final LoaderContents contents;

    public LoaderGateResponse(boolean allowed, LoaderContents contents) {
        this.allowed = allowed;
        this.contents = contents;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public LoaderContents getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "LoaderGateResponse{" +
                "allowed=" + allowed +
                ", contents=" + contents +
                '}';
    }
}
