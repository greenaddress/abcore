package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.24";
    final static String CORE_V = "0.13.1";
    private final static String GLIBC_MINOR = "2";
    private final static String CORE_URL = "https://bitcoin.org/bin/%s";
    private final static String KNOTS_CORE_URL = "https://bitcoinknots.org/files/0.13.x/%s";

    final static List<PkgH> ARCH_PACKAGES = new ArrayList<>(
            Arrays.asList(
                    new PkgH(String.format("glibc/glibc-%s-%s", GLIBC_MAJOR, GLIBC_MINOR),
                            Arrays.asList(
                                    "armhf7e162f4ac00caa4944c5b264a67864dea29c52f22995863641d0c69e83bed042",
                                    "arm64db24c58d9f2501ee622f631d081333fda50c020eaf2bcb043c9473198fa34d44",
                                    "amd6487721ded792e35b8fd2110d4ac91dc229a9cbbd2fd612d21991f7d68a34e195b",
                                    "i38657a56380c7b57f0882b659d7f44d52082c6d4428dbc51392a3fe707b31ebb82a"
                            )),
                    new PkgH("gcc-libs/gcc-libs-6.2.1-1",
                            Arrays.asList(
                                    "armhf21542b9a207db719072cb33f414689cc249a894f1eb301b2d820ec79fa4190db",
                                    "arm647865551b282990a194808fbed9bf62a57173269869e7263b33b6f63f3e8144b8",
                                    "amd64a44edb4dfaf920c6ce5e9289e348b4de59ec98fa923ac6ea8cb68302e7a193e0",
                                    "i38644add8cdd72b66d4c8f70ba0c704c4017c12561d2e684fc02f8d5ae6fda83534"
                            ))
            ));

    final static PkgH CORE_PACKAGE = new PkgH(String.format("bitcoin-core-%s/bitcoin-%s", CORE_V, CORE_V),
            Arrays.asList(
                    "armhfe84620f51e530c6f7d2b4f47e26df3f365009b2f426f82f6ca3bc894c7cdcb46",
                    "arm64cce8417f27953bf01daf4a89de8161d70b88cc3ce78819ca70237b27c944aa55",
                    "amd642293de5682375b8edfde612d9e152b42344d25d3852663ba36f7f472b27954a4",
                    "i38663a5f3e602b8640c5320c402f04379d2f452ea14d2fe84277a5ce95c9ff957c4"
            ));

    final static PkgH KNOTS_CORE_PACKAGE = new PkgH(String.format("%s.knots20161027/bitcoin-%s.knots20161027", CORE_V, CORE_V),
            Arrays.asList(
                    "armhf9601c53c0d79376956f75ca4dfb018e3d878bb8e2747ad2efddd8f04f7d84db9",
                    "arm6443b2b43d2c5af60f17cf78d26d39d3ab87c41271c349f02aae1b569715bd5d7e",
                    "amd647a4bc58a11821d406a1537ba32465dd84c93f1ea1154b5092aa9c2d231237133",
                    "i386999e11faeaa377f7222812e24899f869040ed2385ecc2f893e6939c314220852"
            ));

    private static String getRepo(final String arch) {
        if (arch.equals("amd64") || arch.equals("i386"))
            return "archive.archlinux.org/packages";
        else
            return "tardis.tiny-vps.com/aarm/packages";
    }

    static String getPackageUrl(final Packages.PkgH pkg, final String arch) {
        final boolean isArmArchitecture = !arch.equals("amd64") && !arch.equals("i386");
        final String osArch = Utils.getArchLinuxArchitecture(arch);
        final String fileArch = arch.equals("armhf") ? "armv7h" : osArch;
        final String template = "http://%s/%s/%s-" + (isArmArchitecture ? fileArch : osArch) + ".pkg.tar.xz";
        final String repo = getRepo(arch);
        return String.format(template, repo, pkg.pkg.charAt(0), pkg.pkg);
    }

    static String getCorePackageUrl(final Packages.PkgH pkg, final String arch) {
        final String packageName = arch == null ? Utils.getCorePkgsName(): Utils.getCorePkgsArch(arch);
        final String path = String.format("%s-%s.tar.gz", pkg.pkg, packageName);
        if (pkg.pkg.contains("knots"))
            return String.format(Packages.KNOTS_CORE_URL, path);
        else
            return String.format(Packages.CORE_URL, path);
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