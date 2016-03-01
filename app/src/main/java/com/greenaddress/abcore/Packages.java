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
                        new PkgH("o/openssl/libssl1.0.2_1.0.2f-2",
                                Arrays.asList(
                                        "i386fcd0225aad8263ac91e696b0e6e7a3d322299560a5146b1c9b07659f1c419f8f",
                                        "arm64a7471a10703161255586185fcac0852c1da263ce0d00f8268c9f0c6e1f1d6a3b",
                                        "amd64d572d1e50d298076875b235bfa517e0b2fbe2897535d7145c8c125253e54df82",
                                        "armhf8b4e33b0013fc66fe191b847b0c16a2e0bd300453dedabfe3b0cca8a6f97ff04")),

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

                        new PkgH("libs/libselinux/libselinux1_2.4-3",
                                Arrays.asList(
                                        "i386888f8030910dd202ef9c5cc653c0eaea5c5b32f41d236cd7c6e2222c4a06c218",
                                        "arm6412835d300ea8d3a2b14555868cac5233fd208c790a72d019d75ca13d3b029242",
                                        "amd644f7f8c11735ea4f2dafa1d5a74427882c362abd59519249a92926183e02d750e",
                                        "armhf0734589bfd94cc0eb9a6d46303949f07cff3ec8b550ee19e66ede12c841a790c")),

                        new PkgH("g/gcc-5/libgcc1_5.3.1-8",
                                Arrays.asList(
                                        "i38642f4cdb990777d85b7304c4755ea9a320c0d401bda36a95ee01dcc45baa08f8b",
                                        "arm643cc4d7d33d2cc1cdcbda070e661cefe7a66d38f159d3ce41e8812aefd2f3926b",
                                        "amd6491be11cdd817b682ecd73b7b631ba1edaebdd4c7e1895b7b3e966eb2a4b040eb",
                                        "armhf8651dc5e5be138f56a6c4659ee75a8de3f1974216fd84da6adcaf1c341ed18fa")),

                        new PkgH("g/gcc-5/libstdc++6_5.3.1-8",
                                Arrays.asList(
                                        "i38672b4d1a435a6c21ab19bc30e49529a797d989329fb6931ede91c11f4f61c5005",
                                        "arm64052083c51bd1e0f1f243b6631e1d46ef95273736ba69ed037bd763ebd4784dc4",
                                        "amd649b12a062867bd3c2e40e6bd90ee29b6194c27971aa4e3144eeeab414e6a1b46c",
                                        "armhf890a3f2cd782c5c80314a91b69c665e23a851e918b485ff897077983702f61a0")),

                        new PkgH("d/db5.3/libdb5.3++_5.3.28-11",
                                Arrays.asList(
                                        "i38645ccb0cd1c892eee56ba33dfd80b69eb0479668456244ce779936edbfceaebb1",
                                        "arm64f9dbaba8b3d72b9ff31041d2c55502e4da59c50e726da52140ffe91fc81a3755",
                                        "amd6438021e5e4adaccaf38933358ff87ec8eb243d5bf516bef5cffdc56dd1f182d8e",
                                        "armhfaeb63f1b5c8611a3c4180e819987eb3b5ce9f95a64927d31c1740995d4ed81c8")),

                        new PkgH("g/glibc/libc6_2.21-9",
                                Arrays.asList(
                                        "i386ae15e8b9bbee5db9e2d37dda07cdd6d3601dd6b78e86df641d6a590427332ca6",
                                        "arm64cd570669a665ee70ddf6cb4069a6aa57a56524e4950b4ae3df3862f7a5982fa5",
                                        "amd640b27840c351c57f11223a83c5d99e2fdeeaf9334e51d9c5abdf91d2aa2ef593e",
                                        "armhf6c1f83455940a9b84f59b944bac8be9ae289bd79a1343123ee73fb31027822ce")),

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

                        new PkgH("%s/core/openssl-1.0.2.f-1",
                                Arrays.asList(
                                        "arm64c236caa623d94553aac5102a27b172c048acd071d806241df284f30b8a61f0a5",
                                        "armhf1a2d30cc20515349fdb345fca071f227f8d5a6b5d71df9736ef088a7663e7943")),

                        new PkgH("%s/extra/boost-libs-1.60.0-2",
                                Arrays.asList(
                                        "arm64b0b2fe712893d94739d0b61179e144e61b3a1d4062509833c2764511f28d7452",
                                        "armhfd9ba5b0fadcbbc6984e3c211cb25c5d02563eaa5eb4ac13c287df8eb361c5469")),

                        new PkgH("%s/community/miniupnpc-1.9.20151026-1",
                                Arrays.asList(
                                        "arm6418dc4485f357788247ad1b1db107f27e01ad4543fbb09e331a8d6fe99e5ef064",
                                        "armhfe675dbba4104fe0b3eef2c4bc87afd9bd60d547e01052ba7ce19997a282b0467")),

                        new PkgH("%s/community/bitcoin-daemon-0.12.0-2",
                                Arrays.asList(
                                        "arm647635720bad1586a188520c2d5c1fb1a8593a67866a09a32f19fad4774270d649",
                                        "armhf4dcfe602ac6b9acd82ad10739f8892632733df616f9359443bfb44144389dadf")),

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

                        new PkgH("core/os/%s/openssl-1.0.2.f-1",
                                Arrays.asList(
                                        "amd6481b8f59754f283f222ddccd5cd1fb0afe5b9ef7d81f6b58e3bc8544f08c9f460",
                                        "i3862806ea56b446ed536d45d96974891e1cf29533bf6fc2efd3942cff884f18a180")),

                        new PkgH("extra/os/%s/boost-libs-1.60.0-2",
                                Arrays.asList(
                                        "amd64baf54905cb70828ff1dc7ee43f5d3bac1d25de1e82e49e0758050cdc595482f1",
                                        "i386ea991bcd5d98928e794571aa708cb25943009a80ddbd9af6f49543299ce9a921")),

                        new PkgH("community/os/%s/miniupnpc-1.9.20151026-1",
                                Arrays.asList(
                                        "amd64f306957369f4190a211f66f8023b50ef4dc3afb9351fe4bd3ab958482358252e",
                                        "i38655aaf73bc45be53cd6be0341e064c79a8c1b0b4856b1ae4c3bc89cc01b494817")),

                        new PkgH("community/os/%s/bitcoin-daemon-0.12.0-2",
                                Arrays.asList(
                                        "amd64e43e7136f91daf252fc96affcfab01473321187de8b6c5e0245f9b2fc4554380",
                                        "i386362f1f7c7add7fce9e8448f517260099c731b81ed85e6d9f1a3a4377f9873e13"))
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

    static String getRepo(final Context c, final String arch, final boolean isArchEnabled) {
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
