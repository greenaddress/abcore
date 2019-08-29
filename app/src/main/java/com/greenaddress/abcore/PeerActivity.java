package com.greenaddress.abcore;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class PeerActivity extends ListActivity {

    private final List<String> listItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private RPCResponseReceiver rpcResponseReceiver;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setContentView(R.layout.activity_peer);

        setListAdapter(adapter);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                refresh();
                Snackbar.make(findViewById(android.R.id.content),
                        "Refreshed", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(rpcResponseReceiver);
        rpcResponseReceiver = null;
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

    @SuppressLint("RestrictedApi")
    private void refresh() {
        final ProgressBar pb = findViewById(R.id.progressBarPeerList);
        final FloatingActionButton fab = findViewById(R.id.fab);

        pb.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);

        final Intent i = new Intent(this, RPCIntentService.class);
        i.putExtra("REQUEST", "peerlist");
        startService(i);
    }

    class RPCResponseReceiver extends BroadcastReceiver {

        static final String ACTION_RESP =
                "com.greenaddress.intent.action.RPC_PROCESSED";

        @SuppressLint("RestrictedApi")
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final ProgressBar pb = findViewById(R.id.progressBarPeerList);
            pb.setVisibility(View.GONE);
            final FloatingActionButton fab = findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            final String text = intent.getStringExtra(RPCIntentService.PARAM_OUT_MSG);
            switch (text) {
                case "peerlist": {
                    final ArrayList<String> peers = intent.getStringArrayListExtra(text);
                    if (peers.isEmpty())
                        Snackbar.make(findViewById(android.R.id.content),
                                "There are no peers yet", Snackbar.LENGTH_LONG).show();
                    else {
                        adapter.clear();
                        adapter.addAll(peers);
                        adapter.notifyDataSetChanged();
                    }
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
