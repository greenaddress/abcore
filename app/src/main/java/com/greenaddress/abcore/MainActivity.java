package com.greenaddress.abcore;

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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();
    private RPCResponseReceiver mRpcResponseReceiver;
    private TextView mTvStatus;
    private TextView mTvDetails;
    private Switch mSwitchCore;
    private View mContent;

    private void postStart() {
        mTvStatus.setText(String.format("Bitcoin Core %s is running, please switch Core OFF to stop it.", Packages.CORE_V_FULL));

        mSwitchCore.setText("Switch Core off");
        if (!mSwitchCore.isChecked())
            mSwitchCore.setChecked(true);

        setSwitch();
    }

    private void postConfigure() {

        mTvDetails.setText(String.format("Bitcoin core %s fetched and configured", Packages.CORE_V_FULL));
        mTvStatus.setText(String.format("Bitcoin Core %s is not running, please switch Core ON to start it", Packages.CORE_V_FULL));
        setSwitch();
    }

    private void setSwitch() {
        mSwitchCore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    mTvDetails.setVisibility(View.GONE);
                    startService(new Intent(MainActivity.this, ABCoreService.class));
                    postStart();
                    mSwitchCore.setText("Switch Core off");
                } else {
                    final Intent i = new Intent(MainActivity.this, RPCIntentService.class);
                    i.putExtra("stop", "yep");
                    startService(i);
                    postConfigure();
                    mSwitchCore.setText("Switch Core on");
                }
            }
        });
    }

    private void reset() {

        mSwitchCore.setText("Switch Core on");

        mTvStatus.setText("");

        try {
            Utils.getArch();
        } catch (final Utils.UnsupportedArch e) {
            final String msg = String.format("Architeture %s is unsupported", e.arch);
            mTvStatus.setText(msg);
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
        if (msg != null && !msg.trim().isEmpty())
            Snackbar.make(mContent, msg, length).show();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvStatus = (TextView) findViewById(R.id.textView);
        mTvDetails = (TextView) findViewById(R.id.textViewDetails);
        mSwitchCore = (Switch) findViewById(R.id.switchCore);
        mContent = findViewById(android.R.id.content);
        setSupportActionBar(toolbar);

        reset();
        if (!Utils.isBitcoinCoreConfigured(this))
            startActivity(new Intent(this, DownloadActivity.class));
        postConfigure();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mRpcResponseReceiver);
        mRpcResponseReceiver = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        final IntentFilter rpcFilter = new IntentFilter(RPCResponseReceiver.ACTION_RESP);
        if (mRpcResponseReceiver == null)
            mRpcResponseReceiver = new RPCResponseReceiver();
        rpcFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mRpcResponseReceiver, rpcFilter);
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
                    Log.i(TAG, exe);

                    if (!Utils.isBitcoinCoreConfigured(MainActivity.this)) {
                        final float internal = Utils.megabytesAvailable(Utils.getDir(MainActivity.this));
                        final float external = Utils.megabytesAvailable(Utils.getLargestFilesDir(MainActivity.this));

                        if (internal > 70) {
                            mTvStatus.setText("Please select SETUP BITCOIN CORE to download and configure Core");
                        } else {
                            final String msg = String.format("You have %sMB but need about 70MB available in the internal memory", internal);
                            mTvStatus.setText(msg);
                            showSnackMsg(msg, Snackbar.LENGTH_INDEFINITE);
                        }

                        if (external < 70000) {
                            final String msg = String.format("You have %sMB but need about 70GB available memory unless you enable pruning", external);
                            mTvStatus.setText(msg);
                            // button.setVisibility(View.GONE);
                            showSnackMsg(msg);
                        }
                    } else
                        postConfigure();
                    break;
            }
        }
    }
}