package com.greenaddress.abcore;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();
    private DownloadInstallCoreResponseReceiver downloadInstallCoreResponseReceiver;
    private RPCResponseReceiver rpcResponseReceiver;

    private static void postStart(final Activity activity) {
        // SHOW FEE AND OTHER NODE INFO
        final TextView status = (TextView) activity.findViewById(R.id.textView);
        final Button button = (Button) activity.findViewById(R.id.button);
        button.setVisibility(View.GONE);
        status.setText("Bitcoin Core is running, please switch Core OFF to stop it.");
        final Switch coreSwitch = (Switch) activity.findViewById(R.id.switchCore);

        coreSwitch.setVisibility(View.VISIBLE);
        coreSwitch.setText("Switch Core off");
        if (!coreSwitch.isChecked()) {
            coreSwitch.setChecked(true);
        }

        setSwitch(activity);
    }

    private static void postConfigure(final Activity activity) {

        final ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);
        final TextView tw = (TextView) activity.findViewById(R.id.textViewDetails);
        tw.setText("Bitcoin core fetched and configured");
        final TextView status = (TextView) activity.findViewById(R.id.textView);
        final Button button = (Button) activity.findViewById(R.id.button);
        status.setText("Bitcoin Core is not running, please switch Core ON to start it");
        button.setVisibility(View.GONE);
        setSwitch(activity);
    }

    private static void setSwitch(final Activity a) {
        final Switch coreSwitch = (Switch) a.findViewById(R.id.switchCore);
        coreSwitch.setVisibility(View.VISIBLE);
        coreSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    final TextView tw = (TextView) a.findViewById(R.id.textViewDetails);
                    tw.setVisibility(View.GONE);
                    a.startService(new Intent(a, ABCoreService.class));
                    postStart(a);
                    coreSwitch.setText("Switch Core off");
                } else {
                    final Intent i = new Intent(a, RPCIntentService.class);
                    i.putExtra("stop", "yep");
                    a.startService(i);
                    postConfigure(a);
                    coreSwitch.setText("Switch Core on");
                }
            }
        });
    }

    private void reset() {

        final TextView tw = (TextView) findViewById(R.id.textViewDetails);
        final TextView status = (TextView) findViewById(R.id.textView);
        final Switch coreSwitch = (Switch) findViewById(R.id.switchCore);
        coreSwitch.setVisibility(View.GONE);
        coreSwitch.setText("Switch Core on");

        status.setText("");
        tw.setText("");
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);

        try {
            // throws if the arch is unsupported
            Utils.getArch();

        } catch (final Utils.UnsupportedArch e) {

            final Button button = (Button) findViewById(R.id.button);
            button.setVisibility(View.GONE);

            final String msg = String.format("Architeture %s is unsupported", e.arch);
            status.setText(msg);
            showSnackMsg(msg, Snackbar.LENGTH_INDEFINITE);
            return;
        }

        // rpc check to see if core is already running!
        startService(new Intent(this, RPCIntentService.class));
    }

    private void showSnackMsg(final String msg) {
        showSnackMsg(msg, Snackbar.LENGTH_LONG);
    }

    private void showSnackMsg(final String msg, final int length) {
        if (msg != null && !msg.trim().isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content),
                    msg, length).show();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reset();
    }

    private String getSpeed(final int bytesPerSec) {
        if (bytesPerSec == 0) {
            return "";
        } else if (bytesPerSec > 1024 * 1024 * 1024) {
            return String.format("%s MB/s", bytesPerSec / (1024 * 1024 * 1024));
        } else if (bytesPerSec > 1024 * 1024) {
            return String.format("%s KB/s", bytesPerSec / (1024 * 1024));
        } else if (bytesPerSec > 1024) {
            return String.format("%s KB/s", bytesPerSec / 1024);
        } else {
            return String.format("%s B/s", bytesPerSec);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(downloadInstallCoreResponseReceiver);
        unregisterReceiver(rpcResponseReceiver);
        downloadInstallCoreResponseReceiver = null;
        rpcResponseReceiver = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        final IntentFilter downloadFilter = new IntentFilter(DownloadInstallCoreResponseReceiver.ACTION_RESP);
        if (downloadInstallCoreResponseReceiver == null) {
            downloadInstallCoreResponseReceiver = new DownloadInstallCoreResponseReceiver();
        }
        downloadFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(downloadInstallCoreResponseReceiver, downloadFilter);


        final IntentFilter rpcFilter = new IntentFilter(RPCResponseReceiver.ACTION_RESP);
        if (rpcResponseReceiver == null) {
            rpcResponseReceiver = new RPCResponseReceiver();
        }
        rpcFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(rpcResponseReceiver, rpcFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.configuration:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.peerview:
                startActivity(new Intent(this, PeerActivity.class));
                return true;
            case R.id.synchronization:
                startActivity(new Intent(this, ProgressActivity.class));
                return true;
            case R.id.debug:
                startActivity(new Intent(this, LogActivity.class));
                return true;
            case R.id.console:
                startActivity(new Intent(this, ConsoleActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class DownloadInstallCoreResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.greenaddress.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra(DownloadInstallCoreIntentService.PARAM_OUT_MSG);
            switch (text) {
                case "OK": {
                    postConfigure(MainActivity.this);
                    break;
                }
                case "exception": {
                    String exe = intent.getStringExtra("exception");
                    Log.i(TAG, exe);
                    showSnackMsg(exe);
                    final Button button = (Button) findViewById(R.id.button);
                    final TextView status = (TextView) findViewById(R.id.textView);
                    final ProgressBar pb = (ProgressBar) MainActivity.this.findViewById(R.id.progressBar);

                    button.setEnabled(true);
                    pb.setVisibility(View.GONE);
                    pb.setProgress(0);
                    status.setText("Please select SETUP BITCOIN CORE to download and configure Core");
                    final Switch coreSwitch = (Switch) MainActivity.this.findViewById(R.id.switchCore);

                    if (coreSwitch.isChecked()) {
                        coreSwitch.setChecked(false);
                    }

                    reset();
                    break;
                }
                case "ABCOREUPDATE": {


                    final ProgressBar pb = (ProgressBar) MainActivity.this.findViewById(R.id.progressBar);
                    final TextView tw = (TextView) MainActivity.this.findViewById(R.id.textViewDetails);
                    tw.setText(String.format("%s %s", getSpeed(intent.getIntExtra("ABCOREUPDATESPEED", 0)), intent.getStringExtra("ABCOREUPDATETXT")));

                    pb.setVisibility(View.VISIBLE);
                    pb.setMax(intent.getIntExtra("ABCOREUPDATEMAX", 100));
                    pb.setProgress(intent.getIntExtra("ABCOREUPDATE", 0));
                    final Button button = (Button) findViewById(R.id.button);

                    button.setEnabled(false);
                    final TextView status = (TextView) findViewById(R.id.textView);

                    status.setText("Please wait. Fetching, unpacking and configuring bitcoin core...");

                    break;
                }
            }
        }
    }

    public class RPCResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.greenaddress.intent.action.RPC_PROCESSED";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String text = intent.getStringExtra(RPCIntentService.PARAM_OUT_MSG);
            switch (text) {
                case "OK": {
                    postStart(MainActivity.this);
                    break;
                }
                case "exception":

                    final boolean requiresDownload = !new File(Utils.getDir(context).getAbsolutePath() + "/usr/bin", "bitcoind").exists();
                    final TextView status = (TextView) findViewById(R.id.textView);
                    final Button button = (Button) findViewById(R.id.button);
                    final ProgressBar pb = (ProgressBar) MainActivity.this.findViewById(R.id.progressBar);

                    String exe = intent.getStringExtra("exception");
                    Log.i(TAG, exe);

                    if (requiresDownload) {
                        final float internal = Utils.megabytesAvailable(Utils.getDir(MainActivity.this));
                        final float external = Utils.megabytesAvailable(Utils.getLargestFilesDir(MainActivity.this));

                        if (internal > 70) {
                            status.setText("Please select SETUP BITCOIN CORE to download and configure Core");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    button.setEnabled(false);
                                    pb.setVisibility(View.VISIBLE);
                                    pb.setProgress(0);
                                    status.setText("Please wait. Fetching, unpacking and configuring bitcoin core...");

                                    startService(new Intent(MainActivity.this, DownloadInstallCoreIntentService.class));
                                }
                            });
                        } else {
                            final String msg = String.format("You have %sMB but need about 70MB available in the internal memory", internal);
                            status.setText(msg);

                            button.setVisibility(View.GONE);
                            showSnackMsg(msg, Snackbar.LENGTH_INDEFINITE);
                        }

                        if (external < 70000) {
                            final String msg = String.format("You have %sMB but need about 70GB available in the external memory", external);
                            status.setText(msg);

                            // button.setVisibility(View.GONE);
                            showSnackMsg(msg);

                        }


                    } else {
                        postConfigure(MainActivity.this);
                    }
                    break;

            }
        }
    }
}
