package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.25";
    final static String CORE_V = "0.15.0";
    private final static String GLIBC_MINOR = "4";
    private final static String CORE_URL = "https://bitcoin.org/bin/%s";
    private final static String KNOTS_CORE_URL = "https://bitcoinknots.org/files/0.15.x/%s";

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
                    "armhfec6b9e0ea467f82f2f9938f8577fb41cb7c2998b027709f78b8aff02afc983a9",
                    "arm64ec5e93ebc747d3d50b6c3bc33ac840348820b0e681de734999ebc4e671803a8e",
                    "amd64ed57f268d8b5ea5acfcb0666e801cf557a444720d8aed5e812071ab2e2913342",
                    "i38675de087adf888f15faa4d8a65ea18dee75150ee761b0d6bcaefc7770230e1e66"
            ));

    final static PkgH KNOTS_CORE_PACKAGE = new PkgH(String.format("%s.knots20170914/bitcoin-%s.knots20170914-", CORE_V, CORE_V),
            Arrays.asList(
                    "armhf167c39160a46d8a2bd907195a7b1073f02fadb7d7b880a3be63e293337b6e773",
                    "arm64195053bb307ac6adb2d87005874d9a5ede6e1888a6a485091cff6520249e683e",
                    "amd6432ccb73b883fd35668f547f239c6099b298fdd79eb773b5025d129d751715e6f",
                    "i3868753b12cb889ef95b9a3c2abab84aef71d1eee8c9b0cbcafbc2fd13ec75ac8a8"
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
        throw new RuntimeException("Package not found");
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
