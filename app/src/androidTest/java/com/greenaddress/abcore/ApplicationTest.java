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

    private void downloadAndValidatePackages(final List<Packages.PkgH> pkgs, final String arch, final boolean isArchLinux) throws IOException, NoSuchAlgorithmException {
        for (final Packages.PkgH pkg : pkgs) {

            final String url = Packages.getPackageUrl(pkg, getContext(), arch, isArchLinux);

            final String filePath = Utils.getFilePathFromUrl(getContext(), url);

            Utils.downloadFile(url, filePath);

            for (final String a : pkg.archHash) {
                if (a.startsWith(arch)) {
                    Utils.validateSha256sum(arch, a, filePath);
                    break;
                }
            }
            //noinspection ResultOfMethodCallIgnored
            new File(filePath).delete();
        }
    }

    private void downloadAndValidateDebianPackages(final String arch) throws IOException, NoSuchAlgorithmException {
        downloadAndValidatePackages(Packages.getDebPackages(arch), arch, false);
    }

    private void downloadAndValidateArchPackages(final String arch) throws IOException, NoSuchAlgorithmException {
        downloadAndValidatePackages(Packages.getArchPackages(arch), arch, true);
    }

    public void testDebianArm64Packages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidateDebianPackages("arm64");
    }

    public void testDebianAmd64Packages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidateDebianPackages("amd64");
    }

    public void testDebiani386Packages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidateDebianPackages("i386");
    }

    public void testDebianArmHfPackages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidateDebianPackages("armhf");
    }

    public void testArchArm64Packages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidateArchPackages("arm64");
    }

    public void testArchAmd64Packages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidateArchPackages("amd64");
    }

    public void testArchi386Packages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidateArchPackages("i386");
    }

    public void testArchArmHfPackages() throws IOException, NoSuchAlgorithmException {
        downloadAndValidateArchPackages("armhf");
    }
}