package com.greenaddress.abcore;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class DownloadInstallCoreIntentService extends IntentService {

    public static final String PARAM_OUT_MSG = "abtcore";
    private static final String TAG = DownloadInstallCoreIntentService.class.getName();
    public static boolean HAS_BEEN_STARTED = false;

    public DownloadInstallCoreIntentService() {
        super(DownloadInstallCoreIntentService.class.getName());
    }

    public static void configureCore(final Context c) throws IOException {

        final File coreConf = new File(Utils.getBitcoinConf(c));
        if (coreConf.exists())
            return;
        //noinspection ResultOfMethodCallIgnored
        coreConf.getParentFile().mkdirs();

        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(coreConf);
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
            outputStream.write("validatepegin=0\n".getBytes());
            outputStream.write("listenonion=1\n".getBytes());
            outputStream.write("blocksonly=1\n".getBytes());
            for (final File f : c.getExternalFilesDirs(null))
                outputStream.write(String.format("# for external storage try: %s\n", f.getCanonicalPath()).getBytes());

            // Afaik ipv6 is broken on android, disable by default, user can change this
            // outputStream.write("onlynet=ipv6\n".getBytes());
            outputStream.write(String.format("datadir=%s\n", String.format("%s/.bitcoin", Utils.getDir(c).getAbsolutePath())).getBytes());

            IOUtils.closeQuietly(outputStream);
        } catch (final IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static void markAsDone(final String sha, final File outputDir) throws IOException {
        final File shadir = new File(outputDir, "shachecks");
        if (!shadir.exists())
            //noinspection ResultOfMethodCallIgnored
            shadir.mkdir();
        if (!new File(shadir, sha).createNewFile())
            throw new IOException();
    }

    private static boolean isUnpacked(final String sha, final File outputDir) {
        final File shadir = new File(outputDir, "shachecks");
        return new File(shadir, sha).exists();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        HAS_BEEN_STARTED = true;
        // this already runs in its own thread but no reasons the pkgs couldn't be handle concurrently.
        final File dir = Utils.getDir(DownloadInstallCoreIntentService.this);
        Log.d(TAG, dir.getAbsolutePath());
        final String arch = Utils.getArch();
        Log.d(TAG, arch);

        try {

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            final String useDistribution = prefs.getString("usedistribution", "core");
            final List<String> distro = "knots".equals(useDistribution) ? Packages.NATIVE_KNOTS : "liquid".equals(useDistribution) ? Packages.NATIVE_LIQUID : Packages.NATIVE_CORE;

            final String url = Packages.getPackageUrl(useDistribution, arch);
            final String filePath = Utils.getFilePathFromUrl(this, url);
            String rawSha = null;
            int bs = 0;
            for (final String a : distro) {
                final String hash = a.substring(7);
                bs = Integer.parseInt(a.substring(0, 7));
                if (hash.startsWith(arch)) {
                    rawSha = hash;
                    break;
                }
            }
            if (isUnpacked(rawSha, dir))
                return;

            final int byteSize = bs;
            final Utils.OnDownloadUpdate odsc = new Utils.OnDownloadUpdate() {
                @Override
                public void update(final int bytesPerSecond, final int bytesDownloaded) {
                    sendUpdate("Downloading", bytesPerSecond, bytesDownloaded, byteSize, useDistribution);
                }
            };

            if (!new File(filePath).exists() || Utils.isSha256Different(arch, rawSha, filePath) != null) {

                sendUpdate("Downloading", useDistribution);
                Utils.downloadFile(url, filePath, odsc);

                // Verify sha256sum
                sendUpdate("Verifying", useDistribution);
                Utils.validateSha256sum(arch, rawSha, filePath);
            }

            sendUpdate("Uncompressing", useDistribution);

            Utils.extractTarXz(new File(filePath), dir);

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

    private void sendUpdate(final String upd, final String fileExtracted) {
        sendUpdate(upd, null, null, null, fileExtracted);
    }

    private void sendUpdate(final String upd, final Integer bytesPerSec, final Integer bytesDownloaded, final Integer bytesSize, final String fileExtracted) {
        final Intent broadcastIntent = new Intent();

        broadcastIntent.setAction(DownloadActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, "ABCOREUPDATE");

        broadcastIntent.putExtra("ABCOREUPDATE", bytesDownloaded);
        broadcastIntent.putExtra("ABCOREUPDATEMAX", bytesSize);
        if (bytesPerSec != null)
            broadcastIntent.putExtra("ABCOREUPDATESPEED", bytesPerSec);


        broadcastIntent.putExtra("ABCOREUPDATETXT", String.format("%s %s %s", upd, fileExtracted, "knots".equals(fileExtracted) ? Packages.BITCOIN_KNOTS_NDK : "liquid".equals(fileExtracted) ? Packages.BITCOIN_LIQUID_NDK : Packages.BITCOIN_NDK));


        sendBroadcast(broadcastIntent);
    }
}
