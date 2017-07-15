package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.25";
    final static String CORE_V = "0.14.2";
    private final static String GLIBC_MINOR = "4";
    private final static String CORE_URL = "https://bitcoin.org/bin/%s";
    private final static String KNOTS_CORE_URL = "https://bitcoinknots.org/files/0.14.x/%s";
    private final static String BIP148_URL = "https://uasf.bitcoinreminder.com/core-%s-uasfsegwit1.0/%s";

    final static List<PkgH> ARCH_PACKAGES = new ArrayList<>(
            Arrays.asList(
                    new PkgH(String.format("glibc/glibc-%s-%s", GLIBC_MAJOR, GLIBC_MINOR),
                            Arrays.asList(
                                    "armhf8c1e542e0fac554c29a57301c0daf2fde68b0425ddd9b96b029f766dc440833c",
                                    "arm642d08aa49b527817528b6e75043da5ae264448b541043ccc2af9ca52086aabafd",
                                    "amd64efd42f69a712a4202a2cfd1dd7763bb21eee669242ea4038cc971af147f0c33a",
                                    "i386e00bb8779db964581e17dcc93de9158607e2ca7a4397b36e59ba84c673d67258"
                            )),
                    new PkgH("gcc-libs/gcc-libs-7.1.1-2",
                            Arrays.asList(
                                    "armhf16c07c6b81cc9b07e42356f680cdf42b3fb6cc3ffdf4ab47cfdd163e077f1882",
                                    "arm64c81f122d275171fc1c77dd8871f6fa03ad193fd314d802fc64e7265c874cb1ed",
                                    "amd64f9d11c0b924638a0ac4100c006997f4f0ca102750c0b4c222f19ab00942a579a",
                                    "i386fe4832dab14c7ebdb13e49e1568538a8f3bdb2d7530e7d41265bdde2d29ce845"
                            ))
            ));

    final static PkgH CORE_PACKAGE = new PkgH(String.format("bitcoin-core-%s/bitcoin-%s-", CORE_V, CORE_V),
            Arrays.asList(
                    "armhff273eb5e56694fe5baecdd5ee8cda9ac495541ccd9df5ca1c22a1b10dc6d89e8",
                    "arm64dd877bc247efa4c90a34ec9ce1a497a8ae1f7eac4c688aa8c8b25ffe30c20541",
                    "amd6420acc6d5d5e0c4140387bc3445b8b3244d74c1c509bd98f62b4ee63bec31a92b",
                    "i3861a302092d9af75db93e2d87a9da6f1f2564a209fb8ee1d7f64ca1d2828f31c03"
            ));

    final static PkgH KNOTS_CORE_PACKAGE = new PkgH(String.format("%s.knots20170618/bitcoin-%s.knots20170618-", CORE_V, CORE_V),
            Arrays.asList(
                    "armhf8ebbed1080f8b1d447c9c5f6ddd7f0a80ed020cc23cf081a7a52b335d054df01",
                    "arm64d265d670f09f45c9dc1836dae8cf3bc05b5ece18ac80c084bf91328b44c22234",
                    "amd64d1c311ef1f65257b545707c82729af217da34df08ffd23cf92060c3b7bb0595c",
                    "i3867b9c1a454390c5f29051429e20a477ccc30c2d7afdccc4cd68443e37a4d687d5"
            ));

    final static PkgH BIP148_PACKAGE = new PkgH(String.format("bitcoin-%s-uasfsegwit1.0-", CORE_V),
            Arrays.asList(
                    "armhffa5ca48172aac3bd59ad37e78cc15f6af2ec79507eeb21a630bc96dbd92af74b",
                    "arm640c9dba1e5fbe92ad7201c55bdc4ee7f8212a68a1126f12e2130a21b8ad151c6e",
                    "amd64f07f6c29d63492120ff770ee50875d60354f420ee9272c419dd1321493a6d656",
                    "i386411af7c9d84ee0cb46cbf72d40b81a793cabc975078dddeaf7dbffa4c4b9903d"
            ));

    private static String getRepo(final String arch) {
        if (arch.equals("amd64") || arch.equals("i386"))
            return "https://archive.archlinux.org/packages";
        else
            return "http://tardis.tiny-vps.com/aarm/packages";
    }

    static String getPackageUrl(final Packages.PkgH pkg, final String arch) {
        final boolean isArmArchitecture = !arch.equals("amd64") && !arch.equals("i386");
        final String osArch = Utils.getArchLinuxArchitecture(arch);
        final String fileArch = arch.equals("armhf") ? "armv7h" : osArch;
        final String template = "%s/%s/%s-" + (isArmArchitecture ? fileArch : osArch) + ".pkg.tar.xz";
        final String repo = getRepo(arch);
        return String.format(template, repo, pkg.pkg.charAt(0), pkg.pkg);
    }

    static String getCorePackageUrl(final Packages.PkgH pkg, final String arch) {
        final String packageName = arch == null ? Utils.getCorePkgsName(): Utils.getCorePkgsArch(arch);
        final String path = String.format("%s%s.tar.gz", pkg.pkg, packageName);
        if (pkg.pkg.contains("bitcoin-core"))
            return String.format(Packages.CORE_URL, path);
        else if (pkg.pkg.contains("knots"))
            return String.format(Packages.KNOTS_CORE_URL, path);
        else
            return String.format(Packages.BIP148_URL, CORE_V, path);
    }

    static class PkgH {
        final String pkg;
        final List<String> archHash;

        PkgH(final String pkg, final List<String> archHash) {
            this.pkg = pkg;
            this.archHash = archHash;
        }
    }
}
