package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    final static String GLIBC_MAJOR = "2.25";
    final static String CORE_V = "0.14.2";
    private final static String GLIBC_MINOR = "2";
    private final static String CORE_URL = "https://bitcoin.org/bin/%s";
    private final static String KNOTS_CORE_URL = "https://bitcoinknots.org/files/0.14.x/%s";
    private final static String BIP148_URL = "http://www.uasf.co/binaries/bitcoin-0.14.1-bip148_segwit0.3-%s";

    final static List<PkgH> ARCH_PACKAGES = new ArrayList<>(
            Arrays.asList(
                    new PkgH(String.format("glibc/glibc-%s-%s", GLIBC_MAJOR, GLIBC_MINOR),
                            Arrays.asList(
                                    "armhfda380da65a9eb1cdd2c80af78f0a4de7981d05ad7e2428b9e7e46e94d1f49383",
                                    "arm642845cfab38c283693949c849fa6c14928b09dec23e9ab6449fceba4bb0496f40",
                                    "amd640573f7ddb9789154827f795a008e78753f6e71453ed97e66924d9aef8274aac8",
                                    "i3866bc0f5222d7ced3020d4c982749c0c6353b1459ebd6b44a47fcacf19c9fc056b"
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
