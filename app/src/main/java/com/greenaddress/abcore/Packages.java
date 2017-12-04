package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String CORE_V = "0.15.1";
    private final static String KNOTS_V = ".knots20171111";

    private final static String URL = "https://github.com/greenaddress/abcore/releases/download/v0.61alphaPoC/%s_v%s%s.tar.gz";

    final static List<String> NATIVE_CORE = Arrays.asList(
            "3865955arm-linux-androideabi99f382412eea95da7592e45bea9b2d06249541a6dc9c7acf54fbc63898b7e702",
            "3671269aarch64-linux-androidb4f4689196179e6d2637490bce0506aeffcb8a140f3d91dbdfb326a9e875ae8f",
            "4067477x86_64-linux-androidf9a946d6be46d75f87ecadc54b5a2987cad8464713909ff6c06b499652a946c9",
            "4065739i686-linux-android03bb9a40bc155c476be8e498990cc3384066d44b8b183fac17d4f615a8cc7961"
    );


    final static List<String> NATIVE_KNOTS =
            Arrays.asList(
                    "3979715arm-linux-androideabif6d14368ea603c32424507a0ad53f1cce1ca949b90aea3c0b5bb83e2b680c23e",
                    "3777051aarch64-linux-android0b09eced668570531923bd3a03922c51d63e6a5b180288e26c75253e15f1ddc7",
                    "4186541x86_64-linux-android614d9a105aedd48ebbbb6d440cbdb97e198e36414ce40660013b38477f6b6083",
                    "4186390i686-linux-androidfbe8d674c209e1fb3a8acfb9fa5040cf409695b8c2f944f1ad15be72611e978d"
            );

    static String getPackageUrl(final String distro, final String arch) {
        return String.format(URL, arch, CORE_V, distro.equals("core")? "" : KNOTS_V);
    }
}
