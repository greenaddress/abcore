package com.greenaddress.abcore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class ProgressActivity extends AppCompatActivity {

    private Timer timer;
    private RPCResponseReceiver rpcResponseReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(rpcResponseReceiver);
        rpcResponseReceiver = null;
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter filter = new IntentFilter(RPCResponseReceiver.ACTION_RESP);
        if (rpcResponseReceiver == null) {
            rpcResponseReceiver = new RPCResponseReceiver();
        }
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(rpcResponseReceiver, filter);
        refresh();
    }

    private void refresh() {
        final Intent i = new Intent(this, RPCIntentService.class);
        i.putExtra("REQUEST", "progress");
        startService(i);
    }

    class RPCResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP =
                "com.greenaddress.intent.action.RPC_PROCESSED";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarSyncBlock);
            final TextView textStatus = (TextView) findViewById(R.id.textViewSyncBlock);
            final String text = intent.getStringExtra(RPCIntentService.PARAM_OUT_MSG);
            switch (text) {
                case "progress": {
                    final int max = intent.getIntExtra("max", -1);
                    final int sync = intent.getIntExtra("sync", -1);
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                    }
                    if (max == -1) {
                        Snackbar.make(findViewById(android.R.id.content),
                                "There are no peers yet", Snackbar.LENGTH_LONG).show();
                    } else {
                        pb.setMax(max);
                        pb.setProgress(sync);
                        if (max == sync) {
                            textStatus.setText(String.format("Up to date (block height %s)", sync));
                        } else {
                            textStatus.setText(String.format("Processed %s%s (%s out of %s)", new DecimalFormat("#.##").format(((double) sync / max) * 100.0), "%", sync, max));
                        }

                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refresh();
                                    }
                                });
                            }
                        }, 1000, 1000);
                    }

                    break;
                }
                case "exception":
                    Snackbar.make(findViewById(android.R.id.content),
                            "Core is not running", Snackbar.LENGTH_INDEFINITE).show();
                    break;
            }
        }
    }
}
