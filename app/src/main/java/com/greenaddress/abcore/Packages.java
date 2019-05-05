package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.18.0";
    final static String BITCOIN_KNOTS_NDK = "0.18.0";
    final static String BITCOIN_LIQUID_NDK = "0.17.0";
    final static List<String> NATIVE_CORE = Arrays.asList(
            "4613524aarch64-linux-androidff97eb601200cae77ea89b1370f1606eddf7c53cd17ad0a9f7974539bfd270cc",
            "4509768arm-linux-androideabi5b80ae53728f4d55620a0b3c7e0b30c412e1b36f9ec24f5ff8f7e62a565d0fa9",
            "5247152i686-linux-androida44d181025addf73522fb80968e56dd5dade55a02287f192a80f360c6f75adb3",
            "5448412x86_64-linux-androida30ca997e201ff80f6852769a00637b7e5676a51b2ba9c7f666c2253edcfa278"
    );
    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "4686912aarch64-linux-androide97670dfb990e2a7e33226fc70501ef9ac7083e432c70ce331d1a4728177c6eb",
            "4589528arm-linux-androideabia5e965ef0994a7f59c9a56bc052df48bc7c34fde9576f3a34ba60f8eacb2dd20",
            "5335788i686-linux-android0cdc5b500c30cc1ba0bb2cbfbcdc2cbcd15cbb7294859bd3dbcfbb59f51fd5c9",
            "5537272x86_64-linux-android4ce5226c02fae19941a6a5cca8fc1cb0a9ae932b8f48d672e6991b19e0844fec"
    );
    final static List<String> NATIVE_LIQUID = Arrays.asList(
            "4692064aarch64-linux-androida2e95410e3b78d4078499c27b1c82532f5eacb30a05144a9518689e505a3f67b",
            "4604416arm-linux-androideabie00046d14e9fb8e4587d549a4dc8ba4ce3b26c1dfc55365d6e06dfbcd55e56fb",
            "5362300i686-linux-androida646243f5fe5c32c211844ade509390765bd3af47f055901556915f866a51b67",
            "5572612x86_64-linux-androidc2a5cfcdb69fe7ba45ac1109db1c9570108782162b619d2a30490a08f343d68f"
    );
    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.0c/%s_bitcoin%s.tar.xz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.0c/%s_bitcoin%s.tar.xz";
    private final static String URL_LIQUID = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.18.0c/%s_%s.tar.xz";

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
