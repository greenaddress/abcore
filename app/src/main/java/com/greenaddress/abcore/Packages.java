package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.25";
    final static String CORE_V = "0.13.2";
    private final static String GLIBC_MINOR = "1";
    private final static String CORE_URL = "https://bitcoin.org/bin/%s";
    private final static String KNOTS_CORE_URL = "https://bitcoinknots.org/files/0.13.x/%s";

    final static List<PkgH> ARCH_PACKAGES = new ArrayList<>(
            Arrays.asList(
                    new PkgH(String.format("glibc/glibc-%s-%s", GLIBC_MAJOR, GLIBC_MINOR),
                            Arrays.asList(
                                    "armhf1085999a36b1004cda50a6c7bcf8e1bfa4077ac2d26f15c56c3e05c39d144632",
                                    "arm6476532c7ceba41468d64bbccb61db84d85c8ddacb2229077c7e05aadbcd0eb091",
                                    "amd6445b18d6cbfba5b83233782376be78f3f3d196a766775f04f21956b71045467fc",
                                    "i3863c23a0c610ef9ce8d55ad18d77930606fa0f9ed4a5900cff135527b93754f819"
                            )),
                    new PkgH("gcc-libs/gcc-libs-6.3.1-2",
                            Arrays.asList(
                                    "armhfa3ecb4ec57de1ed00bd4cf21c1ce03f6072eadb219a42058930aeea96a340714",
                                    "arm6458e1d21f07c55cf7de00b18d4cf623ff55a3d135510770e6e90bf362317c58a7",
                                    "amd64de2409876f541df642ab1773c2cbfdf9a6266ef2ca4bad184e9a335569b6e26b",
                                    "i386631570e8f9d6760ae315348e3a0a9cbd591c1a1068c941b5a420e2f38b5677fc"
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