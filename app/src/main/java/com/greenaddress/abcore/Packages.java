package com.greenaddress.abcore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Packages {

    public final static List<PkgH> ARCH_PACKAGES;
    public final static PkgH CORE_PACKAGE;
    public final static String GLIBC_MAJOR = "2.23";
    public final static String CORE_V = "0.13.0";
    private final static String GLIBC_MINOR = "5";
    private final static String CORE_URL = "https://bitcoin.org/bin/bitcoin-core-%s/%s";
    private final static String CORE_MINOR = "rc2";
    private final static String CORE_V_FULL = String.format("%s%s", CORE_V, CORE_MINOR);


    static {

        // FIXME: some deps are not needed, ideally we just build what we need with a static binary, built with the NDK
        // This works for now

        CORE_PACKAGE = new PkgH(String.format("test.%s/bitcoin-%s", CORE_MINOR, CORE_V_FULL),
                Arrays.asList(
                        "armhf2ee49d791e4a9fb4dea7873c2e2a6273aafd3fc1ff72bcf7e1a875266d2107d1",
                        "arm644a6159548241fff91be527cb070f64f7ba15a57dff63bf5fe704f15245e76ca6",
                        "amd642110d28093a922144efcad651e140c30c89bc113b72a43f8fe0e6c365796efb7",
                        "i386dd93395710af8ff02af7f52aa8cd02484d51cfe6e19b6b6b65930051c70e7707"
                ));

        ARCH_PACKAGES = new ArrayList<>(
                Arrays.asList(
                        new PkgH(String.format("glibc/glibc-%s-%s", GLIBC_MAJOR, GLIBC_MINOR),
                                Arrays.asList(
                                        "armhfa8ed199903643d266058b07296d7cdf6680549d68c74a39caaf04cc87c841234",
                                        "arm64ab1ff1d603f3fc3a5e2cb233884d31ed13bb600915d8ed3d3ccb01f7666adfd0",
                                        "amd64b2b6ca0473d2b9f664a2d39217bd42e918bfb70ab66acf1eaebbf6566125ef0c",
                                        "i386d53b4e46e2fd7a6dc39efca162b90459c753d26df2de4e3c828cb2fd8ab80053"
                                )),
                        new PkgH("gcc-libs/gcc-libs-6.1.1-3",
                                Arrays.asList(
                                        "armhfc86efbc2179b6594072a062ba75aef47f562c78d3077a40fdf6ed090c3db0906",
                                        "arm6489f9b7c16ee022e55b0bb70260185d131400c4529672d9587aadae49af800790",
                                        "amd64ea1894cfff8677f07a070bd4a9defd85c907b694ee1a83ad5f4dd057da73831b",
                                        "i38609f855855b560f143538da5a74fd22a5dc38d36af7b9307282240f9d11818cb0"
                                ))
                ));
    }

    private static String getRepo(final Context c, final String arch) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        if (arch.equals("amd64") || arch.equals("i386"))
            return prefs.getString("archi386Repo", "archive.archlinux.org/packages");
        else
            return prefs.getString("archarmRepo", "tardis.tiny-vps.com/aarm/packages");
    }

    static String getPackageUrl(final Packages.PkgH pkg, final Context c, final String arch) {
        final boolean isArmArchitecture = !arch.equals("amd64") && !arch.equals("i386");
        final String osArch = Utils.getArchLinuxArchitecture(arch);
        final String fileArch = arch.equals("armhf") ? "armv7h" : osArch;
        final String template = "http://%s/%s/%s-" + (isArmArchitecture ? fileArch : osArch) + ".pkg.tar.xz";
        final String repo = getRepo(c, arch);
        return String.format(template, repo, pkg.pkg.charAt(0), pkg.pkg);
    }

    static String getCorePackageUrl(final String arch) {
        final String packageName = arch == null ? Utils.getCorePkgsName(): Utils.getCorePkgsArch(arch);
        final String path = String.format("%s-%s.tar.gz", CORE_PACKAGE.pkg, packageName);
        return String.format(Packages.CORE_URL, CORE_V, path);
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