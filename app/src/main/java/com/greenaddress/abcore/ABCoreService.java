package com.greenaddress.abcore;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class ABCoreService extends Service {

    final static String TAG = ABCoreService.class.getName();
    private Process mProcess;

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        final String arch = Utils.getArch();
        final File dir = Utils.getDir(this);
        Log.i(TAG, "Core service msg");

        // start core
        try {
            final String aarch = arch.equals("arm64") ? "aarch64" : arch.equals("amd64") ? "x86_64" : arch;
            final String gnu;

            if (arch.equals("armhf")) {
                gnu = "gnueabihf";
            } else {
                gnu = "gnu";
            }

            final String ld_linux;

            // on arch linux it is usr/lib/ld-linux-x86-64.so.2
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final Boolean archEnabled = prefs.getBoolean("archisenabled", false);

            if (archEnabled) {
                ld_linux = String.format("%s/usr/lib/ld-2.23.so", dir.getAbsoluteFile());
            } else if ("amd64".equals(arch) || "arm64".equals(arch)) {
                ld_linux = String.format("%s/lib/%s-linux-gnu/ld-2.21.so", dir.getAbsolutePath(), aarch);
            } else if ("armhf".equals(arch)) {
                ld_linux = String.format("%s/lib/ld-linux-armhf.so.3", dir.getAbsolutePath());
            } else {
                ld_linux = String.format("%s/lib/ld-linux.so.2", dir.getAbsoluteFile());
            }

            // allow to pass in a different datadir directory

            // HACK: if user sets a datadir in the bitcoin.conf file that should then be the one
            // used
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            final ProcessBuilder pb = new ProcessBuilder(ld_linux,
                    String.format("%s/usr/bin/bitcoind", dir.getAbsolutePath()),
                    "-server=1",
                    String.format("-datadir=%s", Utils.getDataDir(this)),
                    String.format("-conf=%s", Utils.getBitcoinConf(this)));

            final Map<String, String> env = pb.environment();

            // unset LD_PRELOAD for devices such as Samsung S6 (LD_PRELOAD errors on libsigchain.so starting core although works ..)

            env.put("LD_PRELOAD", "");

            env.put("LD_LIBRARY_PATH",
                    String.format("%s:%s:%s:%s:%s:%s",
                            String.format("%s/lib", dir.getAbsolutePath()),
                            String.format("%s/usr/lib", dir.getAbsolutePath()),
                            String.format("%s/lib/%s-linux-%s", dir.getAbsolutePath(), aarch, gnu),
                            String.format("%s/lib/arm-linux-gnueabihf", dir.getAbsolutePath()),
                            String.format("%s/usr/lib/%s-linux-%s", dir.getAbsolutePath(), aarch, gnu),
                            String.format("%s/usr/lib/arm-linux-gnueabihf", dir.getAbsolutePath())
                    ));

            pb.directory(new File(Utils.getDataDir(this)));

            mProcess = pb.start();
            final ProcessLogger errorGobbler = new ProcessLogger(mProcess.getErrorStream());
            final ProcessLogger outputGobbler = new ProcessLogger(mProcess.getInputStream());

            errorGobbler.start();
            outputGobbler.start();

        } catch (final IOException e) {
            Log.i(TAG, "Native exception!");
            Log.i(TAG, e.getMessage());

            Log.i(TAG, e.getLocalizedMessage());

            e.printStackTrace();
        }
        Log.i(TAG, "background Task finished");


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProcess != null) {
            mProcess.destroy();
        }
    }
}