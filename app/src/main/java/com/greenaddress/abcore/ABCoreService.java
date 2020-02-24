package com.greenaddress.abcore;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class ABCoreService extends Service {

    private final static String TAG = ABCoreService.class.getName();
    private final static int NOTIFICATION_ID = 922430164;
    private static final String PARAM_OUT_MSG = "rpccore";
    private Process mBitcoinProcess;
    private Process mTorProcess;

    private final Thread bitcoinWaiterThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                final int exit = mBitcoinProcess.waitFor();
                Log.i(TAG, "Bitcoin process finished - " + exit);
                stopSelf();
            } catch (InterruptedException e) {
                Log.e(TAG, "Bitcoin InterruptedException", e);
            }
        }
    });

    private final Thread torWaiterThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                final int exit = mTorProcess.waitFor();
                Log.i(TAG, "Tor process finished - " + exit);
                // if the Tor process stops, the Bitcoin process will already stop itself
            } catch (InterruptedException e) {
                Log.e(TAG, "Tor InterruptedException", e);
            }
        }
    });

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (mBitcoinProcess != null || intent == null)
            return START_STICKY;

        Log.i(TAG, "Core service msg");

        try {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            startTor();
            startBitcoin();

            setupNotificationAndMoveToForeground();

        } catch (final IOException e) {
            Log.e(TAG, "Native exception!", e);
            mBitcoinProcess = null;
            mTorProcess = null;
            stopSelf();
        }
        Log.i(TAG, "background Task finished");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "destroying core service");

        if (mBitcoinProcess != null) {
            mBitcoinProcess.destroy();
            mBitcoinProcess = null;
        }
        if (mTorProcess != null) {
            mTorProcess.destroy();
            mTorProcess = null;
        }
        removeNotification();
    }

    private void startTor() throws IOException {
        final String path = getNoBackupFilesDir().getCanonicalPath();

        final ProcessBuilder torProcessBuilder = new ProcessBuilder(
                String.format("%s/%s", path, "tor"),
                "SafeSocks",
                "1",
                "SocksPort",
                "auto",
                "NoExec",
                "1",
                "CookieAuthentication",
                "1",
                "ControlPort",
                "9051",
                "DataDirectory",
                path + "/tordata"
        );

        torProcessBuilder.directory(new File(path));

        mTorProcess = torProcessBuilder.start();

        final ProcessLogger.OnError torErrorListener = new ProcessLogger.OnError() {
            @Override
            public void onError(final String[] error) {
                final StringBuilder bf = new StringBuilder();
                for (final String e : error) {
                    if (!TextUtils.isEmpty(e)) {
                        bf.append(String.format("%s%s", e, System.getProperty("line.separator")));
                        Log.e(TAG, "Tor process error - " + bf.toString());
                    }
                }
            }
        };

        final ProcessLogger torErrorGobbler = new ProcessLogger(mTorProcess.getErrorStream(),"TOR-ERR", torErrorListener);
        final ProcessLogger torOutputGobbler = new ProcessLogger(mTorProcess.getInputStream(), "TOR", torErrorListener);

        torErrorGobbler.start();
        torOutputGobbler.start();

        torWaiterThread.start();
    }

    private void startBitcoin() throws IOException {
        final String path = getNoBackupFilesDir().getCanonicalPath();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String useDistribution = prefs.getString("usedistribution", "core");
        final String daemon = "liquid".equals(useDistribution) ? "liquidd" : "bitcoind";
        final ProcessBuilder bitcoinProcessBuilder = new ProcessBuilder(
                String.format("%s/%s", path, daemon),
                "--server=1",
                String.format("--conf=%s", Utils.getBitcoinConf(this)));

        bitcoinProcessBuilder.directory(new File(path));

        mBitcoinProcess = bitcoinProcessBuilder.start();

        final ProcessLogger.OnError btcErrorListener = new ProcessLogger.OnError() {
            @Override
            public void onError(final String[] error) {
                final StringBuilder bf = new StringBuilder();
                for (final String e : error) {
                    if (!TextUtils.isEmpty(e)) {
                        bf.append(String.format("%s%s", e, System.getProperty("line.separator")));
                        Log.e(TAG, "Bitcoin process error - " + bf.toString());
                    }
                }
            }
        };

        final ProcessLogger errorGobbler = new ProcessLogger(mBitcoinProcess.getErrorStream(), "BTC-ERR", btcErrorListener);
        final ProcessLogger outputGobbler = new ProcessLogger(mBitcoinProcess.getInputStream(), "BTC", btcErrorListener);

        errorGobbler.start();
        outputGobbler.start();

        bitcoinWaiterThread.start();
    }

    private void setupNotificationAndMoveToForeground() {
        final Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pI;
        pI = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        final NotificationManager nM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String useDistribution = prefs.getString("usedistribution", "core");
        final String version = Packages.getVersion(useDistribution);

        final Notification.Builder b = new Notification.Builder(this)
                .setContentTitle("ABCore is running")
                .setContentIntent(pI)
                .setContentText(String.format("Version %s", version))
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int importance = NotificationManager.IMPORTANCE_LOW;

            final NotificationChannel mChannel = new NotificationChannel("channel_00", "ABCore", importance);
            mChannel.setDescription(String.format("Version %s", version));
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nM.createNotificationChannel(mChannel);
            b.setChannelId("channel_00");
        }

        final Notification n = b.build();

        Log.d(TAG, "startForeground");
        startForeground(NOTIFICATION_ID, n);

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.RPCResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, "OK");
        sendBroadcast(broadcastIntent);
    }

    private void removeNotification() {
        ((NotificationManager) this.getSystemService(NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.RPCResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, "exception");
        broadcastIntent.putExtra("exception", "");
        this.sendBroadcast(broadcastIntent);
    }
}
