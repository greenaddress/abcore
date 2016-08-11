package com.greenaddress.abcore;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

class Utils {

    private final static String TAG = Utils.class.getSimpleName();

    @SuppressWarnings("deprecation")
    static float megabytesAvailable(final File f) {
        if (f == null || !f.exists())
            return 0;
        final StatFs stat = new StatFs(f.getPath());
        final long bytesAvailable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        else
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();

        return bytesAvailable / (1024.f * 1024.f);
    }

    static void extractTarXz(final File input, final File outputDir, final boolean xz) throws IOException {
        TarArchiveInputStream in = null;
        List<FileToCopy> toCopy = new ArrayList<>();
        try {
            if (xz)
                in = new TarArchiveInputStream(new BufferedInputStream(new XZCompressorInputStream(new BufferedInputStream(new FileInputStream(input)))));
            else
                in = new TarArchiveInputStream(new BufferedInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(input)))));

            ArchiveEntry entry;

            while ((entry = in.getNextEntry()) != null) {
                final String entryName = entry.getName();

                // we don't need these files so may as well skip them
                if (entryName.contains("usr/share/")
                        || entryName.contains("bitcoin-cli")
                        || entryName.contains("usr/include")
                        || entryName.contains("etc/")
                        || entryName.contains("python")
                        || entryName.contains("fortran")
                        || entryName.contains("libgo")
                        || entryName.contains("libitm")
                        || entryName.contains("liblsan")
                        || entryName.contains("libubsan")
                        || entryName.contains("libtsan")
                        || entryName.contains("libdb_stl")
                        || entryName.contains("libquadmath")
                        || entryName.contains("libcilkrts")
                        || entryName.contains("libobjc")
                        || entryName.contains("libatomic")
                        || entryName.contains("libcidn")
                        || entryName.contains("libmvec")
                        || entryName.contains("libmpx")
                        || entryName.contains("libnsl")
                        || entryName.contains("libutil")
                        || entryName.contains("libvtv")
                        || entryName.contains("libdb-5")
                        || entryName.contains("libBrokenLocale")
                        || entryName.contains("libmemusage")
                        || entryName.contains("libnss")
                        || entryName.contains("libresolv")
                        || entryName.contains("libSegFault")
                        || entryName.contains("libpcprofile")
                        || entryName.contains("usr/lib/engines")
                        || entryName.contains("mpi.so")
                        || entryName.endsWith(".a")
                        || (entryName.contains("usr/bin") && !entryName.contains("bitcoind"))
                        || entryName.contains("libasan")
                        || (entryName.contains("libboost") && (
                        !entryName.contains("system")
                                && !entryName.contains("program")
                                && !entryName.contains("thread")
                                && !entryName.contains("chrono")))
                        || (entryName.contains("usr/lib/") && entryName.contains("/gconv/"))) {
                    continue;
                }

                Log.v(TAG, "Entry in tar " + entry.getName() + " is " + ((TarArchiveEntry) entry).isSymbolicLink());

                final File f = new File(outputDir, entry.getName());

                if (entry.isDirectory())
                    f.mkdirs();
                else {
                    f.getParentFile().mkdirs();
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(f);
                        IOUtils.copy(in, out);
                    } finally {
                        IOUtils.closeQuietly(out);
                    }
                }

                f.setLastModified(entry.getLastModifiedDate().getTime());

                if (((TarArchiveEntry) entry).isSymbolicLink() && !entry.isDirectory()) {
                    final String name = entry.getName();
                    final String linkName = ((TarArchiveEntry) entry).getLinkName();
                    final String linkedFile = linkName.startsWith("/") ? linkName : name.substring(0, name.lastIndexOf("/")) + "/" + linkName;

                    // copy them later as they may ref files not extracted yet
                    toCopy.add(new FileToCopy(linkedFile, name));
                }
                final int mode = ((TarArchiveEntry) entry).getMode();

                if ((mode & 64) > 0)
                    f.setExecutable(true, (mode & 1) == 0);
            }

            for (final FileToCopy f : toCopy)
                try {
                    copyFile(f.src, f.dst, outputDir);
                } catch (final IOException e1) {
                    // usr share only
                    Log.v(TAG, "MISSING NAME " + f.src + " LINKS (" + f.dst + ")");
                }
        } finally {
            IOUtils.closeQuietly(in);
        }
        input.delete();
    }

    private static void copyFile(final String src, final String dst, final File outputDir) throws IOException {
        final InputStream linked = new BufferedInputStream(new FileInputStream(new File(outputDir, src)));
        final OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outputDir, dst)));

        final byte[] buf = new byte[1024];
        int len;
        while ((len = linked.read(buf)) > 0)
            out.write(buf, 0, len);

        IOUtils.closeQuietly(linked);
        IOUtils.closeQuietly(out);
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

    static void downloadFile(final String url, final String filePath, final OnDownloadSpeedChange odsc) throws IOException {

        final FileOutputStream fos = new FileOutputStream(filePath);
        final long start_download_time = System.currentTimeMillis();

        final DataInputStream dis = new DataInputStream(new BufferedInputStream(new URL(url).openStream()));

        final byte[] buffer = new byte[1024];
        int length;

        long lastUpdate = 0;

        int totalBytesDownloaded = 0, currentRate = 0;
        while ((length = dis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
            if (odsc != null) {
                totalBytesDownloaded += length;
                final long currentTime = System.currentTimeMillis();
                final long ms = currentTime - start_download_time;
                if (ms > 200) {
                    final int rate = (int) (totalBytesDownloaded / (ms / 1000.0));
                    if (rate != currentRate) {
                        if (currentTime - lastUpdate > 200) {
                            odsc.bytesPerSecondUpdate(rate);
                            lastUpdate = currentTime;
                        }
                        currentRate = rate;
                    }
                }
            }

        }

        IOUtils.closeQuietly(fos);
        IOUtils.closeQuietly(dis);
    }

    static String getCorePkgsName() {
        final String arch = System.getProperty("os.arch");
        if (arch.endsWith("86"))
            return "i686-pc-linux-gnu";
        else if (arch.startsWith("armv7"))
            return "arm-linux-gnueabihf";
        else if (arch.endsWith("86_64"))
            return "x86_64-linux-gnu";
        else if ("aarch64".equals(arch))
            return "aarch64-linux-gnu";
        throw new UnsupportedArch(arch);
    }

    static String getArch() {
        final String arch = System.getProperty("os.arch");
        if (arch.endsWith("86"))
            return "i386";
        else if (arch.startsWith("armv7"))
            return "armhf";
        else if (arch.endsWith("86_64"))
            return "amd64";
        else if ("aarch64".equals(arch))
            return "arm64";
        throw new UnsupportedArch(arch);
    }

    static File getDir(final Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return c.getNoBackupFilesDir();
        else
            return c.getFilesDir();
    }

    static String getBitcoinConf(final Context c) {
        return String.format("%s/.bitcoin/bitcoin.conf", getDir(c).getAbsolutePath());
    }

    static File getLargestFilesDir(final Context c) {
        File largest = getDir(c);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            for (final File f : c.getExternalFilesDirs(null)) {
                if (megabytesAvailable(f) > megabytesAvailable(largest)) {
                    largest = f;
                }
            }
            for (final File f : c.getExternalCacheDirs()) {
                if (megabytesAvailable(f) > megabytesAvailable(largest)) {
                    largest = f;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (final File f : c.getExternalMediaDirs()) {
                    if (megabytesAvailable(f) > megabytesAvailable(largest)) {
                        largest = f;
                    }
                }
            }
        }
        return largest;
    }

    static String getDataDir(final Context c) {
        final String defaultDataDir = String.format("%s/.bitcoin", getLargestFilesDir(c).getAbsolutePath());
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

    static String toBase58(final byte[] in) {

        final int[] indexes = new int[128];

        Arrays.fill(indexes, -1);

        final char[] ab = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

        for (int k = 0; k < ab.length; ++k)
            indexes[ab[k]] = k;

        int zeroCounter = 0;

        while (zeroCounter < in.length && in[zeroCounter] == 0)
            ++zeroCounter;

        final byte[] cp = Arrays.copyOf(in, in.length);

        final char[] enc = new char[cp.length * 2];

        int resBegin = enc.length;
        int begin = zeroCounter;

        while (begin < cp.length) {

            int rem = 0;
            for (int j = begin; j < cp.length; ++j) {
                final int temp = ((int) cp[j] & 0xFF) + (256 * rem);
                cp[j] = (byte) (temp / 58);
                rem = temp % 58;
            }

            enc[--resBegin] = ab[(byte) rem];
            if (cp[begin] == 0)
                ++begin;
        }

        while (resBegin < enc.length && ab[0] == enc[resBegin])
            ++resBegin;

        while (--zeroCounter >= 0)
            enc[--resBegin] = ab[0];

        return new String(enc, resBegin, enc.length - resBegin);
    }


    static String getArchLinuxArchitecture(final String arch) {
        switch (arch) {
            case "amd64":
                return "x86_64";
            case "i386":
                return "i686";
            case "armhf":
                return "armv7h";
            case "arm64":
                return "aarch64";
            default:
                throw new UnsupportedArch(arch);
        }
    }

    static String getFilePathFromUrl(final Context c, final String url) {
        return getLargestFilesDir(c).getAbsoluteFile() + "/" + url.substring(url.lastIndexOf("/") + 1);
    }

    static void validateSha256sum(final String arch, final String sha256raw, final String filePath) throws IOException, NoSuchAlgorithmException {
        final String hash = Utils.sha256Hex(filePath);
        final String sha256hash = sha256raw.substring(sha256raw.indexOf(arch) + arch.length());

        if (!sha256hash.equals(hash))
            throw new ValidationFailure(String.format("File %s doesn't match sha256sum %s, expected %s", filePath, hash, sha256hash));
    }

    interface OnDownloadSpeedChange {
        void bytesPerSecondUpdate(final int bytes);
    }

    static class FileToCopy {
        final String src, dst;

        FileToCopy(final String src, final String dst) {
            this.src = src;
            this.dst = dst;
        }
    }

    static class UnsupportedArch extends RuntimeException {
        final String arch;

        UnsupportedArch(final String a) {
            super(UnsupportedArch.class.getName());
            this.arch = a;
        }
    }

    static class ValidationFailure extends RuntimeException {
        ValidationFailure(final String s) {
            super(s);
        }
    }
}
