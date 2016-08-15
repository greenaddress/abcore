package com.greenaddress.abcore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();
    private RPCResponseReceiver mRpcResponseReceiver;
    private TextView mTvStatus;
    private Switch mSwitchCore;
    private ProgressBar mProgressBar;

    private void postDetection() {
        mProgressBar.setVisibility(View.GONE);
        mSwitchCore.setVisibility(View.VISIBLE);
    }

    private void preDetection() {
        mProgressBar.setVisibility(View.VISIBLE);
        mSwitchCore.setVisibility(View.GONE);
    }

    private void postStart() {
        mSwitchCore.setOnCheckedChangeListener(null);
        postDetection();
        mTvStatus.setText(getString(R.string.runningturnoff, Packages.CORE_V_FULL));
        if (!mSwitchCore.isChecked())
            mSwitchCore.setChecked(true);
        mSwitchCore.setText(R.string.switchcoreoff);
        setSwitch();

    }

    private void postConfigure() {
        mSwitchCore.setOnCheckedChangeListener(null);
        postDetection();
        mTvStatus.setText(getString(R.string.stoppedturnon, Packages.CORE_V_FULL));
        if (mSwitchCore.isChecked())
            mSwitchCore.setChecked(false);
        mSwitchCore.setText(R.string.switchcoreon);
        setSwitch();
    }

    private void setSwitch() {
        mSwitchCore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                preDetection();
                if (isChecked) {
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    final SharedPreferences.Editor e = prefs.edit();
                    e.putBoolean("magicallystarted", false);
                    e.apply();
                    startService(new Intent(MainActivity.this, ABCoreService.class));
                }
                else {
                    final Intent i = new Intent(MainActivity.this, RPCIntentService.class);
                    i.putExtra("stop", "yep");
                    startService(i);
                }
            }
        });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvStatus = (TextView) findViewById(R.id.textView);
        mSwitchCore = (Switch) findViewById(R.id.switchCore);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        setSwitch();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRpcResponseReceiver != null)
            unregisterReceiver(mRpcResponseReceiver);
        mRpcResponseReceiver = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!Utils.isBitcoinCoreConfigured(this)) {
            startActivity(new Intent(this, DownloadActivity.class));
            return;
        }

        final IntentFilter rpcFilter = new IntentFilter(RPCResponseReceiver.ACTION_RESP);
        if (mRpcResponseReceiver == null)
            mRpcResponseReceiver = new RPCResponseReceiver();
        rpcFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mRpcResponseReceiver, rpcFilter);

        preDetection();
        startService(new Intent(this, RPCIntentService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
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

    public class RPCResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.greenaddress.intent.action.RPC_PROCESSED";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String text = intent.getStringExtra(RPCIntentService.PARAM_OUT_MSG);
            switch (text) {
                case "OK":
                    postStart();
                    break;
                case "exception":
                    final String exe = intent.getStringExtra("exception");
                    if (exe != null)
                        Log.i(TAG, exe);
                    postConfigure();
            }
        }
    }
}