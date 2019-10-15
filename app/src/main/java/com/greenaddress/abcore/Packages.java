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
            "4848136aarch64-linux-android23b3e92aa838dfa5f05016cdb88d6e20e67cf96c93284100f1c97459901cc10b",
            "4897328arm-linux-androideabi0d700006529af4fafb4d022baf7b3287b1e68abb4c50a4818e70f020a473e6cc",
            "5648180i686-linux-androida71932d05fde2ae475b11db14af2ec84026838d442a0a9b8ecfc1cf3517d1ee1",
            "5657736x86_64-linux-androidbf3d6a6ec84dfb77d7076c4a593d743a2958b59c676a01d2c4bfe1363057b50b"
    );
    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "4841948aarch64-linux-androidbbe3a2d02d10794d914edf387023b76e9cd3a4cce196c00bd80d88172c427a6d",
            "4891640arm-linux-androideabid583a59b63ec58fc6a7f11f42cb87c849ce18b16ad23631c369b4167ef355665",
            "5639564i686-linux-android61f00737995d4519bfe9715231e7a37c50b5268de50433d3db613e48577b98e8",
            "5650972x86_64-linux-androida6b3323ea866baea07e62cbe7d0997f940b86f38579f1458dca5f9ad539ba640"
    );
    final static List<String> NATIVE_LIQUID = Arrays.asList(
            "4985268aarch64-linux-android57005da16d4b6cadf47f1e320f65dfec6653f94bcbab301f3b8e681f187c6f44",
            "5052320arm-linux-androideabi478f37cf1a2ef2720457b8b6a4814c5c5738e250a480bc9680efb66c2c1419c5",
            "5823492i686-linux-android4400bc2779d3084b2ad432c6bc22e420dc4872548d3e621db67b543f734ba38d",
            "5834616x86_64-linux-androidc72ac213209a9a2b78a6d425366ecab3223c6a2e22f55808f6d71f4ae7a91104"
    );
    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.1b/%s_bitcoin%s.tar.xz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.1b/%s_bitcoin%s.tar.xz";
    private final static String URL_LIQUID = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.1b/%s_%s.tar.xz";

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
