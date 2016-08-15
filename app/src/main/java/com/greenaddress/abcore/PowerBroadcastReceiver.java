package com.greenaddress.abcore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class PowerBroadcastReceiver extends BroadcastReceiver {
    private Boolean mWifiIsOn = null;
    private Boolean mCharging = null;
    private BroadcastReceiver mReceiver = null;
    private long mMillis = -1;
    private static final String TAG = PowerBroadcastReceiver.class.getName();

    private static boolean isCharging(final Context context) {
        final Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        final int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }

    private static boolean isWifiConnected(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public class RPCResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.greenaddress.intent.action.RPC_PROCESSED";

        @Override
        public void onReceive(final Context context, final Intent intent) {

            Log.d(TAG, "mWifiIsOn " + mWifiIsOn);
            Log.d(TAG, "mCharging " + mCharging);

            context.unregisterReceiver(mReceiver);
            final String text = intent.getStringExtra(RPCIntentService.PARAM_OUT_MSG);
            switch (text) {
                case "OK":
                    Log.w(TAG, "CORE IS ALREADY RUNNING");
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    if ((!mWifiIsOn || !mCharging) && prefs.getBoolean("magicallystarted", false) ) {
                        Log.w(TAG, "STOPPING IT");
                        stopCore(context);
                    }
                    break;
                case "exception":
                    Log.w(TAG, "CORE IS NOT RUNNING");
                    if (mWifiIsOn && mCharging && (mMillis == -1 || System.currentTimeMillis() - mMillis > 20000)) {
                        Log.w(TAG, "STARTING CORE");
                        mMillis = System.currentTimeMillis();
                        startCore(context);
                    }
            }
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_LOW:
            case Intent.ACTION_SHUTDOWN:
            case Intent.ACTION_POWER_DISCONNECTED:
                if (mCharging != null && !mCharging)
                    return;
                mCharging = false;
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                final Boolean prev = mWifiIsOn;
                final NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                mWifiIsOn = info != null && info.isConnected() || isWifiConnected(context);
                if (prev != null && prev == mWifiIsOn)
                    return;
                break;
            case Intent.ACTION_POWER_CONNECTED:
                if (mCharging != null && mCharging) {
                    return;
                }
                mCharging = true;
                break;
            default:
                Log.w(TAG, intent.getAction());
                return;
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean("startonchargingandwifi", false))
            return;

        if (mCharging == null)
            mCharging = isCharging(context);

        if (mWifiIsOn == null)
            mWifiIsOn = isWifiConnected(context);


        final IntentFilter rpcFilter = new IntentFilter(RPCResponseReceiver.ACTION_RESP);
        mReceiver = new RPCResponseReceiver();
        rpcFilter.addCategory(Intent.CATEGORY_DEFAULT);
        context.getApplicationContext().registerReceiver(mReceiver, rpcFilter);
        context.startService(new Intent(context, RPCIntentService.class));
    }

    private void startCore(final Context c) {
        setMagicallyStarted(c, true);
        c.startService(new Intent(c, ABCoreService.class));
    }

    private void setMagicallyStarted(final Context c, final boolean started) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        final SharedPreferences.Editor e = prefs.edit();
        e.putBoolean("magicallystarted", started);
        e.apply();
    }

    private void stopCore(final Context c) {
        final Intent i = new Intent(c, RPCIntentService.class);
        i.putExtra("stop", "yep");
        c.startService(i);
        setMagicallyStarted(c, false);
    }
}