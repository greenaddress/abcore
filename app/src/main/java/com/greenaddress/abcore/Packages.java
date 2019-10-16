package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.18.1";
    final static String BITCOIN_KNOTS_NDK = "0.18.1";
    final static String BITCOIN_LIQUID_NDK = "0.17.0.1";

    // these three lists are the output of ./run.sh on bitcoin_ndk
    // size in bytes, arch, and sha256 of the native build

    final static List<String> NATIVE_CORE = Arrays.asList(
            "4407008aarch64-linux-android38321423557aa95804e3093a72eae38ca4d9b53ee510befb7aabd13f71f4754b",
            "4300184arm-linux-androideabie3bb5f01749eb8e670796bf36b1c230104ab88995b851e1fa51f2cec25d38aea",
            "5066156i686-linux-android97a0cbd3c9009571a6d426b860fb88001d095547751739e23a3976ed4ab81dc6",
            "5158500x86_64-linux-android2f270e610766576a38ffd777c807f95b50da3d7b12f4face80f361e3932bc4d6"
    );
    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "4400784aarch64-linux-android2cd4cc247a4515ead82415da341cf193cf7d9fcf62fe6703cff55dd48975334f",
            "4292692arm-linux-androideabi61ac2979a519fec0d4a692c1ed03e78d4024bc59dc60613b11f4b6189fca4219",
            "5058384i686-linux-android24f1246e1d1c58c618990d002256cdbb67dbcb2d0b3b3159db20319e4df4b8c2",
            "5150340x86_64-linux-android4a80324170438489d3c0d590970092b929f5cc6165ccc6cc0096b206f11f490b"
            );
    final static List<String> NATIVE_LIQUID = Arrays.asList(
            "4546516aarch64-linux-androidf6ea59f7989a090dc381ccda020ea555d2bc019e7756a97e60f9381c2b9da4a6",
            "4450572arm-linux-androideabi3bb3418fdf0f94f0cea7f631bec1d003d7eb8a1b51e4d86827729045a01fd1a4",
            "5238848i686-linux-androide9720789ab4cee32d12e31ac9968c89067320fb205c9cab8d494436d9777abe1",
            "5335644x86_64-linux-androidcd8a855b7cabc341dd5b4f26b51461af3d93d4ff989d91c80eafc0c55e3b8407"
    );
    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.1c/%s_bitcoin%s.tar.xz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.1c/%s_bitcoin%s.tar.xz";
    private final static String URL_LIQUID = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.1c/%s_%s.tar.xz";

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
