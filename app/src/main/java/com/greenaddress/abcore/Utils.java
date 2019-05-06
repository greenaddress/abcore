package com.greenaddress.abcore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

class Utils {

    private final static String TAG = Utils.class.getSimpleName();

    static void extractTarXz(final File input, final File outputDir) throws IOException {
        TarArchiveInputStream in = null;
        try {

            in = new TarArchiveInputStream(new BufferedInputStream(new XZCompressorInputStream(new BufferedInputStream(new FileInputStream(input)))));

            ArchiveEntry entry;

            while ((entry = in.getNextEntry()) != null) {

                final String name = entry.getName();

                Log.v(TAG, "Extracting " + name);

                final File f = new File(outputDir, name);

                OutputStream out = null;
                try {
                    out = new FileOutputStream(f);
                    IOUtils.copy(in, out);
                } finally {
                    IOUtils.closeQuietly(out);
                }

                final int mode = ((TarArchiveEntry) entry).getMode();
                //noinspection ResultOfMethodCallIgnored
                f.setExecutable(true, (mode & 1) == 0);
            }

        } finally {
            IOUtils.closeQuietly(in);
        }
        //noinspection ResultOfMethodCallIgnored
        input.delete();
    }

    private static String sha256Hex(final String filePath) throws NoSuchAlgorithmException, IOException {
        final InputStream fis = new BufferedInputStream(new FileInputStream(filePath));
        final MessageDigest md = MessageDigest.getInstance("SHA-256");

        final byte[] dataBytes = new byte[1024];

        int nread;
        while ((nread = fis.read(dataBytes)) != -1)
            md.update(dataBytes, 0, nread);
        final byte[] mdbytes = md.digest();

        final StringBuilder sb = new StringBuilder();
        for (final byte b : mdbytes)
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));

        return sb.toString();
    }

    static void downloadFile(final String url, final String filePath) throws IOException {
        downloadFile(url, filePath, null);
    }

    static void downloadFile(final String url, final String filePath, final OnDownloadUpdate odsc) throws IOException {

        final FileOutputStream fos = new FileOutputStream(filePath);
        final long start_download_time = System.currentTimeMillis();

        final DataInputStream dis = new DataInputStream(new BufferedInputStream(new URL(url).openStream()));

        final byte[] buffer = new byte[1024];
        int length;

        long lastUpdate = 0;

        int totalBytesDownloaded = 0;
        while ((length = dis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
            if (odsc != null) {
                totalBytesDownloaded += length;
                final long currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdate > 200) {
                    final long ms = currentTime - start_download_time;
                    final int rate = (int) (totalBytesDownloaded / (ms / 1000.0));
                    odsc.update(rate, totalBytesDownloaded);
                    lastUpdate = currentTime;
                }
            }
        }

        IOUtils.closeQuietly(fos);
        IOUtils.closeQuietly(dis);
    }

    static String getArch() {
        for (final String abi : Build.SUPPORTED_ABIS) {
            switch (abi) {
                case "armeabi-v7a":
                    return "arm-linux-androideabi";
                case "arm64-v8a":
                    return "aarch64-linux-android";
                case "x86":
                    return "i686-linux-android";
                case "x86_64":
                    return "x86_64-linux-android";
            }
        }
        throw new ABIsUnsupported();
    }

    static File getDir(final Context c) {
        return c.getNoBackupFilesDir();
    }

    static String getBitcoinConf(final Context c) {
        return String.format("%s/.bitcoin/bitcoin.conf", getDir(c).getAbsolutePath());
    }

    static String getDataDir(final Context c) {
        final String defaultDataDir = String.format("%s/.bitcoin", getDir(c).getAbsolutePath());
        try {
            final Properties p = new Properties();
            p.load(new BufferedInputStream(new FileInputStream(getBitcoinConf(c))));
            return p.getProperty("datadir", defaultDataDir);
        } catch (final IOException e) {
            return defaultDataDir;
        }
    }

    static boolean isTestnet(final Context c) {
        try {
            final Properties p = new Properties();
            p.load(new BufferedInputStream(new FileInputStream(getBitcoinConf(c))));
            return p.getProperty("testnet", p.getProperty("regtest", "0")).equals("1");
        } catch (final IOException e) {
            return false;
        }
    }

    static String getFilePathFromUrl(final Context c, final String url) {
        return getDir(c).getAbsoluteFile() + "/" + url.substring(url.lastIndexOf("/") + 1);
    }

    static String isSha256Different(final String arch, final String sha256raw, final String filePath) throws IOException, NoSuchAlgorithmException {
        final String hash = Utils.sha256Hex(filePath);
        final String sha256hash = sha256raw.substring(sha256raw.indexOf(arch) + arch.length());
        Log.d(TAG, hash);
        return sha256hash.equals(hash) ? null : hash;
    }

    static void validateSha256sum(final String arch, final String sha256raw, final String filePath) throws IOException, NoSuchAlgorithmException {
        final String diff = isSha256Different(arch, sha256raw, filePath);
        if (diff != null)
            throw new ValidationFailure(String.format("File %s doesn't match sha256sum %s", filePath, diff));
    }

    static boolean isDaemonInstalled(final Context c) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        final String useDistribution = prefs.getString("usedistribution", "core");
        final String daemon = "liquid".equals(useDistribution) ? "liquidd" : "bitcoind";
        return new File(Utils.getDir(c).getAbsolutePath() + "/" + daemon).exists()
                && new File(Utils.getDir(c).getAbsolutePath() + "/tor").exists();
    }

    interface OnDownloadUpdate {
        void update(final int bytesPerSecond, final int bytesDownloaded);
    }

    static class ABIsUnsupported extends RuntimeException {
        ABIsUnsupported() {
            super(ABIsUnsupported.class.getName());
        }
    }

    static class ValidationFailure extends RuntimeException {
        ValidationFailure(final String s) {
            super(s);
        }
    }
}
