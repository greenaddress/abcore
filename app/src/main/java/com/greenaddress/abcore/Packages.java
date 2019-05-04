package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.18.0";
    final static String BITCOIN_KNOTS_NDK = "0.18.0";
    final static String BITCOIN_LIQUID_NDK = "0.17.0";
    final static List<String> NATIVE_CORE = Arrays.asList(
            "3947023aarch64-linux-androidc581e24c60cd26b3d6d0066d11aa50e47a174567abc74a58c7541f497042d1b3",
            "3836030arm-linux-androideabi0ecb59d59f9e6104846920c346c14bcee50415fe356e8a1372167e882eafcdf1",
            "4166841i686-linux-androida54fca3d49d8cf61902be21264b68c8bbec087d2dd1f761419d00b353f66bd70",
            "4307941x86_64-linux-android9b6b7a201faf2c939e3df8a61c229507ec3de926933996de750ff9005935a6f0"
    );
    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "4064581aarch64-linux-android7215d6cfbb4be6cc7654cb9b5eb399e8ea485d22091ff6e595877744ffea4a03",
            "3956120arm-linux-androideabi0780b45d66c2168c0134da293d289e4ee603752d6dfd2b8d094735bb420151a5",
            "4299987i686-linux-android194a994311723a9a056c8823523dacfaf7ea0812beeecd35c59e69a928d703f4",
            "4437992x86_64-linux-android844d4e83ac0769de246e8afa1be6c63d752310f062539362f23ef616d31afb56"
    );
    final static List<String> NATIVE_LIQUID = Arrays.asList(
            "4057394aarch64-linux-androide244082382979ca36a10d9cce010129abdfb43727c003477749c439a72b42075",
            "3960900arm-linux-androideabi1989f67936a129e3526f91df9be9ab24b8cb363c0f90f99e6c376ad5c9f08d51",
            "4305316i686-linux-android6ec2a0f176d4341d97c94dfaf862df452210d79b61a011b9577e2a3a2d4c6ba1",
            "4452465x86_64-linux-androidb88f81e0ad0b80549bd1aead4c15a84a488d126cb0776c2bb19f0297b7d613ee"
    );
    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.0b/%s_bitcoin%s.tar.gz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.0b/%s_bitcoin%s.tar.gz";
    private final static String URL_LIQUID = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.0b/%s_%s.tar.gz";

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
