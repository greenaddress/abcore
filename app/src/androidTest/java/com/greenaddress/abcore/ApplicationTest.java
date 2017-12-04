package com.greenaddress.abcore;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {


    public ApplicationTest() {
        super(Application.class);
    }

    private void downloadPackage(final String distro, final String arch) throws IOException, NoSuchAlgorithmException {
        final String url = Packages.getPackageUrl(distro, arch);
        final String filePath = Utils.getFilePathFromUrl(getContext(), url);
        Utils.downloadFile(url, filePath);
        final List<String> hashes = distro.equals("knots") ? Packages.NATIVE_KNOTS : Packages.NATIVE_CORE;

        for (final String a : hashes) {
            final String h = a.substring(7, a.length());
            if (h.startsWith(arch)) {
                Utils.validateSha256sum(arch, h, filePath);
                break;
            }
        }

        //noinspection ResultOfMethodCallIgnored
        new File(filePath).delete();
    }

    private void downloadCorePackage(final String arch) throws IOException, NoSuchAlgorithmException {
        downloadPackage("core", arch);
        downloadPackage("knots", arch);
    }

    public void testArm64Packages() throws IOException, NoSuchAlgorithmException {
        downloadCorePackage("aarch64-linux-android");
    }

    public void testAmd64Packages() throws IOException, NoSuchAlgorithmException {
        downloadCorePackage("x86_64-linux-android");
    }

    public void testi386Packages() throws IOException, NoSuchAlgorithmException {
        downloadCorePackage("i686-linux-android");
    }

    public void testArmHfPackages() throws IOException, NoSuchAlgorithmException {
        downloadCorePackage("arm-linux-androideabi");
    }
}
