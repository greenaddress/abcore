package com.greenaddress.abcore;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {


    public ApplicationTest() {
        super(Application.class);
    }

    private void downloadPackage(final Packages.PkgH pkg, final String arch, final String url) throws IOException, NoSuchAlgorithmException {

        final String filePath = Utils.getFilePathFromUrl(getContext(), url);

        Utils.downloadFile(url, filePath);

        for (final String a : pkg.archHash)
            if (a.startsWith(arch)) {
                Utils.validateSha256sum(arch, a, filePath);
                break;
            }

        //noinspection ResultOfMethodCallIgnored
        new File(filePath).delete();
    }

    private void downloadAndValidatePackages(final String arch) throws IOException, NoSuchAlgorithmException {
        for (final Packages.PkgH pkg : Packages.ARCH_PACKAGES)
            downloadPackage(pkg, arch, Packages.getPackageUrl(pkg, getContext(), arch));
        downloadCorePackage(arch);
    }

    private void downloadCorePackage(final String arch) throws IOException, NoSuchAlgorithmException {
        final String url = String.format(Packages.CORE_PACKAGE.pkg, getCorePkgsName(arch));
        downloadPackage(Packages.CORE_PACKAGE, arch, url);
    }

    private static String getCorePkgsName(final String arch) {
        if (arch.endsWith("i386"))
            return "i686-pc-linux-gnu";
        else if (arch.startsWith("armhf"))
            return "arm-linux-gnueabihf";
        else if (arch.endsWith("amd64"))
            return "x86_64-linux-gnu";
        else if ("arm64".equals(arch))
            return "aarch64-linux-gnu";

        return null;
    }

    public void testArm64Packages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidatePackages("arm64");
    }

    public void testAmd64Packages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidatePackages("amd64");
    }

    public void testi386Packages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidatePackages("i386");
    }

    public void testArmHfPackages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidatePackages("armhf");
    }
}
