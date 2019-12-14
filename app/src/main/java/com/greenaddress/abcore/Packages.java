package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.19.0.1";
    final static String BITCOIN_KNOTS_NDK = "0.18.1";
    final static String BITCOIN_LIQUID_NDK = "0.18.1.3";

    // these three lists are the output of ./run.sh on bitcoin_ndk
    // size in bytes, arch, and sha256 of the native build

    final static List<String> NATIVE_CORE = Arrays.asList(
            "5527036aarch64-linux-android7f431841190c276b51318803f7e71e3012726fb7323d7abfebc926e466568575",
            "5226832arm-linux-androideabi81bd282a2c607f62f17a0fb640927ff2d78588b389285de981dda0a6ef2e56f8",
            "6176104i686-linux-android66553fc6a0b3077f70131eb0a246890901ed51e15642dd97be58eb7002490891",
            "6363016x86_64-linux-android3ce9d65b36e8e74ab29a22836149b64680cb749d9dbf7a89a5fb9f5c81de4e22"
    );
    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "5368752aarch64-linux-android91a2414d3b697502924d13dcdb0fd586994c25b4008547aa012e37bce3b92a21",
            "5091092arm-linux-androideabi17d6a46fe9893a962a2ea4c307be56126b865840dee676452bc931f1d18bdc1b",
            "6033768i686-linux-androidb61fbe07604931ea1ff541e6331535fd0d6f94332611abfba92ad3df9b516380",
            "6209312x86_64-linux-android3411ea5bc32d74529d45915a7e4d2376a37a075d56e34c439a1795f81e360b09"
            );
    final static List<String> NATIVE_LIQUID = Arrays.asList(
            "5790596aarch64-linux-androided0038610bfd710074a96bab736adf56d4fba9d76132cdd956eaeeb532ebdc35",
            "5547968arm-linux-androideabi13e7f4270c51d7fe75809dd966a407e58ae0095f707b878423060b3a917592b0",
            "6480784i686-linux-android35ebf1aca32caff99d7346ce93775d9fea8f2bd4cf57edcc78f0ff71b087c10c",
            "6656944x86_64-linux-android947f94562018a45cbea9efcc391489fd54f578343eeb8c7dd5b48529d2b93c15"
            );
    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.19.0.1/%s_bitcoin%s.tar.xz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.19.0.1/%s_bitcoin%s.tar.xz";
    private final static String URL_LIQUID = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.19.0.1/%s_%s.tar.xz";

    static String getPackageUrl(final String distro, final String arch) {
        if ("core".equals(distro)) {
            return String.format(URL, arch, "");
        } else if ("liquid".equals(distro)) {
            return String.format(URL_LIQUID, arch, "liquid");
        }
        return String.format(URL_KNOTS, arch, "knots");
    }

    static String getVersion(final String distro) {
        if ("core".equals(distro)) {
            return BITCOIN_NDK;
        } else if ("liquid".equals(distro)) {
            return BITCOIN_LIQUID_NDK;
        }
        return BITCOIN_KNOTS_NDK;
    }
}
