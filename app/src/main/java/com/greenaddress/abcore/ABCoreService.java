package com.greenaddress.abcore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ABCoreService extends Service {

    private final static String TAG = ABCoreService.class.getName();
    final static int NOTIFICATION_ID = 922430164;
    private Process mProcess;

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    private void setupNotification() {
        final Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pI;
        pI = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        final NotificationManager nM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Notification n = new Notification.Builder(this)
                .setContentTitle("Abcore is running")
                .setContentIntent(pI)
                .setContentText("Currently started")
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setOngoing(true)
                .build();

        nM.notify(NOTIFICATION_ID, n);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        final String arch = Utils.getArch();
        final File dir = Utils.getDir(this);
        Log.i(TAG, "Core service msg");

        // start core
        try {
            final String tmpArch = arch.equals("amd64") ? "x86_64" : arch;
            final String aarch = arch.equals("arm64") ? "aarch64" : tmpArch;
            final String gnu;

            if (arch.equals("armhf")) {
                gnu = "gnueabihf";
            } else {
                gnu = "gnu";
            }

            final String ld;

            ld = String.format("%s/usr/lib/ld-%s.so", dir.getAbsoluteFile(), Packages.GLIBC_MAJOR);


            // allow to pass in a different datadir directory

            // HACK: if user sets a datadir in the bitcoin.conf file that should then be the one
            // used
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            final ProcessBuilder pb = new ProcessBuilder(ld,
                    String.format("%s/bitcoin-%s/bin/bitcoind", dir.getAbsolutePath(), Packages.CORE_V),
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
            final ProcessLogger.OnError er = new ProcessLogger.OnError() {
                @Override
                public void onError(final String[] error) {
                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
                    final StringBuilder bf = new StringBuilder();
                    for (final String e : error)
                        if (!TextUtils.isEmpty(e))
                            bf.append(String.format("%s%s", e, System.getProperty("line.separator")));

                    final Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(DownloadActivity.DownloadInstallCoreResponseReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra("abtcore", "exception");
                    broadcastIntent.putExtra("exception", bf.toString());

                    sendBroadcast(broadcastIntent);
                }
            };
            final ProcessLogger errorGobbler = new ProcessLogger(mProcess.getErrorStream(), er);
            final ProcessLogger outputGobbler = new ProcessLogger(mProcess.getInputStream(), er);

            errorGobbler.start();
            outputGobbler.start();

            setupNotification();

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