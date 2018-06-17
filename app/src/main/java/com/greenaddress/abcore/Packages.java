package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String CORE_V = "0.16.1";
    final static String KNOTS_V = "0.16.0";

    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v%s/%s_bitcoin%s.tar.gz";

    final static List<String> NATIVE_CORE = Arrays.asList(
            "3821902arm-linux-androideabiab44e38a07f84370a2181f54082969e01673ce475229919dc582c9b76e13befb",
            "3820722aarch64-linux-android39b25f1413b159a1bfe806ee32de85d141559b395708502f6ef4547149f047f2",
            "4181597x86_64-linux-android9eb99c35c64eb9e38876c38788b666d29fefd7548a808c73f3d0aa3ecda35988",
            "4090532i686-linux-android5e962b265b73de66c5c002a6b631517aefff4befd6fe87e61e795cdeee411b6d"
    );


    final static List<String> NATIVE_KNOTS =
            Arrays.asList(
                    "4107514arm-linux-androideabi7b7d2dfe5a179805b948dff1a37cabc1400874411d84a94680026c0ffa6d0edb",
                    "3914765aarch64-linux-android3d80d68528bb21b3433de41e5354ecad195badfe3f1049a89bfd729fc55eef11",
                    "4325805x86_64-linux-android975636798528cf57b22ecbc96f342808dbda34111ab5873d1d9463840cf744c7",
                    "4327164i686-linux-android47b4af30c46c60d1996f0af21f4ef3455e0a7b31e11b9d8fc9e84572b02fe33f"
            );

    static String getPackageUrl(final String distro, final String arch) {
        if (distro.equals("core")) {
            return String.format(URL, CORE_V, arch, "");
        }
        return String.format(URL, KNOTS_V, arch, "knots");
    }
}
