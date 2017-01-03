package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.24";
    final static String CORE_V = "0.13.2";
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
                    "armhf3c460784d3ab64645d48389c467336a38da473706a69f22f39cfcce5e0f33780",
                    "arm64eda24dcf0b9fae606eb9811f74ddba69a3287316950f3f02b3000b6b1c02b65f",
                    "amd6429215a7fe7430224da52fc257686d2d387546eb8acd573a949128696e8761149",
                    "i386790e4c7ebf9f4a734d1d2b6bb5e9f5fb3f613f6f93da30fd1420c5b4115dd72f"
            ));

    final static PkgH KNOTS_CORE_PACKAGE = new PkgH(String.format("%s.knots20170102/bitcoin-%s.knots20170102", CORE_V, CORE_V),
            Arrays.asList(
                    "armhf6c28eba0a8dff111ebac6c884d37085de983ed407e88f61dc3de17d25374db0a",
                    "arm64fd382b8550b037d9727d6857027226d783ab30fd7cbb2955da8d6c1bc6d208b8",
                    "amd6475a26f60eee819158faa0ca561a0490a9208139f9a73a986931051089f1fbb9f",
                    "i3865054ce09894890f5b63fe112e8adcf343a0879e760ce97c8d29ef8ffc0addca6"
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