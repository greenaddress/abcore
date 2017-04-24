package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.25";
    final static String CORE_V = "0.14.1";
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
                    "armhfcd23ffe044b56dd56d3b9ba384e606c44000b60f44e0a74a19c313a4f30ea5c8",
                    "arm64a60d7c8dde9b77e7ff547976ce37db1fe98c71833003465befe650d6bc102b6b",
                    "amd640c6920a9f3181a95ca029fdac5342b5702569ee441ec2128d19051f281683058",
                    "i386ff6bf851dae036905de6272562cca4b94c4842f758b7bd68879a088fe7b0f662"
            ));

    final static PkgH KNOTS_CORE_PACKAGE = new PkgH(String.format("%s.knots20170420/bitcoin-%s.knots20170420", CORE_V, CORE_V),
            Arrays.asList(
                    "armhf87ad8363f32a77853871be1f81e08cb35fd720b57de4569c276376f4658238d2",
                    "arm64ecb64538774b7fcc6f2e40687f7916b4a28a9ee7559114d4d7f8eb2dfe8dfb84",
                    "amd64bab3e6bbe802eb47704d14ffd913712f6b1a8f9ee146d7d9a90979bec3fe68dc",
                    "i386887241fba263bb974072d145eecd3825657dfb0bf902acd5e45868b977e073a0"
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