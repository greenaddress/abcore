package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.25";
    final static String CORE_V = "0.14.0";
    private final static String GLIBC_MINOR = "1";
    private final static String CORE_URL = "https://bitcoin.org/bin/%s";
    private final static String KNOTS_CORE_URL = "https://bitcoinknots.org/files/0.14.x/%s";

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
                    "armhf55957e2c35aa2ba836cbae7cbf945bcf489a46b243551b0f6fd86f60603032a6",
                    "arm64466adccf7352f06de35afc1627a3ea721764268ceaf08fa3641f9b47e7df091a",
                    "amd6406e6ceeb687e784e9aaad45e9407c7eed5f7e9c9bbe44083179287f54f0f9f2b",
                    "i386e4bb8b52acde07788dfcf024645fe291f0deca2b7172939fb2ddb8789fe56973"
            ));

    final static PkgH KNOTS_CORE_PACKAGE = new PkgH(String.format("%s.knots20170307/bitcoin-%s.knots20170307", CORE_V, CORE_V),
            Arrays.asList(
                    "armhfebcdcd00e92cba237b256fbe94d665a032a5936a8e7937fe9f558e8b37bcf20f",
                    "arm641e812a26e9ca8a87281c485d33057105b757c02b61d29244203d8860471360eb",
                    "amd64ef315eeaf088e8a15d2c18204869279a38fd5f65b00201dfd3ccbc4559ad705e",
                    "i386b031deab8d7432302c1c76803336c5e06a17a448fe3c574364a83dfe6a394ba4"
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