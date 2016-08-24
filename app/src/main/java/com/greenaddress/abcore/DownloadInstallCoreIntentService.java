package com.greenaddress.abcore;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class DownloadInstallCoreIntentService extends IntentService {

    public static final String PARAM_OUT_MSG = "abtcore";
    private static final String TAG = DownloadInstallCoreIntentService.class.getName();
    public static boolean HAS_BEEN_STARTED = false;

    public DownloadInstallCoreIntentService() {
        super(DownloadInstallCoreIntentService.class.getName());
    }

    private static String getRnd() {

        final Random ranGen = new SecureRandom();
        final byte[] pass = new byte[16];
        ranGen.nextBytes(pass);
        return Utils.toBase58(pass);
    }

    public static void configureCore(final Context c) throws IOException {

        final File coreConf = new File(Utils.getBitcoinConf(c));
        if (coreConf.exists())
            return;
        coreConf.getParentFile().mkdirs();

        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(coreConf);
            outputStream.write("rpcuser=bitcoinrpc\n".getBytes());
            outputStream.write(String.format("rpcpassword=%s\n", getRnd()).getBytes());
            outputStream.write("listen=1\n".getBytes());

            //outputStream.write("bind=127.0.0.1\n".getBytes());
            outputStream.write("disablewallet=0\n".getBytes());
            outputStream.write("testnet=0\n".getBytes());
            //outputStream.write("testnet=1\n".getBytes());
            //outputStream.write("addnode=192.168.2.47\n".getBytes());
            outputStream.write("prune=1000\n".getBytes());
            //outputStream.write("regtest=1\n".getBytes());
            outputStream.write("upnp=0\n".getBytes());
            // don't attempt onion connections by default
            outputStream.write("onlynet=ipv4\n".getBytes());
            outputStream.write("blocksonly=1\n".getBytes());

            // Afaik ipv6 is broken on android, disable by default, user can change this
            // outputStream.write("onlynet=ipv6\n".getBytes());
            outputStream.write(String.format("datadir=%s\n", String.format("%s/.bitcoin", Utils.getDir(c).getAbsolutePath())).getBytes());

            IOUtils.closeQuietly(outputStream);
        } catch (final IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        HAS_BEEN_STARTED = true;
        // this already runs in its own thread but no reasons the pkgs couldn't be handle concurrently.
        final File dir = Utils.getDir(DownloadInstallCoreIntentService.this);
        Log.d(TAG, dir.getAbsolutePath());
        final String arch = Utils.getArch();
        Log.d(TAG, arch);

        final List<Packages.PkgH> pkgs = Packages.ARCH_PACKAGES;

        try {
            for (final Packages.PkgH d : pkgs)
                for (final String a : d.archHash)
                    try {
                        if (a.startsWith(arch)) {
                            unpack(d, arch, dir, a);
                            break;
                        }
                    } catch (final FileNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                        Log.e(TAG, "NOT FOUND " + String.format(d.pkg, arch));
                        throw e;
                    }

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            final boolean useknots = prefs.getBoolean("useknots", false);
            final Packages.PkgH pkg = useknots ? Packages.KNOTS_CORE_PACKAGE : Packages.CORE_PACKAGE;

            final Utils.OnDownloadSpeedChange odsc = new Utils.OnDownloadSpeedChange() {
                @Override
                public void bytesPerSecondUpdate(final int bytes) {
                    sendUpdate("Downloading", pkg, bytes, null);
                }
            };

            final String url = Packages.getCorePackageUrl(pkg, arch);
            final String filePath = Utils.getFilePathFromUrl(this, url);
            String rawSha = null;
            for (final String hash : pkg.archHash)
                if (hash.startsWith(arch)) {
                    rawSha = hash;
                    break;
                }
            if (isUnpacked(rawSha, dir))
                return;
            if (!new File(filePath).exists() || Utils.isSha256Different(arch, rawSha, filePath)) {

                sendUpdate("Downloading", pkg);
                Utils.downloadFile(url, filePath, odsc);

                // Verify sha256sum
                sendUpdate("Verifying", pkg);
                Utils.validateSha256sum(arch, rawSha, filePath);
            }

            // extract from deb/ar file the data.tar.xz, then uncompress via xz and untar
            sendUpdate("Uncompressing", pkg);

            Utils.extractTarXz(new File(filePath), dir, false, new Utils.OnFileNewFileUnpacked() {
                @Override
                public void fileUnpackedUpdate(final String file) {
                    sendUpdate("Unpacking", pkg, null, file);
                }
            });

            // bitcoin core & deps installed, configure it now
            configureCore(this);

            // notify

            // processing done here….
            final Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(DownloadActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, "OK");
            sendBroadcast(broadcastIntent);
            HAS_BEEN_STARTED = false;
            markAsDone(rawSha, dir);

        } catch (final Utils.ValidationFailure | NoSuchAlgorithmException | IOException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
            final Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(DownloadActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, "exception");
            broadcastIntent.putExtra("exception", e.getMessage());

            sendBroadcast(broadcastIntent);
        }
        Log.v(TAG, "onHandleIntent END");
    }

    private void sendUpdate(final String upd, final Packages.PkgH pkg) {
        sendUpdate(upd, pkg, null, null);
    }

    private void sendUpdate(final String upd, final Packages.PkgH pkg, final Integer bytesPerSec, final String fileExtracted) {
        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DownloadActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, "ABCOREUPDATE");
        if (Packages.ARCH_PACKAGES.contains(pkg))
            broadcastIntent.putExtra("ABCOREUPDATE", Packages.ARCH_PACKAGES.indexOf(pkg) + 1);
        else
            broadcastIntent.putExtra("ABCOREUPDATE", Packages.ARCH_PACKAGES.size() + 1);
        broadcastIntent.putExtra("ABCOREUPDATEMAX", Packages.ARCH_PACKAGES.size() + 1);
        if (bytesPerSec != null)
            broadcastIntent.putExtra("ABCOREUPDATESPEED", bytesPerSec);
        if (fileExtracted != null)
            broadcastIntent.putExtra("ABCOREUPDATETXT", String.format("%s %s %s", upd, pkg.pkg.substring(pkg.pkg.lastIndexOf("/") + 1), fileExtracted));
        else
            broadcastIntent.putExtra("ABCOREUPDATETXT", String.format("%s %s", upd, pkg.pkg.substring(pkg.pkg.lastIndexOf("/") + 1)));

        sendBroadcast(broadcastIntent);
    }

    private static void markAsDone(final String sha, final File outputDir) throws IOException {
        final File shadir = new File(outputDir, "shachecks");
        if (!shadir.exists())
            shadir.mkdir();
        if (!new File(shadir, sha).createNewFile())
            throw new IOException();
    }

    private static boolean isUnpacked(final String sha, final File outputDir) {
        final File shadir = new File(outputDir, "shachecks");
        return new File(shadir, sha).exists();
    }

    private void unpack(final Packages.PkgH pkg, final String arch, final File outputDir, final String sha256raw) throws IOException, NoSuchAlgorithmException {
        if (isUnpacked(sha256raw, outputDir))
            return;

        final String url = Packages.getPackageUrl(pkg, arch);
        final String filePath = Utils.getFilePathFromUrl(this, url);


        if (!new File(filePath).exists() || Utils.isSha256Different(arch, sha256raw, filePath)) {

            // Download file
            sendUpdate("Downloading", pkg);
            Utils.downloadFile(url, filePath, new Utils.OnDownloadSpeedChange() {
                @Override
                public void bytesPerSecondUpdate(final int bytes) {
                    sendUpdate("Downloading", pkg, bytes, null);
                }
            });

            // Verify sha256sum
            sendUpdate("Verifying", pkg);
            Utils.validateSha256sum(arch, sha256raw, filePath);
        }

        // extract from deb/ar file the data.tar.xz, then uncompress via xz and untar
        sendUpdate("Uncompressing", pkg);

        Utils.extractTarXz(new File(filePath), outputDir, true, new Utils.OnFileNewFileUnpacked() {

            @Override
            public void fileUnpackedUpdate(final String file) {
                sendUpdate("Unpacking", pkg, null, file);
            }
        });
        markAsDone(sha256raw, outputDir);
    }
}
