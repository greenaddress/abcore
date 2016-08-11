package com.greenaddress.abcore;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
            //outputStream.write("regtest=1\n".getBytes());
            outputStream.write("upnp=0\n".getBytes());
            // don't attempt onion connections by default
            outputStream.write("onlynet=ipv4\n".getBytes());

            // Afaik ipv6 is broken on android, disable by default, user can change this
            // outputStream.write("onlynet=ipv6\n".getBytes());
            outputStream.write(String.format("datadir=%s\n", Utils.getLargestFilesDir(c)).getBytes());

            IOUtils.closeQuietly(outputStream);
        } catch (final IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        // this already runs in its own thread but no reasons the pkgs couldn't be handle concurrently.
        final File dir = Utils.getDir(DownloadInstallCoreIntentService.this);
        final String arch = Utils.getArch();

        final List<Packages.PkgH> pkgs = Packages.ARCH_PACKAGES;

        try {
            for (final Packages.PkgH d : pkgs)
                for (final String a : d.archHash)
                    try {
                        if (a.startsWith(arch)) {
                            unpack(d, arch, dir, a, new Utils.OnDownloadSpeedChange() {
                                @Override
                                public void bytesPerSecondUpdate(final int bytes) {
                                    sendUpdate("Downloading", d, bytes);
                                }
                            });
                            break;
                        }
                    } catch (final FileNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                        Log.e(TAG, "NOT FOUND " + String.format(d.pkg, arch));
                        throw e;
                    }

            // Download bitcoin core

            final Utils.OnDownloadSpeedChange odsc = new Utils.OnDownloadSpeedChange() {
                @Override
                public void bytesPerSecondUpdate(final int bytes) {
                    sendUpdate("Downloading", null, bytes);
                }
            };

            final String url = String.format(Packages.CORE_PACKAGE.pkg, Utils.getCorePkgsName());
            final String filePath = Utils.getFilePathFromUrl(this, url);
            sendUpdate("Downloading", null);
            Utils.downloadFile(url, filePath, odsc);

            // Verify sha256sum
            sendUpdate("Verifying", null);
            for (final String hash : Packages.CORE_PACKAGE.archHash)
                if (hash.startsWith(arch)) {
                    Utils.validateSha256sum(arch, hash, filePath);
                    break;
                }

            // extract from deb/ar file the data.tar.xz, then uncompress via xz and untar
            sendUpdate("Unpacking", null);

            Utils.extractTarXz(new File(filePath), dir, false);

            // bitcoin core & deps installed, configure it now
            configureCore(this);

            // notify

            // processing done here….
            final Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, "OK");
            sendBroadcast(broadcastIntent);


        } catch (final Utils.ValidationFailure | NoSuchAlgorithmException | IOException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
            final Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, "exception");
            broadcastIntent.putExtra("exception", e.getMessage());

            sendBroadcast(broadcastIntent);
        }

        Log.v(TAG, "onHandleIntent END");
    }

    private void sendUpdate(final String upd, final Packages.PkgH pkg) {
        sendUpdate(upd, pkg, null);
    }

    private void sendUpdate(final String upd, final Packages.PkgH pkg, final Integer bytesPerSec) {
        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, "ABCOREUPDATE");
        if (pkg != null)
            broadcastIntent.putExtra("ABCOREUPDATE", Packages.ARCH_PACKAGES.indexOf(pkg) + 1);
        else
            broadcastIntent.putExtra("ABCOREUPDATE", Packages.ARCH_PACKAGES.size() + 1);
        broadcastIntent.putExtra("ABCOREUPDATEMAX", Packages.ARCH_PACKAGES.size() + 1);
        if (bytesPerSec != null)
            broadcastIntent.putExtra("ABCOREUPDATESPEED", bytesPerSec);

        if (pkg != null)
            broadcastIntent.putExtra("ABCOREUPDATETXT", String.format("%s %s", upd, pkg.pkg.substring(pkg.pkg.lastIndexOf("/") + 1)));
        else
            broadcastIntent.putExtra("ABCOREUPDATETXT", String.format("%s bitcoin-core-%src2", upd, Packages.CORE_V));

        sendBroadcast(broadcastIntent);
    }

    private void unpack(final Packages.PkgH pkg, final String arch, final File outputDir, final String sha256raw, final Utils.OnDownloadSpeedChange odsc) throws IOException, NoSuchAlgorithmException {

        final String url = Packages.getPackageUrl(pkg, this, arch);
        final String filePath = Utils.getFilePathFromUrl(this, url);

        // Download file
        sendUpdate("Downloading", pkg);
        Utils.downloadFile(url, filePath, odsc);

        // Verify sha256sum
        sendUpdate("Verifying", pkg);
        Utils.validateSha256sum(arch, sha256raw, filePath);

        // extract from deb/ar file the data.tar.xz, then uncompress via xz and untar
        sendUpdate("Unpacking", pkg);

        Utils.extractTarXz(new File(filePath), outputDir, true);

    }
}
