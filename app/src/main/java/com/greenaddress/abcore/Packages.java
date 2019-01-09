package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.18.0";
    final static String BITCOIN_KNOTS_NDK = "0.17.1";
    final static String BITCOIN_LIQUID_NDK = "0.17.0";
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
    final static List<String> NATIVE_LIQUID = Arrays.asList(
            "3959317arm-linux-androideabi989daaba123e416e8ef3cfaefc366342fc5100f751a8c08cafacdf7d070526ad",
            "4050750aarch64-linux-android5fa6406a679adc1066413138c3e1a98f27df08b9c700af2e9afa224a728e991b",
            "4452523x86_64-linux-android9fcc498d0992f1d334643b279b98293c39c6e9e41f715f87acb0a88719b0e102",
            "4304758i686-linux-android86d3f0a6075569f9fd7be0c3c4ddc62d7ee4ddd0ad717cf97f69d2bf0eaeb7f2"
    );
    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.0/%s_bitcoin%s.tar.gz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.17.1b/%s_bitcoin%s.tar.gz";
    private final static String URL_LIQUID = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.17.0_liquid/%s_%s.tar.gz";

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
