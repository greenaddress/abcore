package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.25";
    final static String CORE_V = "0.15.1";
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
                    "armhfceba092c9a390082ff184c8d82a24bc34d7f9b421dc5c1e6847fcf769541f305",
                    "arm64d64d2e27cad78bbd2a0268bdaa9efa3f1eca670a4fab462b5e851699c780e3a0",
                    "amd64387c2e12c67250892b0814f26a5a38f837ca8ab68c86af517f975a2a2710225b",
                    "i386231e4c9f5cf4ba977dbaf118bf38b0fde4d50ab7b9efd65bee6647fb14035a2c"
            ));

    final static PkgH KNOTS_CORE_PACKAGE = new PkgH(String.format("%s.knots20171111/bitcoin-%s.knots20171111-", CORE_V, CORE_V),
            Arrays.asList(
                    "armhfa69398ec03b6c1ca8fca6ab8140759bc52175c6d0efed825c5f7b807f16eb672",
                    "arm6469ca02bcdfd1cb005069d5cd9586d032e308d304134235d7c4dd450ff830b12c",
                    "amd641f12e32e18974bdd6f820f01596c0b2789278f53425f3cbc8d7508b0617c76ca",
                    "i3863aff13e30b00bf4ae6aa24c88c3b7dbd56c918d7b7b3b260d2e4ce9f50973e21"
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
