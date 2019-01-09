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

import java.util.Timer;
import java.util.TimerTask;

public class ProgressActivity extends AppCompatActivity {

    private Timer timer;
    private RPCResponseReceiver rpcResponseReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        final Toolbar toolbar = findViewById(R.id.toolbar);
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
        if (rpcResponseReceiver == null)
            rpcResponseReceiver = new RPCResponseReceiver();
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

        static final String ACTION_RESP =
                "com.greenaddress.intent.action.RPC_PROCESSED";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final ProgressBar pb = findViewById(R.id.progressBarSyncBlock);
            final TextView textStatus = findViewById(R.id.textViewSyncBlock);
            final String text = intent.getStringExtra(RPCIntentService.PARAM_OUT_MSG);
            switch (text) {
                case "progress": {
                    final int max = 100;
                    final int blocks = intent.getIntExtra("blocks", 0);
                    final int percent = intent.getIntExtra("sync", -1);
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                    }

                    pb.setMax(max);
                    pb.setProgress(percent);
                    textStatus.setText(String.format("Processed %s%s (block height %s)", percent, "%", blocks));
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


                    break;
                }
                case "exception":
                    Snackbar.make(findViewById(android.R.id.content),
                            "Daemon is not running", Snackbar.LENGTH_INDEFINITE).show();
                    break;
            }
        }
    }
}
