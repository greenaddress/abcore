package com.greenaddress.abcore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Packages {

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

    public static List<PkgH> getDebPackages(final String arch) {
        return getPackages(arch, DEB_PACKAGES);
    }

    public static List<PkgH> getArchPackages(final String arch) {
        return getPackages(arch, ARCH_PACKAGES);
    }

    private final static List<PkgH> ARCH_PACKAGES;

    private final static List<PkgH> DEB_PACKAGES;

    static {

        // FIXME: some deps are not needed, ideally we just build what we need with a static binary, built with the NDK
        // This works for now

        // FIXME: add support for arch linux packages, they seem to be more up to date
        DEB_PACKAGES = new ArrayList<>(
                Arrays.asList(
                        /*armhf only*/
                        new PkgH("g/gcc-5/libstdc++6_5.3.1-4",
                                Arrays.asList("armhf5049a68ae71ad67bb26d27e0cb175c7b7c2528ea50501d0d2b78b157f5d927b2")),

                        new PkgH("g/gcc-5/libgcc1_5.3.1-4",
                                Arrays.asList("armhf5e978ba37fa6c99a3de89e7289a5e2dda70e237a70ac7b581fe84d89e35a4fd7")),

                        /*common*/
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

                        new PkgH("o/openssl/libssl1.0.2_1.0.2e-1",
                                Arrays.asList("i386a35cc64cb324f0f9e1b033d5f5fd3d17271a19b7e8c82bc8fd90178d33ac15e4",
                                        "arm6447024ecdcf098f1764954e58a4073cc803af8b04b7331a75347d6ea3f833a4a7",
                                        "amd64d789b51d135fbafa93a9b3d7169e6609a8181e5df48d24572615f95605a33d63",
                                        "armhf2742788be57cd6f7bfc40b6c35b7883841d21e9df8e29a539268357f04c74ba3")),

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

                        new PkgH("g/gcc-5/libgcc1_5.3.1-5",
                                Arrays.asList("i38688d6aa38dbf29e7faa866f8a610892337ec5214b79b591b320f2e27393d64444",
                                        "arm640917e1b45e869b181424048518bae4cbfbc1c6ecea2bdd94f7f3faf878b6ebe1",
                                        "amd6470fd211d027398cb48e540044c676f83eff4e26e9539770a2d810eb44e1a8b85")),

                        new PkgH("g/gcc-5/libstdc++6_5.3.1-5",
                                Arrays.asList("i38649b4c2797cf1651e6016755930c81fcdd83ccc73ec1466b2ee4952e8e0a9eb0b",
                                        "arm646e2e0cdd640414678a8f894a03bdea27e0f9f946e33e4ede2bf463103d583e67",
                                        "amd64137a9618c0b6aa7c1c4ab7c3483ef94d680cb6638b2fbea226c45d0ddb28cc27")),

                        new PkgH("d/db5.3/libdb5.3++_5.3.28-11",
                                Arrays.asList("i38645ccb0cd1c892eee56ba33dfd80b69eb0479668456244ce779936edbfceaebb1",
                                        "arm64f9dbaba8b3d72b9ff31041d2c55502e4da59c50e726da52140ffe91fc81a3755",
                                        "amd6438021e5e4adaccaf38933358ff87ec8eb243d5bf516bef5cffdc56dd1f182d8e",
                                        "armhfaeb63f1b5c8611a3c4180e819987eb3b5ce9f95a64927d31c1740995d4ed81c8")),

                        new PkgH("g/glibc/libc6_2.21-6",
                                Arrays.asList("i3865bfb2bbc583d53656e6ab3d2dddd11478265a3ee1b7b2f21c74e5fb3d5a8e08a",
                                        "arm64de30ec56f25b9c24baa022bdb15934b61df6a343630983d737bbfa7bc2351027",
                                        "amd6491677c5783579567dfef56811e7ddc474cf89bba2175cddbfd9aa3811a8609ee",
                                        "armhf9001e5e3fa75cf3d44b2dc3a61c20fbc47c2f411f166721441a9a45c688dbe64")),

                        new PkgH("b/bitcoin/bitcoind_0.11.1-1",
                                Arrays.asList("i3861b21cbee8967148f27abec948ee6e96f8b7df46fbb7a22ec70fe5b864abd6d2d",
                                        "arm640446220402835df4b62e933d680f319c4da7b83606296667d544c5855502fffe",
                                        "amd64dd898844b17661de3ced158796f0712e30cb21520ea9efd9ce992475a4a6d477",
                                        "armhf73559b8aba45fd8dc705e74dd1f05e2032f02e5b487f8549f3a73607b299fa7b"))
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
                        new PkgH("%s/core/openssl-1.0.2.e-1",
                                Arrays.asList("arm649cc4437b725e5cc0a207ca01da384432b1c4b63679e652c24c08e1648529e250",
                                        "armhf4928ef25750c50657b7d5e8741feaf629f705e76153d61d4b6737b0e3f298cae")),

                        new PkgH("%s/extra/boost-libs-1.60.0-1",
                                Arrays.asList("arm64c385fb5c282d1e2bb5b224b43922eca463c3d1b9d1ae872f219dc07fb464bcf8",
                                        "armhf4105a1e3c09f2a31bdf3c6361ab1ed1956c4764a8238c536196a2ffc1775c351")),
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
                                        "i3869fc442b39218e6034ae919a3eeb22b67630fe0b5b2c68a257f5f3cced536bde3" )),
                        new PkgH("core/os/%s/db-5.3.28-3",
                                Arrays.asList("amd64cf6c0b4599cf34e137c68a2b760a75c5110eb44fd707d8085673efaf604d9bee",
                                        "i38637376a77cbf4958738deabf7b207c015cc04670563ae11e55ad46773fdb37c56")),
                        new PkgH("core/os/%s/zlib-1.2.8-4",
                                Arrays.asList("amd6418b76944a0470685f012f56e397d63a2700365fb8e1e8204239517462c2f696e",
                                        "i386a4e7f7adf197493f0c47c272949baa2bbb3fe6e32db26722b293337162e023f4")),
                        new PkgH("core/os/%s/openssl-1.0.2.e-1",
                                Arrays.asList("amd64798dd2434dce2445aeab942c3c8c78ebd63c52cde98694d5235b1248269e6b38",
                                        "i38641f86f7d4dbc4c2afb179fdd4c5f5a5535e880d4b0ae0fe1a1398932d69639db")),
                        new PkgH("extra/os/%s/boost-libs-1.60.0-1",
                                Arrays.asList("amd64e0adc16ffdfd94289d4e8a4936961939b66d9c2a15ee85f78dfa0fe9870286f6",
                                        "i38600ae492625adff70e108ad359739556ef818584c63afa0a75d96f04dcf33315e")),
                        new PkgH("community/os/%s/miniupnpc-1.9.20151026-1",
                                Arrays.asList("amd64f306957369f4190a211f66f8023b50ef4dc3afb9351fe4bd3ab958482358252e",
                                        "i38655aaf73bc45be53cd6be0341e064c79a8c1b0b4856b1ae4c3bc89cc01b494817")),
                        new PkgH("community/os/%s/bitcoin-daemon-0.11.2-3",
                                Arrays.asList("amd64b74a28dffd945ed809bfdf2f8592507d8cc6058576b6ea02c02e93be0d02af55",
                                        "i386677fa9aa585514be1f052ba1cf263e367661fbf5a693d3efb013e944e28ba424"))
                        ));
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