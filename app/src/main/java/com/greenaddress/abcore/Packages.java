package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.18.1";
    final static String BITCOIN_KNOTS_NDK = "0.18.0";
    final static String BITCOIN_LIQUID_NDK = "0.17.0.1";

    final static List<String> NATIVE_CORE = Arrays.asList(
            "4457584aarch64-linux-android9209e31ebe51887a11a02a2e2e6a2795608923c539d825f66c3168f957daaa4d",
            "4345624arm-linux-androideabi29d70965ffb4b74f80e0a0ad7e4986cf3bb5579904fd0ff6686973143d7be3de",
            "5118928i686-linux-android4b252564cccc80137c67e61b70c8c81c60ce49632b6aaafd2bf32e08e59c5089",
            "5232628x86_64-linux-androidd7d5bc7945db35b1c6ab9adbf0a71349a685f10beaf191b0f90845203e1bca03"
    );
    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "4522504aarch64-linux-android70b5a3de50766639c680fd5c84d2a51323775fabfa2ee01673c86d626058fa5e",
            "4416424arm-linux-androideabi7e5015c68b0cbfaa7f39bd3a9ebd314ad10d3efbb544f7ffb35cb25b809e96ce",
            "5201088i686-linux-android9afaa412286ea95ce7d65510b31d909979661dac3381882517a64d184b21608f",
            "5310668x86_64-linux-android3a46a77caae8ecd34487b9dabd9d6a780a85ca90e959337c194ece051e28f407"
    );
    final static List<String> NATIVE_LIQUID = Arrays.asList(
            "4594488aarch64-linux-android9333417f27deb893bea4ff23d27c70e38498323252bc7121eb96a8b46681aa61",
            "4494116arm-linux-androideabie5019ce55625208c3b66e270e5cb12096fa30c0f8dd9f6eb915265cee71f603c",
            "5292712i686-linux-androidc1cf98d6994e83273b23dc83fa046d854463998ef63ddbe81a8c353b56d74bcf",
            "5409312x86_64-linux-androide257f8a4ed1d39f29f0e8529f133ea6ae1d6713644117e91e8dd20eb6a847e2c"
    );
    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.1/%s_bitcoin%s.tar.xz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.1/%s_bitcoin%s.tar.xz";
    private final static String URL_LIQUID = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.1/%s_%s.tar.xz";

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
