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
                        new PkgH("b/boost1.58/libboost-filesystem1.58.0_1.58.0+dfsg-4.1",
                                Arrays.asList("i386a0777b96dc4c91a556532248475b8ebf31b2e8fcc649568fb0473f673908c530",
                                        "arm646e158e7d1e0916ec6def192988965cfcb65f1b6777a94f235a7f8a085e08f178",
                                        "amd6437363a8a74e801e3df03574cf13a51a4d78f04e2e0e21d060fcbd8ac48f004fd",
                                        "armhf0cb9201316708bdff6c1bdd18ddeaa7503269435d1c94417a1169e5be5d5a0fd")),

                        new PkgH("b/boost1.58/libboost-program-options1.58.0_1.58.0+dfsg-4.1",
                                Arrays.asList("i386fc25c38b9a955e4d65ae7ba309b05a7c7fcfef98f964225614a059c7ac69baeb",
                                        "amd647fe601bf40e32feb90ea8cc37d962f8022600957c5c247cf5f91fd93527e0336",
                                        "arm645fca1d1e15c54053fa7608884d686fe0ee9775002b752fa971074063c3629051",
                                        "armhf8762d894e483f315f567dc07cd4873e1240f41b9e079f239650d584efe1b9e4f")),

                        new PkgH("b/boost1.58/libboost-system1.58.0_1.58.0+dfsg-4.1",
                                Arrays.asList("i386b0b1e0055fc06e0411e1adaa4657ab123aa3c6e5ebd49ff6a39e588c3538629a",
                                        "arm644a07555d104395e45a60ba6f65d99f59d6e3282a2cb9c215b4e497a246e247bd",
                                        "armhf0a0e20823ba12a5283d6c37ef71509b9841c2587e62949a67b44f94d22bcad76",
                                        "amd64bc34026c243ccea8ffb31bfd515f70a7ac0f36dbc8d3a507028dd08b307a079d")),

                        new PkgH("b/boost1.58/libboost-thread1.58.0_1.58.0+dfsg-4.1",
                                Arrays.asList("i38690c9e5b570f60811a627c08d7443cb370f530f4bcbdc9c0d0bf4cc073c6565fe",
                                        "arm647c606a90cea803ef0049b9cef55008c8b3aa47328ebbe4735df82a570cad1e48",
                                        "amd6473dd86acba008cdf2a856b7500e95d05d0f9fb08d5d72c5f8330f96c2c2f1a1e",
                                        "armhf340e3776994a91c0eebc2abdd0fdf0c639d5a14763df932e30250852dc1f2873")),

                        new PkgH("b/boost1.58/libboost-chrono1.58.0_1.58.0+dfsg-4.1",
                                Arrays.asList("i386cd420cb2b5b1a470a74be22aeb9c97dce264e98c5e1e1b0e6a705d0bb7aa3c22",
                                        "arm6483d69060988e99079a34defaa00c08a7fbd71ef6e9ab5d1f949dcb1a38b50e66",
                                        "amd640a8b467fe289b2910021db1aa1a68e1e55b2b6047b065e7bd7f822d7769b4ec8",
                                        "armhf9a797522c731bca8c324eb88f17f4b9c8d3445f789c06589fb95f1195d5a2971")),

                        new PkgH("o/openssl/libssl1.0.2_1.0.2f-2",
                                Arrays.asList("i386fcd0225aad8263ac91e696b0e6e7a3d322299560a5146b1c9b07659f1c419f8f",
                                        "arm64a7471a10703161255586185fcac0852c1da263ce0d00f8268c9f0c6e1f1d6a3b",
                                        "amd64d572d1e50d298076875b235bfa517e0b2fbe2897535d7145c8c125253e54df82",
                                        "armhf8b4e33b0013fc66fe191b847b0c16a2e0bd300453dedabfe3b0cca8a6f97ff04")),

                        new PkgH("m/miniupnpc/libminiupnpc10_1.9.20140610-2.1",
                                Arrays.asList("i386ef7079457688f33e309efcd0c085128a82b7e8abe34b134ffa7a315b4648cb3a",
                                        "arm642e883d58e85a18929433a518ef0ae086d3ae2ddc2a440e884cb5ac9379fcd4ea",
                                        "amd6409325d8d60fdebf88438840c5881e6f079657495495958e21abf9b8ebb4eefcc",
                                        "armhf9561320481bb96d0aed587d52baee5d78f07407fdab60e99b2b7bd4b03fe36f1")),

                        new PkgH("a/acl/libacl1_2.2.52-2",
                                Arrays.asList("i3861ed4d8acb3c432ff559b1ea88bafac7d60da3dd8c58ca40d41fc17c70761f72f",
                                        "arm64534dfd1cdf887a4b98be2882d93c72ad0cd439c60c50ae6d725c832d759e4deb",
                                        "amd64209a2c0c192d7debe0453a671f443dc73120c8c062ce66abe44afd06e711c801",
                                        "armhff16dc508fb664be6320514ac7c0ccedbdd67848eb6d284478719b5e842179e08")),

                        new PkgH("a/attr/libattr1_2.4.47-2",
                                Arrays.asList("i3867f0c5ecfacd122cd64a7ab05aa3f125f53303ebae468399efe1af7eb40ef7c72",
                                        "arm645a16a0b5477ba134a17f14101fa1f2da94513cdeb4c5fa3ce9613512433f9c15",
                                        "amd64ebe8b6a4c62b88db772f05c05b50d8b9f66f4d46488c547bda5042ae36865d88",
                                        "armhf34761c1df4f6f9ffe823cccb412ec11bd6b7906565ab2a9abad024d3efee1e19")),

                        new PkgH("libs/libselinux/libselinux1_2.4-3",
                                Arrays.asList("i386888f8030910dd202ef9c5cc653c0eaea5c5b32f41d236cd7c6e2222c4a06c218",
                                        "arm6412835d300ea8d3a2b14555868cac5233fd208c790a72d019d75ca13d3b029242",
                                        "amd644f7f8c11735ea4f2dafa1d5a74427882c362abd59519249a92926183e02d750e",
                                        "armhf0734589bfd94cc0eb9a6d46303949f07cff3ec8b550ee19e66ede12c841a790c")),

                        new PkgH("g/gcc-5/libgcc1_5.3.1-7",
                                Arrays.asList("i386c3b7b932764332f46c24060a5267202fb37bb0c87315a02279235105af3f22ab",
                                        "arm6470950cbffc605d19d831548a56ae9471b2828018afca034637e85b2b1fda7a24",
                                        "amd648dc7057ec22a07d540292c32578774f9342feb7527290e3e07bf1f9c9b8ad886",
                                        "armhfeb3fa21f966cf7ecf97be204abb60fddcda8da6018e07e29055e7dd0d8820bc6")),

                        new PkgH("g/gcc-5/libstdc++6_5.3.1-7",
                                Arrays.asList("i386f17a035a6fd740745b57e640703ec96888193657e697be21cc1899bcb2107c26",
                                        "arm648053f923405afba227e33a7c15faccbddd0c34c9fd3b98b12ec529da2c10140e",
                                        "amd64f9160d545444f79aabf50b84db27e764cb420075da3ea8ecd2f3d70475c91ede",
                                        "armhfd6fe8ed81734836e66099fde949d2ed6a0fc7051edb6aec0b20069985f96f23b")),

                        new PkgH("d/db5.3/libdb5.3++_5.3.28-11",
                                Arrays.asList("i38645ccb0cd1c892eee56ba33dfd80b69eb0479668456244ce779936edbfceaebb1",
                                        "arm64f9dbaba8b3d72b9ff31041d2c55502e4da59c50e726da52140ffe91fc81a3755",
                                        "amd6438021e5e4adaccaf38933358ff87ec8eb243d5bf516bef5cffdc56dd1f182d8e",
                                        "armhfaeb63f1b5c8611a3c4180e819987eb3b5ce9f95a64927d31c1740995d4ed81c8")),

                        new PkgH("g/glibc/libc6_2.21-7",
                                Arrays.asList("i386b0d72a5253e18e41f5294537a95f01cc64c1a8c4a47007a66c656dc00e62c0f1",
                                        "arm64db499513199b36d37947a17f908622c0a3b2a22df157bf0be759956d88daf3f0",
                                        "amd64df601e94884bcf57725db1a17ba85bed10ef4d59c180cc674a1d0c42b6712ac2",
                                        "armhf90a0ab83f55150fb3879a340b56946f777568c84d69b2afd3709437c21ee8ec0")),

                        new PkgH("b/bitcoin/bitcoind_0.11.2-1",
                                Arrays.asList("i386347449b94112686dc31d86e4dbc026a30d0352a60edf58365ae33e0117b90b0b",
                                        "arm64f027c349237acc1f485a9f52dddbdcf81b888f8ca496e64b3ea9e8b88405bea5",
                                        "amd6437b25a47c7e4490df58bd6fbe292e6f224fd69c2bc28f57af846b67ffaf12728",
                                        "armhf8ef112f4fca5ba20e7d701095193d00e6e07e5ccbb1c8d0f52c11cb3c9fe3b04"))
                )
        );

        ARCH_PACKAGES = new ArrayList<>(
                Arrays.asList(

                        /*arm64 and armhf only*/

                        new PkgH("%s/core/glibc-2.22-3",
                                Arrays.asList("arm642474b51260cd169835f248389a09b177c91ae9e037d91ddad0738ac6288f3e87",
                                        "armhfc205bf3e96c73e7ad2b287e2c0c0d6c229997e5011756511b718753da9efb253")),
                        new PkgH("%s/core/gcc-libs-5.3.0-3",
                                Arrays.asList("arm64ca25fc0fcbf4e7af0662d085a20a975248f86b6183cc5dacd3af29bc8b5ab047",
                                        "armhf4795140dfabe9a585aead71360cbcbc1cde2579a6ea6e743157afe9a76d0a2b9")),
                        new PkgH("%s/core/db-5.3.28-3",
                                Arrays.asList("arm64327fe79a326826d93fa370b5e10ff0fd0138beee6a0ecddd00df3b76bf50ec8b",
                                        "armhfd1b7ede577211e916dae5da681702744ac673a6756a194f404e031451356b528")),
                        new PkgH("%s/core/zlib-1.2.8-4",
                                Arrays.asList("arm6487e2e8bb8aaf38fda9dd4d185b0bef2669113098d13804dc43d2a87f8b78181c",
                                        "armhf2cffcc9a1a50b413759d4d5de8c53cb63ffb4df1a8f804e9e0c8325a413dd784")),
                        new PkgH("%s/core/openssl-1.0.2.f-1",
                                Arrays.asList("arm64c236caa623d94553aac5102a27b172c048acd071d806241df284f30b8a61f0a5",
                                        "armhf1a2d30cc20515349fdb345fca071f227f8d5a6b5d71df9736ef088a7663e7943")),

                        new PkgH("%s/extra/boost-libs-1.60.0-2",
                                Arrays.asList("arm64b0b2fe712893d94739d0b61179e144e61b3a1d4062509833c2764511f28d7452",
                                        "armhfd9ba5b0fadcbbc6984e3c211cb25c5d02563eaa5eb4ac13c287df8eb361c5469")),
                        new PkgH("%s/community/miniupnpc-1.9.20151026-1",
                                Arrays.asList("arm6418dc4485f357788247ad1b1db107f27e01ad4543fbb09e331a8d6fe99e5ef064",
                                        "armhfe675dbba4104fe0b3eef2c4bc87afd9bd60d547e01052ba7ce19997a282b0467")),
                        new PkgH("%s/community/bitcoin-daemon-0.11.2-3",
                                Arrays.asList("arm64aa1eb3107e3a3acf7869e90cc20aaa8a26e6f66234f1e747a85bbe7f8e245dda",
                                        "armhfefdebf1314538cde7e7df5c5a6c9ff49ab01341c70efd1da71c683ed80f9dc93")),

                        /*686 and  x86_64 only*/

                        new PkgH("core/os/%s/glibc-2.22-3",
                                Arrays.asList("amd64a16e087a9ab7b71a68f07e027f2929b78e5c251a88a57a4bcd1948961cf7a86d",
                                        "i386d3ecfd4ccbb47688b47fedb0c9e68d2092e03a30bc2ac1a1f950306da357288e")),
                        new PkgH("core/os/%s/gcc-libs-5.3.0-3",
                                Arrays.asList("amd64e89168e2c0dbed78e6292b4fd8dc6d25ae05e9a5aee9408edfe595767de06327",
                                        "i3869fc442b39218e6034ae919a3eeb22b67630fe0b5b2c68a257f5f3cced536bde3")),
                        new PkgH("core/os/%s/db-5.3.28-3",
                                Arrays.asList("amd64cf6c0b4599cf34e137c68a2b760a75c5110eb44fd707d8085673efaf604d9bee",
                                        "i38637376a77cbf4958738deabf7b207c015cc04670563ae11e55ad46773fdb37c56")),
                        new PkgH("core/os/%s/zlib-1.2.8-4",
                                Arrays.asList("amd6418b76944a0470685f012f56e397d63a2700365fb8e1e8204239517462c2f696e",
                                        "i386a4e7f7adf197493f0c47c272949baa2bbb3fe6e32db26722b293337162e023f4")),
                        new PkgH("core/os/%s/openssl-1.0.2.f-1",
                                Arrays.asList("amd6481b8f59754f283f222ddccd5cd1fb0afe5b9ef7d81f6b58e3bc8544f08c9f460",
                                        "i3862806ea56b446ed536d45d96974891e1cf29533bf6fc2efd3942cff884f18a180")),
                        new PkgH("extra/os/%s/boost-libs-1.60.0-2",
                                Arrays.asList("amd64baf54905cb70828ff1dc7ee43f5d3bac1d25de1e82e49e0758050cdc595482f1",
                                        "i386ea991bcd5d98928e794571aa708cb25943009a80ddbd9af6f49543299ce9a921")),
                        new PkgH("community/os/%s/miniupnpc-1.9.20151026-1",
                                Arrays.asList("amd64f306957369f4190a211f66f8023b50ef4dc3afb9351fe4bd3ab958482358252e",
                                        "i38655aaf73bc45be53cd6be0341e064c79a8c1b0b4856b1ae4c3bc89cc01b494817")),
                        new PkgH("community/os/%s/bitcoin-daemon-0.11.2-3",
                                Arrays.asList("amd64b74a28dffd945ed809bfdf2f8592507d8cc6058576b6ea02c02e93be0d02af55",
                                        "i386677fa9aa585514be1f052ba1cf263e367661fbf5a693d3efb013e944e28ba424"))
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
