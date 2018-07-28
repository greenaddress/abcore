package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.16.2";

    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.16.2/%s_bitcoin%s.tar.gz";

    final static List<String> NATIVE_CORE = Arrays.asList(
            "3820562arm-linux-androideabiad9ddaa9d9f18e8bb2ce2e4e08a43778d54669535faecc3d791f6a2065a2b4ee",
            "3819156aarch64-linux-android42a8e81cfd4784c1366f584989a7c3fb76b01adbf12338b0cadee7564de1f5aa",
            "4175958x86_64-linux-android9cdeffcec7e2c3053c4d898e3e7eb697359793293b0942742be1bb7e18be1d34",
            "4088067i686-linux-androida105e0c02ca26b9c64c26de72fa0fdd95238a064c6ff0a435619b8443d3db068"
    );


    final static List<String> NATIVE_KNOTS =
            Arrays.asList(
                    "4007064arm-linux-androideabi6a7079c155c73ce13d5be613b4f0c2a347d6e4522901a429d6f6462d84aba84c",
                    "4003310aarch64-linux-android71eda00b437eca28097b557e6d1df0527e492b547b9d2e5b8942396e1f7ac7a7",
                    "4416797x86_64-linux-androidc0bbf5930f43619d96b5041ec8adc40d71c61b2d21deec06891819a8d099819f",
                    "4322555i686-linux-androidf62702523815072f051c202be7b8d988e1c4f57d85059631d3d09db4c15257d7"
            );

    static String getPackageUrl(final String distro, final String arch) {
        if (distro.equals("core")) {
            return String.format(URL, arch, "");
        }
        return String.format(URL, arch, "knots");
    }
}
