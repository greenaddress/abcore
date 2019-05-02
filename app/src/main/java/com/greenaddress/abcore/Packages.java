package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.18.0";
    final static String BITCOIN_KNOTS_NDK = "0.17.1";


    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.0/%s_bitcoin%s.tar.gz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.17.1b/%s_bitcoin%s.tar.gz";


    final static List<String> NATIVE_CORE = Arrays.asList(
            "3835876arm-linux-androideabif2245c769f39a40ca21e23177b8952257630e4d589cfe87f98879ae7a60fb60c",
            "3944076aarch64-linux-android3a75d80f38bd2b78b051a1f763ac1eab3ac67a8970200a9f35486b2b0b1b1650",
            "4308004x86_64-linux-androidd18f7377fe73aa161eab615e1381c18434a843cd55827157514a0fc84f96e9d7",
            "4167528i686-linux-androida55ea34976f9e542c8998fc0d42d3ebeb5562e7404779a58fe4e50498bf4c032"
    );


    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "3946592arm-linux-androideabibc87bef758a455b335955c21205559b78d58b85b6cc9e6d5ab25d5d13ea19128",
            "4024125aarch64-linux-androidd3135748e5fc1f47b73e2117158e43ffd840ccf49c8ac2e07f805883dfdb8d4c",
            "4437929x86_64-linux-android17e162b33363da439739eced99735e24f00e4735e9e32ee461e440c542bacc4f",
            "4287838i686-linux-android81ec3f60dfb5a2b9b5092bb5a775fb1fa87dba02190aea8b7537ddec134599c3"
    );

    static String getPackageUrl(final String distro, final String arch) {
        if (distro.equals("core")) {
            return String.format(URL, arch, "");
        }
        return String.format(URL_KNOTS, arch, "knots");
    }
}
