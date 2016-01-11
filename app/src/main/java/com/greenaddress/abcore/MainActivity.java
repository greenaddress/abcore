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
    private DownloadInstallCoreResponseReceiver downloadInstallCoreResponseReceiver;
    private RPCResponseReceiver rpcResponseReceiver;

    final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

            Snackbar.make(findViewById(android.R.id.content),
                    msg, Snackbar.LENGTH_INDEFINITE).show();
            return;
        }

        // rpc check to see if core is already running!
        startService(new Intent(this, RPCIntentService.class));
    }

    private static void postStart(final Activity activity) {
        // SHOW FEE AND OTHER NODE INFO
        final TextView status = (TextView) activity.findViewById(R.id.textView);
        final Button button = (Button) activity.findViewById(R.id.button);
        button.setVisibility(View.GONE);
        status.setText("Bitcoin Core is running, select STOP CORE to stop it.");
        final Switch coreSwitch = (Switch) activity.findViewById(R.id.switchCore);
        coreSwitch.setVisibility(View.VISIBLE);
        coreSwitch.setText("Switch Core off");
        if (!coreSwitch.isChecked()) {
            coreSwitch.setChecked(true);
        }
    }


    private static void postConfigure(final Activity activity) {

        final ProgressBar pb = (ProgressBar) activity.findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);
        final TextView tw = (TextView) activity.findViewById(R.id.textViewDetails);
        tw.setText("Bitcoin core fetched and configured");
        final TextView status = (TextView) activity.findViewById(R.id.textView);
        final Button button = (Button) activity.findViewById(R.id.button);
        status.setText("Bitcoin Core is not running, please select START CORE to start it");
        button.setVisibility(View.GONE);
        final Switch coreSwitch = (Switch) activity.findViewById(R.id.switchCore);
        coreSwitch.setVisibility(View.VISIBLE);
        coreSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    final TextView tw = (TextView) activity.findViewById(R.id.textViewDetails);
                    tw.setVisibility(View.GONE);
                    activity.startService(new Intent(activity, ABCoreService.class));
                    postStart(activity);
                    coreSwitch.setText("Switch Core off");
                } else {
                    final Intent i = new Intent(activity, RPCIntentService.class);
                    i.putExtra("stop", "yep");
                    activity.startService(i);
                    postConfigure(activity);
                    coreSwitch.setText("Switch Core on");
                }
            }
        });

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
                    break;
                }
                case "ABCOREUPDATE": {
                    final String file = intent.getStringExtra("ABCOREUPDATETXT");
                    int update = intent.getIntExtra("ABCOREUPDATE", 0);
                    int max = intent.getIntExtra("ABCOREUPDATEMAX", 100);

                    final ProgressBar pb = (ProgressBar) MainActivity.this.findViewById(R.id.progressBar);
                    final TextView tw = (TextView) MainActivity.this.findViewById(R.id.textViewDetails);
                    tw.setText(file);
                    pb.setVisibility(View.VISIBLE);
                    pb.setMax(max);
                    pb.setProgress(update);
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
                        final float external = Utils.megabytesAvailable(Utils.getLargetFilesDir(MainActivity.this));

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
                        }  else {
                            final String msg = String.format("You have %sMB but need about 70MB available in the internal memory", internal);
                            status.setText(msg);

                            button.setVisibility(View.GONE);
                            Snackbar.make(findViewById(android.R.id.content),
                                    msg, Snackbar.LENGTH_INDEFINITE).show();
                        }

                        if (external < 70000) {
                            final String msg = String.format("You have %sMB but need about 70GB available in the external memory", external);
                            status.setText(msg);

                            // button.setVisibility(View.GONE);
                            Snackbar.make(findViewById(android.R.id.content),
                                    msg, Snackbar.LENGTH_LONG).show();
                        }


                    } else {
                        postConfigure(MainActivity.this);
                    }
                    break;

            }
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
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}