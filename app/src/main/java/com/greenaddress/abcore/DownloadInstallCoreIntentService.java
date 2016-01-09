package com.greenaddress.abcore;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class DownloadInstallCoreIntentService extends IntentService {

    private static final String TAG = DownloadInstallCoreIntentService.class.getName();

    public static final String PARAM_OUT_MSG = "abtcore";

    public DownloadInstallCoreIntentService() {
        super(DownloadInstallCoreIntentService.class.getName());
    }

    private List<Packages.PkgH> getPackages() {
        final String arch = Utils.getArch();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Boolean archEnabled = prefs.getBoolean("archisenabled", false);
        return archEnabled ? Packages.getArchPackages(arch): Packages.getDebPackages(arch);
    }


    private String getRepo() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Boolean archEnabled = prefs.getBoolean("archisenabled", false);
        if (archEnabled) {
            if (Utils.getArch().equals("amd64") || Utils.getArch().equals("i386")) {
                return prefs.getString("archi386Repo", "archlinux.openlabto.org/archlinux");
            } else {
                return prefs.getString("archarmRepo", "eu.mirror.archlinuxarm.org");
            }
        } else {
            return prefs.getString("debianRepo", "ftp.us.debian.org/debian");
        }
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        // this already runs in its own thread but no reasons the pkgs couldn't be handle concurrently.
        final File dir = Utils.getDir(DownloadInstallCoreIntentService.this);
        final String arch = Utils.getArch();
        final String repo = getRepo();

        final List<Packages.PkgH> pkgs = getPackages();

        try {

            for (final Packages.PkgH d : pkgs) {
                for (final String a : d.archHash) {
                    try {
                        if (a.startsWith(arch)) {
                            unpack(repo, d, arch, dir, a);
                            break;
                        }
                    } catch (final FileNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                        Log.e(TAG, "NOT FOUND " + String.format(d.pkg, arch));
                        throw e;
                    }
                }
            }

            // bitcoin core & deps installed, configure it now
            configureCore(this);

            // notify

            // processing done here….
            final Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, "OK");
            sendBroadcast(broadcastIntent);


        } catch (final ValidationFailure | ArchiveException | NoSuchAlgorithmException | IOException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, "exception");
            broadcastIntent.putExtra("exception", e.getMessage());

            sendBroadcast(broadcastIntent);
        }

        Log.v(TAG, "onHandleIntent END");
    }

    private static String getRnd() {

        final Random ranGen = new SecureRandom();
        final byte[] pass = new byte[16];
        ranGen.nextBytes(pass);
        return Utils.toBase58(pass);
    }

    static class ValidationFailure extends RuntimeException {
        final String pkg;
        ValidationFailure(final String s, final String a) {
            super(s);
            this.pkg = a;
        }
    }

    public static void configureCore(final Context c) throws IOException {

        final File coreConf = new File(Utils.getBitcoinConf(c));
        if (coreConf.exists()) {
            return;
        }
        coreConf.getParentFile().mkdirs();

        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(coreConf);
            outputStream.write("rpcuser=bitcoinrpc\n".getBytes());
            outputStream.write(String.format("rpcpassword=%s\n", getRnd()).getBytes());
            outputStream.write("listen=1\n".getBytes());

            //outputStream.write("bind=127.0.0.1\n".getBytes());
            outputStream.write("rpcbind=127.0.0.1\n".getBytes());
            outputStream.write("disablewallet=1\n".getBytes());
            outputStream.write("testnet=0\n".getBytes());
            //outputStream.write("testnet=1\n".getBytes());
            //outputStream.write("addnode=192.168.2.47\n".getBytes());
            //outputStream.write("regtest=1\n".getBytes());
            outputStream.write("upnp=0\n".getBytes());
            // don't attempt onion connections by default
            outputStream.write("onlynet=ipv4\n".getBytes());

            // Afaik ipv6 is broken on android, disable by default, user can change this
            // outputStream.write("onlynet=ipv6\n".getBytes());
            outputStream.write(String.format("datadir=%s\n", Utils.getLargetFilesDir(c)).getBytes());

            IOUtils.closeQuietly(outputStream);
        } catch (final IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void sendUpdate(final String upd, final Packages.PkgH pkg) {
        Log.i(TAG, upd);
        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, "ABCOREUPDATE");
        broadcastIntent.putExtra("ABCOREUPDATE", Integer.valueOf(getPackages().indexOf(pkg)));
        broadcastIntent.putExtra("ABCOREUPDATEMAX", getPackages().size() * 3);
        broadcastIntent.putExtra("ABCOREUPDATETXT", String.format("%s %s", upd, pkg.pkg.substring(pkg.pkg.lastIndexOf("/") + 1)));
        sendBroadcast(broadcastIntent);
    }

    private void unpack(final String repo, final Packages.PkgH pkg, final String arch, final File outputDir, final String sha256raw) throws IOException, NoSuchAlgorithmException, ArchiveException {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Boolean archEnabled = prefs.getBoolean("archisenabled", false);
        final String osArch = System.getProperty("os.arch");
        final boolean archarm = !Utils.getArch().equals("amd64") && !Utils.getArch().equals("i386");

        final String template = archEnabled ?
                archarm? "http://%s/%s-"+(
                        Utils.getArch().contains("armhf")?"armv7h":osArch)+".pkg.tar.xz":"http://%s/%s-" + osArch + ".pkg.tar.xz" : "http://%s/pool/main/%s_%s.deb";

        final String url = archEnabled ? String.format(template, repo, String.format(pkg.pkg, Utils.getArch().contains("armhf")?"armv7h":osArch)): String.format(template, repo, pkg.pkg, arch);

        final String fileName = url.substring(url.lastIndexOf("/") + 1);
        final String filePath = outputDir.getAbsoluteFile() + "/" + fileName;

        // Download file
        sendUpdate("Downloading", pkg);
        Utils.downloadFile(url, filePath);


        // Verify sha256sum
        sendUpdate("Verifying", pkg);

        final String hash = Utils.sha256Hex(new BufferedInputStream(new FileInputStream(filePath)));
        final String sha256hash = sha256raw.substring(sha256raw.indexOf(arch) + arch.length());

        if (!sha256hash.equals(hash)) {
            Log.e(TAG, "Doesn't match " + url + " " + hash);
            throw new ValidationFailure("Doesn't match " + url + " " + hash, fileName);
        } else {
            Log.i(TAG, "Matched " + url);
        }

        // extract from deb/ar file the data.tar.xz, then uncompress via xz and untar
        sendUpdate("Unpacking", pkg);
        if (archEnabled) {
            Utils.extractTarXz(new File(filePath), outputDir);
        } else {
            Utils.extractDataFromDeb(filePath, outputDir);
        }
    }
}
