package com.greenaddress.abcore;

import java.util.Arrays;
import java.util.List;

class Packages {

    final static String BITCOIN_NDK = "0.16.3";

    private final static String URL = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.16.3/%s_bitcoin%s.tar.gz";
    private final static String URL_KNOTS = "https://github.com/greenaddress/bitcoin_ndk/releases/download/v0.16.3/%s_bitcoin%s.tar.gz";


    final static List<String> NATIVE_CORE = Arrays.asList(
            "3820644arm-linux-androideabi0c9988853f1c4d37912cd8787f385df61cda0e3150655c74db9573c6838f93ed",
            "3818557aarch64-linux-androidd52dbc4078dfdd41f75be08a97968e8ef7d1c0810a0ff85890a042cf5d6b246d",
            "4176548x86_64-linux-androidbb37ad7e63792e15c75f225ccaf1d5b73b4acd881653d4780e933e8d771cb030",
            "4088035i686-linux-androidc585a3293cbc968eab684b1a1c76b6ee854d710402c17304eb9848c75c826425"
    );


    final static List<String> NATIVE_KNOTS = Arrays.asList(
            "4004146arm-linux-androideabi241d614774733ab68d98299e6eb9c313238ad27830e8364d03006f7543b00a34",
            "3998141aarch64-linux-androidb415944c32948f4ceab4447087e5031aed06acc57120f7a45f55300862258373",
            "4411787x86_64-linux-androidcf05c6d6be9d628a0bc6fc2c3bf9202e84f9afc1d6a0db3f995aa59261fffa5d",
            "4320595i686-linux-android14c0d2249d91b679f69bea52ffe1343284142c4dde02622aefee0c30bee86c9c"
    );

    static String getPackageUrl(final String distro, final String arch) {
        if (distro.equals("core")) {
            return String.format(URL, arch, "");
        }
        return String.format(URL_KNOTS, arch, "knots");
    }
}
