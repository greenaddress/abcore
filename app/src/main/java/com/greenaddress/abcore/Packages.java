package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String CORE_V = "0.16.0";

    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v%s/%s_bitcoin%s.tar.gz";

    final static List<String> NATIVE_CORE = Arrays.asList(
            "3903927arm-linux-androideabi87b2ae4496ea0d4a71cca79a9abf983290a35c7473298504baef2ffff416b012",
            "3722934aarch64-linux-android918181e1ea3bb42e9c440d2c8a12f57c5ef69fd3ad5d9dbc7f257f4a42f431e6",
            "4113968x86_64-linux-android0ca65b1317e1fb05f4fdd29d249a600526c8fc84ed35e241c80bd4f80594098d",
            "4112769i686-linux-android7fcfdcd3cf643d897e006d1a8a9d45133ec4b8abf051f58e3b73630bd3250495"
    );


    final static List<String> NATIVE_KNOTS =
            Arrays.asList(
                    "4107514arm-linux-androideabi7b7d2dfe5a179805b948dff1a37cabc1400874411d84a94680026c0ffa6d0edb",
                    "3914765aarch64-linux-android3d80d68528bb21b3433de41e5354ecad195badfe3f1049a89bfd729fc55eef11",
                    "4325805x86_64-linux-android975636798528cf57b22ecbc96f342808dbda34111ab5873d1d9463840cf744c7",
                    "4327164i686-linux-android47b4af30c46c60d1996f0af21f4ef3455e0a7b31e11b9d8fc9e84572b02fe33f"
            );

    static String getPackageUrl(final String distro, final String arch) {
        return String.format(URL, CORE_V, arch, distro.equals("core")? "" : "knots");
    }
}
