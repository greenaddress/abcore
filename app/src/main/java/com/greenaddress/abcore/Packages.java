package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.17.1";
    final static String BITCOIN_KNOTS_NDK = "0.17.1";


    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.17.1b/%s_bitcoin%s.tar.gz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.17.1b/%s_bitcoin%s.tar.gz";


    final static List<String> NATIVE_CORE = Arrays.asList(
            "3816351arm-linux-androideabif207bd1eaf76c665a3b1a892adb2f86420febb782f1d0066a2474637cef89b6e",
            "3895937aarch64-linux-android683201131d9edd7ab3ca6bb20e17dae9f3f5b5e54d49574f783682e5d1dd384c",
            "4293686x86_64-linux-android40c876d4b92cd8c9458e2273303ba4669713cf7b0187389f6a1b4c151f40f655",
            "4142626i686-linux-android59b83c996e429f27ce1ac1f9e291d0d84f16ccff909c82ab6b1615f2d6c0205a"
    );


    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "3946592arm-linux-androideabibc87bef758a455b335955c21205559b78d58b85b6cc9e6d5ab25d5d13ea19128",
            "4024125aarch64-linux-androidd3135748e5fc1f47b73e2117158e43ffd840ccf49c8ac2e07f805883dfdb8d4c",
            "4437929x86_64-linux-android17e162b33363da439739eced99735e24f00e4735e9e32ee461e440c542bacc4f",
            "4287838i686-linux-android81ec3f60dfb5a2b9b5092bb5a775fb1fa87dba02190aea8b7537ddec134599c3"
    );

    static String getPackageUrl(final String distro, final String arch) {
        if (distro.equals("core")) {
            return String.format(URL, arch, "");
        }
        return String.format(URL_KNOTS, arch, "knots");
    }
}
