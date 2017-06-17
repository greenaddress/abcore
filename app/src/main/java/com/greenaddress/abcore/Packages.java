package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.25";
    final static String CORE_V = "0.14.2";
    final static String KNOTS_V = "0.14.1";
    private final static String GLIBC_MINOR = "1";
    private final static String CORE_URL = "https://bitcoin.org/bin/%s";
    private final static String KNOTS_CORE_URL = "https://bitcoinknots.org/files/0.14.x/%s";
    private final static String BIP148_URL = "http://www.uasf.co/binaries/bitcoin-0.14.1-bip148_segwit0.3-%s";

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

    final static PkgH CORE_PACKAGE = new PkgH(String.format("bitcoin-core-%s/bitcoin-%s-", CORE_V, CORE_V),
            Arrays.asList(
                    "armhff273eb5e56694fe5baecdd5ee8cda9ac495541ccd9df5ca1c22a1b10dc6d89e8",
                    "arm64dd877bc247efa4c90a34ec9ce1a497a8ae1f7eac4c688aa8c8b25ffe30c20541",
                    "amd6420acc6d5d5e0c4140387bc3445b8b3244d74c1c509bd98f62b4ee63bec31a92b",
                    "i3861a302092d9af75db93e2d87a9da6f1f2564a209fb8ee1d7f64ca1d2828f31c03"
            ));

    final static PkgH KNOTS_CORE_PACKAGE = new PkgH(String.format("%s.knots20170420/bitcoin-%s.knots20170420-", KNOTS_V, KNOTS_V),
            Arrays.asList(
                    "armhf87ad8363f32a77853871be1f81e08cb35fd720b57de4569c276376f4658238d2",
                    "arm64ecb64538774b7fcc6f2e40687f7916b4a28a9ee7559114d4d7f8eb2dfe8dfb84",
                    "amd64bab3e6bbe802eb47704d14ffd913712f6b1a8f9ee146d7d9a90979bec3fe68dc",
                    "i386887241fba263bb974072d145eecd3825657dfb0bf902acd5e45868b977e073a0"
            ));

    final static PkgH BIP148_PACKAGE = new PkgH("",
            Arrays.asList(
                    "armhfceca9659d1d22b5a3cb9827a268ac360f60a8d3e14ef5fadd3aa4d9089843821",
                    "arm64a0c70faa9e2e687e4aea2b932857eaa2db089c3c41672bead400ad2268b28b18",
                    "amd6416309ad82fad310937986a7041bdd251a7891ed1b8ccac5fa6ef869c10c89259",
                    "i3862a7d4140e1a9ed615aa7e261d6ac161298e4b6003f9de8d3d3b87de279740ca3"
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
            return String.format(Packages.BIP148_URL, path);
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
