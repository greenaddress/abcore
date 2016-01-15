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

            final String url = Utils.getPackageUrl(pkg, getContext(), arch, isArchLinux);

            final String filePath = Utils.getFilePathFromUrl(getContext(), url);

            Utils.downloadFile(url, filePath);

            for (final String a: pkg.archHash) {
                if (a.startsWith(arch)) {
                    Utils.validateSha256sum(arch, a, filePath);
                    break;
                }
            }
            //noinspection ResultOfMethodCallIgnored
            new File(filePath).delete();
        }
    }

    private void downloadAndValidatePackages(final String arch) throws IOException, NoSuchAlgorithmException {
        downloadAndValidatePackages(Packages.getDebPackages(arch), arch, false);
        downloadAndValidatePackages(Packages.getArchPackages(arch), arch, true);
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