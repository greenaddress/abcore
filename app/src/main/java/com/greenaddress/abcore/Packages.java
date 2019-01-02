package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.17.1";
    final static String BITCOIN_KNOTS_NDK = "0.17.1";


    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.17.1/%s_bitcoin%s.tar.gz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.17.1/%s_bitcoin%s.tar.gz";


    final static List<String> NATIVE_CORE = Arrays.asList(
            "3836183arm-linux-androideabia73a2e62e33ae994ecda021b130c55672a4dd3f53977b8427a4e45d5097f56a4",
            "3910122aarch64-linux-androidaa45b42f08a4d7b71f12758f92155b319abfc8c04f55fba47bff1abbbfc0bdfa",
            "4260400x86_64-linux-androidfeb2b7614111e7591cb4cfaa82fe31f97953324ccac55e25e2a8a26a24b2be60",
            "4138985i686-linux-android2c6fda14756aa9fd08068bb85f5589f31e2c490f88e12c90b276256cb672c6df"
    );


    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "3967502arm-linux-androideabi9ae8fbbdfe43178474e9f876e4d22f199cec98a94995ad84ef37e3172b499b25",
            "4037180aarch64-linux-android96a76ad89f57b3a36e74f4de179ca9f0e9e1355558b423ea7229d666b5cb6c22",
            "4402426x86_64-linux-android98a48e390c7b527135747e75b4925232e812204c99b425ffd9140541631f87f2",
            "4278455i686-linux-android17354bd00d840f3cfd90df9031ba1249ac408a994a61c555fc7ddcc7329fdd1c"
    );

    static String getPackageUrl(final String distro, final String arch) {
        if (distro.equals("core")) {
            return String.format(URL, arch, "");
        }
        return String.format(URL_KNOTS, arch, "knots");
    }
}
