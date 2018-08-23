package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.16.2";

    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.16.2/%s_bitcoin%s.tar.gz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.16.2.knots20180730/%s_bitcoin%s.tar.gz";


    final static List<String> NATIVE_CORE = Arrays.asList(
            "3820562arm-linux-androideabiad9ddaa9d9f18e8bb2ce2e4e08a43778d54669535faecc3d791f6a2065a2b4ee",
            "3819156aarch64-linux-android42a8e81cfd4784c1366f584989a7c3fb76b01adbf12338b0cadee7564de1f5aa",
            "4175958x86_64-linux-android9cdeffcec7e2c3053c4d898e3e7eb697359793293b0942742be1bb7e18be1d34",
            "4088067i686-linux-androida105e0c02ca26b9c64c26de72fa0fdd95238a064c6ff0a435619b8443d3db068"
    );


    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "4003537arm-linux-androideabiaa026c69d4f2b51efe5730dea8cbb9575ff7e6ca651a29cd7e2b970ac48a497b",
            "3998145aarch64-linux-android32faebb115926f731bfb0b5f2ee524d8876c90c2ed677e9e4844d73345770bc0",
            "4412806x86_64-linux-android6fc81027363625f88a3d5c1bf3338838c33c47f5e6ffe43a7f6aec3bc4b1f39a",
            "4320618i686-linux-android1a036a5b06ff9b88f7b7b60c973a8c0913d8194b3f8ed2fb0b6260430f657f87"
    );

    static String getPackageUrl(final String distro, final String arch) {
        if (distro.equals("core")) {
            return String.format(URL, arch, "");
        }
        return String.format(URL_KNOTS, arch, "knots");
    }
}
