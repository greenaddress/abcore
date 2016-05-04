package com.greenaddress.abcore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Packages {

    private final static List<PkgH> ARCH_PACKAGES;
    private final static List<PkgH> DEB_PACKAGES;

    static {

        // FIXME: some deps are not needed, ideally we just build what we need with a static binary, built with the NDK
        // This works for now

        DEB_PACKAGES = new ArrayList<>(
                Arrays.asList(
                        /*86***/
                        new PkgH("b/boost1.58/libboost-filesystem1.58.0_1.58.0+dfsg-5+b1",
                                Arrays.asList(
                                        "i38678f7a8c73ee7be22f8383ddadb27b774f356b9a10c90a9a953becda64aa088e1",
                                        "amd642cd7ef494a4f0f46b7a0310c8244dbdd4559038f8ffc726c66a8fa5685e9d217")),

                        new PkgH("b/boost1.58/libboost-program-options1.58.0_1.58.0+dfsg-5+b1",
                                Arrays.asList(
                                        "i386aa2ab0cfff3cfd4a22ec6f8a55b016b2acc6052b7bfc2fef095970a622d4b434",
                                        "amd64db97812aad29d040fa076298cbc5ba0e992132369ed0efbdaa45c28dd1d0cec3")),

                        new PkgH("b/boost1.58/libboost-system1.58.0_1.58.0+dfsg-5+b1",
                                Arrays.asList(
                                        "i38658cead2e2c77db69975333f133b8e9cafe1d0b5d81fa9673284aa378e44bf9df",
                                        "amd6442b4f4ea64e9eb692f886cc5de2e5dbf94e9b7288cde8f697f23e927a73dd2b3")),

                        new PkgH("b/boost1.58/libboost-thread1.58.0_1.58.0+dfsg-5+b1",
                                Arrays.asList(
                                        "i386a986b0ab14650e3a4f37823696f2939e3e1aca1c1586244b718f1d8a8d90dd00",
                                        "amd6439261f6a5ae0062103e70007f5223b9c1e05895a838e463bcde6b54072ef4481")),

                        new PkgH("b/boost1.58/libboost-chrono1.58.0_1.58.0+dfsg-5+b1",
                                Arrays.asList(
                                        "i3862fe7a67d2a13cc2a5957a97ff6c5b7a1342b737868ee586bd816796019707345",
                                        "amd6459d6099be900dc2326e6e2ef1750df80a7d6d265ddfc35453b41bc4651f176ac")),

                        /*arm**/

                        new PkgH("b/boost1.58/libboost-filesystem1.58.0_1.58.0+dfsg-5",
                                Arrays.asList(
                                        "arm64534d11226c832c2acf322037ffe3967e5ce726b6cd61308ac07a19e8df83e73b",
                                        "armhfebc6623867c05236f5b731c53e39b88bf085923580c5c92f1dfd2c39ab73cd48")),

                        new PkgH("b/boost1.58/libboost-program-options1.58.0_1.58.0+dfsg-5",
                                Arrays.asList(
                                        "arm645b582dd2845307c2483969dbcb3294312b22e11a442e57c8b21595489cdc9f5d",
                                        "armhfa5fa18b5a05e2eb5c068b9395b743f38514a1ece48c2fc1631abbf34b8646c47")),

                        new PkgH("b/boost1.58/libboost-system1.58.0_1.58.0+dfsg-5",
                                Arrays.asList(
                                        "arm64f067925c073370d145ccef370a7ac86c7b49a7793f2cae25540cac12c8bf496a",
                                        "armhfe589e197a4f42ce7198bd30917f9a38ae155b53a762b2b99b82823470c8e2f1f")),

                        new PkgH("b/boost1.58/libboost-thread1.58.0_1.58.0+dfsg-5",
                                Arrays.asList(
                                        "arm6450ff76c9d2eb31894b1db0362193aa2de61bd16b5c3ce2ef797a2f4bb3d2b50f",
                                        "armhf9441e56c8e5d8c07634f77868208ea9fce335ca9d97d76d804000c1f49ff421a")),

                        new PkgH("b/boost1.58/libboost-chrono1.58.0_1.58.0+dfsg-5",
                                Arrays.asList(
                                        "arm646005a147386e1aa61c56889a4d545ab75bf826abcd8fa505825b3cad774a00a5",
                                        "armhf7ea3951210eb664e8d2f3f1a8547da42a7c9a9984f2e5839c8d1ba28b429ec5d")),
                        /*common**/
                        new PkgH("o/openssl/libssl1.0.2_1.0.2g-2",
                                Arrays.asList(
                                        "i3865b41ce89a2cf1bcca6439f9aa3a8b982b5e58aab39a106c7d048f1016b8f08d6",
                                        "arm64982288bd02005ba22aa74ceee09c6cbfebbfc315d626be98e2d4966d7c41e995",
                                        "amd642ff9a50ce3410851a29afdac612911b5f60c218fef4742df2b890b5ffe7b6204",
                                        "armhf7bd489e7ea95b9068254155416ea4b02dc5f211214ec54abe3028a93045f8107")),

                        new PkgH("m/miniupnpc/libminiupnpc10_1.9.20140610-2.1",
                                Arrays.asList(
                                        "i386ef7079457688f33e309efcd0c085128a82b7e8abe34b134ffa7a315b4648cb3a",
                                        "arm642e883d58e85a18929433a518ef0ae086d3ae2ddc2a440e884cb5ac9379fcd4ea",
                                        "amd6409325d8d60fdebf88438840c5881e6f079657495495958e21abf9b8ebb4eefcc",
                                        "armhf9561320481bb96d0aed587d52baee5d78f07407fdab60e99b2b7bd4b03fe36f1")),

                        new PkgH("a/acl/libacl1_2.2.52-2",
                                Arrays.asList(
                                        "i3861ed4d8acb3c432ff559b1ea88bafac7d60da3dd8c58ca40d41fc17c70761f72f",
                                        "arm64534dfd1cdf887a4b98be2882d93c72ad0cd439c60c50ae6d725c832d759e4deb",
                                        "amd64209a2c0c192d7debe0453a671f443dc73120c8c062ce66abe44afd06e711c801",
                                        "armhff16dc508fb664be6320514ac7c0ccedbdd67848eb6d284478719b5e842179e08")),

                        new PkgH("a/attr/libattr1_2.4.47-2",
                                Arrays.asList(
                                        "i3867f0c5ecfacd122cd64a7ab05aa3f125f53303ebae468399efe1af7eb40ef7c72",
                                        "arm645a16a0b5477ba134a17f14101fa1f2da94513cdeb4c5fa3ce9613512433f9c15",
                                        "amd64ebe8b6a4c62b88db772f05c05b50d8b9f66f4d46488c547bda5042ae36865d88",
                                        "armhf34761c1df4f6f9ffe823cccb412ec11bd6b7906565ab2a9abad024d3efee1e19")),

                        new PkgH("libs/libselinux/libselinux1_2.5-1",
                                Arrays.asList(
                                        "i38682f965830a53d3dc51e31886567f29328d11a0e8b23ce319524497440b29968c",
                                        "arm64858b729597076220c4e93b64890a140a3e86caeee7cc29d2562d4ca4fc1126ae",
                                        "amd64ca46da23c569041dab92a218624854546379e4c53cb8151396e3fb4eab7869aa",
                                        "armhf308b40ecc7b77ec73818245bb1f8f8f4937d4ec8af6a0aaeb945d7b757504499")),

                        new PkgH("g/gcc-5/libgcc1_5.3.1-14",
                                Arrays.asList(
                                        "i386736c688d7821df61a6ed1ad367ef54d364913d738440fd257f84b7df0b6b0014",
                                        "arm640f81279f43b73a740ca39ea673c89e7aaa3f22f965a627b23747d93c2f117e3a",
                                        "amd647dbd839deaafd69bf6eeac2bfd3371595d23bce7ff2326bd19011cfc875d0fb6",
                                        "armhf89442fda81b5b3664957201cf2a931b3e2c1a4eaf992eb9cb25e9f4e0fbbb3ed")),

                        new PkgH("g/gcc-5/libstdc++6_5.3.1-14",
                                Arrays.asList(
                                        "i386d1ff2c2ac83819ef0f4d587d91891c904861bf4d30a3d7f1622644a41582e301",
                                        "arm6474c45a8b9ccb51060e067e6d03f6af4c42e39e296fdfca9809ac8484972fc303",
                                        "amd64cbdf67b77ee394995ec5e5a037484b21af45d2709eda9f158c629cd6eb150a99",
                                        "armhfb23aa5afc511151f5249c61ed3928192a5d7d18007c783ccf9c4878aff12f365")),

                        new PkgH("d/db5.3/libdb5.3++_5.3.28-11",
                                Arrays.asList(
                                        "i38645ccb0cd1c892eee56ba33dfd80b69eb0479668456244ce779936edbfceaebb1",
                                        "arm64f9dbaba8b3d72b9ff31041d2c55502e4da59c50e726da52140ffe91fc81a3755",
                                        "amd6438021e5e4adaccaf38933358ff87ec8eb243d5bf516bef5cffdc56dd1f182d8e",
                                        "armhfaeb63f1b5c8611a3c4180e819987eb3b5ce9f95a64927d31c1740995d4ed81c8")),

                        new PkgH("g/glibc/libc6_2.22-7",
                                Arrays.asList(
                                        "i386e0cf385bfa08c17b1ddbf15a5ef6cec5b4d287f5297dd589c89c355e6a15544b",
                                        "arm64780d335cae334d308bd5eeb7ab1b5bb23e7ad5121d7d6f644473fda9c32d6d64",
                                        "amd649292aecdd8dd76bb4cb0826ec0adf12c5c01dacf8d53d0ce32770315ab153d97",
                                        "armhf5d92a0f0d284c311acf313eeeecb03782c31d9d7c18de57fd040ffe11c2c433c")),

                        new PkgH("b/bitcoin/bitcoind_0.11.2-1",
                                Arrays.asList(
                                        "i386347449b94112686dc31d86e4dbc026a30d0352a60edf58365ae33e0117b90b0b",
                                        "arm64f027c349237acc1f485a9f52dddbdcf81b888f8ca496e64b3ea9e8b88405bea5",
                                        "amd6437b25a47c7e4490df58bd6fbe292e6f224fd69c2bc28f57af846b67ffaf12728",
                                        "armhf8ef112f4fca5ba20e7d701095193d00e6e07e5ccbb1c8d0f52c11cb3c9fe3b04"))
                )
        );

        ARCH_PACKAGES = new ArrayList<>(
                Arrays.asList(

                        /*arm64 and armhf only*/

                        new PkgH("%s/core/glibc-2.23-1",
                                Arrays.asList(
                                        "arm64db36a92014f13cfe5c1cfd3be06cdd2fc436332a4e9489fa5c63668ec1f83a88",
                                        "armhffe83fcfbb14c914f5095199e30d1efb7fe5d8649942c6fb0bd03242adc8dcfd5")),

                        new PkgH("%s/core/gcc-libs-5.3.0-5",
                                Arrays.asList(
                                        "arm6459a1af9e6a91e8a8258ca4bc90d94a47704834c4569ad07518e11cbe00c6bb12",
                                        "armhf486f733b7a355fb3eb85dbf293fe157c7d35624812a0b5ebd0fa4a5dc8a229eb")),

                        new PkgH("%s/core/db-5.3.28-3",
                                Arrays.asList(
                                        "arm64327fe79a326826d93fa370b5e10ff0fd0138beee6a0ecddd00df3b76bf50ec8b",
                                        "armhfd1b7ede577211e916dae5da681702744ac673a6756a194f404e031451356b528")),

                        new PkgH("%s/core/zlib-1.2.8-4",
                                Arrays.asList(
                                        "arm6487e2e8bb8aaf38fda9dd4d185b0bef2669113098d13804dc43d2a87f8b78181c",
                                        "armhf2cffcc9a1a50b413759d4d5de8c53cb63ffb4df1a8f804e9e0c8325a413dd784")),

                        new PkgH("%s/core/openssl-1.0.2.g-3",
                                Arrays.asList(
                                        "arm64e7fc10fd2d73f6d5fe2980174ab7717c388bdd61d88216339f4fb42a65c7a31e",
                                        "armhf79012fb8dd55595930f5813d5be27c3c50748047446b7398fb9c968320dac4bd")),

                        new PkgH("%s/core/libevent-2.0.22-2",
                                Arrays.asList(
                                        "arm6437d50ef86bcc553ab3110cbffd13094ceb7774fe8954d0b6d5629a812bae400a",
                                        "armhff173ef8111c1b28a3ab71817f45c38a62e91c50eff430ab6b4bd3b0f8095d0e7")),

                        new PkgH("%s/extra/boost-libs-1.60.0-4",
                                Arrays.asList(
                                        "arm64c16aca8c1d6b255221b926e4e1ae96f5754087ccc14e56cf455316ce6260014d",
                                        "armhfbb474bcf4022ed01f7d4b29a778999f5c534c350e064196eceb3999f225398d1")),

                        new PkgH("%s/community/miniupnpc-1.9.20151026-1",
                                Arrays.asList(
                                        "arm6418dc4485f357788247ad1b1db107f27e01ad4543fbb09e331a8d6fe99e5ef064",
                                        "armhfe675dbba4104fe0b3eef2c4bc87afd9bd60d547e01052ba7ce19997a282b0467")),
                        new PkgH("%s/community/bitcoin-daemon-0.12.1-1",
                                Arrays.asList(
                                        "arm6469598c7ad5bb0bc627ade46179e2f9c08acd541a0b3f5d61583f02af13d1de07",
                                        "armhf3cb3d7c4ad5b635d6f45a7cea7f63c0e5b344ab8c864bad0b951b7639d865fea")),

                        /*686 and  x86_64 only*/

                        new PkgH("core/os/%s/glibc-2.23-1",
                                Arrays.asList(
                                        "amd64da9c393c55b258dc4ea0a5b12d386cb07b7c415b211e8b5c2a72772929a923e8",
                                        "i3866501272aab67dc7e70265b1d4d4004d16dc17cf3616394f935eefac101b6ac53")),

                        new PkgH("core/os/%s/gcc-libs-5.3.0-5",
                                Arrays.asList(
                                        "amd649c4c2c8ab0a97b69315d124027d548db4a95c61a3955d2c387c627d8dd9f3747",
                                        "i386f89135089471fffc0d1a0f6c48252346476688cfa9d34ffc0ecf5b6d971e3145")),

                        new PkgH("core/os/%s/db-5.3.28-3",
                                Arrays.asList(
                                        "amd64cf6c0b4599cf34e137c68a2b760a75c5110eb44fd707d8085673efaf604d9bee",
                                        "i38637376a77cbf4958738deabf7b207c015cc04670563ae11e55ad46773fdb37c56")),

                        new PkgH("core/os/%s/zlib-1.2.8-4",
                                Arrays.asList(
                                        "amd6418b76944a0470685f012f56e397d63a2700365fb8e1e8204239517462c2f696e",
                                        "i386a4e7f7adf197493f0c47c272949baa2bbb3fe6e32db26722b293337162e023f4")),

                        new PkgH("core/os/%s/openssl-1.0.2.g-3",
                                Arrays.asList(
                                        "amd64de51ec48b56fda0206c05d4ef6c0d86c96bec02ccc44861968ae8d4863e3c3cd",
                                        "i386defc8e3c3996c82f7ed37add41b74e48021789b3c9c6f45bcab20633cadb836a")),

                        new PkgH("core/os/%s/libevent-2.0.22-2",
                                Arrays.asList(
                                        "i386622509ff4df583be77e3c5a57f6c930adc1bdc73c2b92737574b4bac4d6f969d",
                                        "amd64044cbe2d2611966a5a8e05dfc1defadce58e5295b42ef82d52c85c9c2f4f9120")),

                        new PkgH("extra/os/%s/boost-libs-1.60.0-4",
                                Arrays.asList(
                                        "amd642e8d23f8e9c36ba04c662c3e3b2b975b582e85bf8849ddb6cbc74373386fd92c",
                                        "i3868200666f4811427e98760f7df193820ce534ea11efc712ae2dab85ae45e298f1")),

                        new PkgH("community/os/%s/miniupnpc-1.9.20151026-1",
                                Arrays.asList(
                                        "amd64f306957369f4190a211f66f8023b50ef4dc3afb9351fe4bd3ab958482358252e",
                                        "i38655aaf73bc45be53cd6be0341e064c79a8c1b0b4856b1ae4c3bc89cc01b494817")),

                        new PkgH("community/os/%s/bitcoin-daemon-0.12.1-1",
                                Arrays.asList(
                                        "amd64d7cf860a1e3aec30f0954e46457e905671621eca18cd07dcb7f92ffcb825ffe6",
                                        "i386d5c000b2c7a272177625bba988666e9d57226a36e98995203cdc29dbd02538b9"))
                ));
    }

    private static List<PkgH> getPackages(final String arch, final List<Packages.PkgH> static_pkgs) {
        final List<PkgH> pkgs = new ArrayList<>();
        for (final PkgH d : static_pkgs) {
            for (final String s : d.archHash) {
                if (s.startsWith(arch)) {
                    pkgs.add(d);
                    break;
                }
            }
        }
        return pkgs;
    }

    private static String getRepo(final Context c, final String arch, final boolean isArchEnabled) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        if (isArchEnabled) {
            if (arch.equals("amd64") || arch.equals("i386")) {
                return prefs.getString("archi386Repo", "archlinux.openlabto.org/archlinux");
            } else {
                return prefs.getString("archarmRepo", "eu.mirror.archlinuxarm.org");
            }
        } else {
            return prefs.getString("debianRepo", "ftp.us.debian.org/debian");
        }
    }

    static String getPackageUrl(final Packages.PkgH pkg, final Context c, final String arch, final boolean isArchLinux) {

        final String osArch = Utils.getArchLinuxArchitecture(arch);

        final boolean isArmArchitecture = !arch.equals("amd64") && !arch.equals("i386");
        final String repo = getRepo(c, arch, isArchLinux);

        final String fileArch = arch.equals("armhf") ? "armv7h" : osArch;

        final String template = isArchLinux ?
                (isArmArchitecture ? "http://%s/%s-" + fileArch : "http://%s/%s-" + osArch) + ".pkg.tar.xz" : "http://%s/pool/main/%s_%s.deb";

        return isArchLinux ? String.format(template, repo, String.format(pkg.pkg, fileArch)) : String.format(template, repo, pkg.pkg, arch);
    }

    public static List<PkgH> getDebPackages(final String arch) {
        return getPackages(arch, DEB_PACKAGES);
    }

    public static List<PkgH> getArchPackages(final String arch) {
        return getPackages(arch, ARCH_PACKAGES);
    }

    public static class PkgH {
        final String pkg;
        final List<String> archHash;

        PkgH(final String pkg, final List<String> archHash) {
            this.pkg = pkg;
            this.archHash = archHash;
        }
    }
}
