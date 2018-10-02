package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.17.0";
    final static String BITCOIN_KNOTS_NDK = "0.16.3";


    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.17.0/%s_bitcoin%s.tar.gz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.16.3/%s_bitcoin%s.tar.gz";


    final static List<String> NATIVE_CORE = Arrays.asList(
            "3825885arm-linux-androideabib4b3be79c859c83ca02ac12b70fceb3e800eb15580addd7b48bb1b896a32c302",
            "3903585aarch64-linux-android51c129708b2ae4a4bc223fff1b486d6a62cabbfaaf6b8b7a9ef3aa71ac8f1ab8",
            "4250511x86_64-linux-androide688b8592e660c52c39aba98ec141d1799f501bdd23af2f9e3fdb60541b3b466",
            "4127210i686-linux-android5ae74105f98e0e0737ef40950f0078d3b504c50b34348bfb93a76f7cc37379da"
    );


    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "4004146arm-linux-androideabi241d614774733ab68d98299e6eb9c313238ad27830e8364d03006f7543b00a34",
            "3998141aarch64-linux-androidb415944c32948f4ceab4447087e5031aed06acc57120f7a45f55300862258373",
            "4411787x86_64-linux-androidcf05c6d6be9d628a0bc6fc2c3bf9202e84f9afc1d6a0db3f995aa59261fffa5d",
            "4320595i686-linux-android14c0d2249d91b679f69bea52ffe1343284142c4dde02622aefee0c30bee86c9c"
    );

    static String getPackageUrl(final String distro, final String arch) {
        if (distro.equals("core")) {
            return String.format(URL, arch, "");
        }
        return String.format(URL_KNOTS, arch, "knots");
    }
}
