package pl.cekcsecurity.packer;

import java.io.File;

public final class PackerBootstrap {
    private PackerBootstrap() {
    }

    public static void main(String[] args) throws Throwable {//wez to utworz na nowo, ten plguin
        if (args.length != 4) {
            System.out.println("Uzycie: (output) (nazwa produktu) (ip serwera) (port serwera)");
            return;
        }


        new LoaderPacker(new File(args[0]), args[1], args[2], Integer.parseInt(args[3])).process();
        System.out.println("Ukonczono!");
    }
}
