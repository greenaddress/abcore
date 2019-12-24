package com.greenaddress.abcore;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();
    private RPCResponseReceiver mRpcResponseReceiver;
    private TextView mTvStatus;
    private Switch mSwitchCore;
    private TextView mQrCodeText;
    private ImageView mImageViewQr;

    private final static int SCALE = 4;
    private boolean mSwitchOn = false;
    private enum DaemonStatus {
        UNKNOWN,
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED
    }
    private DaemonStatus mDaemonStatus = DaemonStatus.STOPPED;

    private final Handler mMsgHandler = new Handler();
    /**
     * Runnable object that refreshes the UI with updated
     * mDaemonStatus and progress
     */
    private final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            refresh();
            mMsgHandler.postDelayed(this, 1000);
        }
    };

    private void refresh() {
        if (mSwitchOn){
            if (mDaemonStatus == DaemonStatus.STARTING || mDaemonStatus == DaemonStatus.RUNNING || mDaemonStatus == DaemonStatus.UNKNOWN) {
                //refresh
                final Intent i = new Intent(this, RPCIntentService.class);
                i.putExtra("REQUEST", "localonion");
                startService(i);
            }
            else {
                // mDaemonStatus = STOPPING or STOPPED
                // if we get here it means that the mDaemonStatus and mSwitchOn somehow fell out of sync
                // This is a bad state and we will simply try to stop the daemon and get back to a
                // consistent state
                stopDaemonAndSetStatus();
            }
        } else{
            // switch OFF
            if (mDaemonStatus == DaemonStatus.STOPPING || mDaemonStatus == DaemonStatus.UNKNOWN) {
                //refresh
                final Intent i = new Intent(this, RPCIntentService.class);
                i.putExtra("REQUEST", "localonion");
                startService(i);
            }
            else if (mDaemonStatus == DaemonStatus.STARTING || mDaemonStatus == DaemonStatus.RUNNING){
                // if we get here it means that the mDaemonStatus and mSwitchOn somehow fell out of sync
                // This is a bad state and we will simply try to stop the daemon and get back to a
                // consistent state
                stopDaemonAndSetStatus();
            }
            // when mDaemonStatus = STOPPED, switch OFF and daemon is not running, so nothing to do
        }
    }

    private void stopDaemonAndSetStatus(){
        mSwitchOn = false;
        mDaemonStatus = DaemonStatus.STOPPING;
        mTvStatus.setText(getString(R.string.status_header, mDaemonStatus.toString()));
        mSwitchCore.setText(R.string.switchcoreon);
        final Intent i = new Intent(MainActivity.this, RPCIntentService.class);
        i.putExtra("stop", "yep");
        startService(i);
    }

    private void setSwitch() {
        mSwitchCore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    mSwitchOn = true;
                    mDaemonStatus = DaemonStatus.STARTING;
                    mTvStatus.setText(getString(R.string.status_header, mDaemonStatus.toString()));
                    mSwitchCore.setText(R.string.switchcoreoff);
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    final SharedPreferences.Editor e = prefs.edit();
                    e.putBoolean("magicallystarted", false);
                    e.apply();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(MainActivity.this, ABCoreService.class));
                    } else {
                        startService(new Intent(MainActivity.this, ABCoreService.class));
                    }
                } else {
                    stopDaemonAndSetStatus();
                }
            }
        });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        mTvStatus = findViewById(R.id.textViewStatus);
        mSwitchCore = findViewById(R.id.switchCore);
        mQrCodeText = findViewById(R.id.textViewQr);
        mImageViewQr = findViewById(R.id.qrcodeImageView);
        setSupportActionBar(toolbar);

        setSwitch();
        final View.OnClickListener cliboard = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clip = ClipData.newPlainText("Onion Address", mQrCodeText.getText().toString());
                if (clipboard != null && clip != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "Copied to clipboard!", Toast.LENGTH_LONG).show();
                }
            }
        };
        mImageViewQr.setOnClickListener(cliboard);
        mQrCodeText.setOnClickListener(cliboard);
        mDaemonStatus = DaemonStatus.UNKNOWN;
        mSwitchCore.setText(R.string.switchcoreon);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRpcResponseReceiver != null)
            unregisterReceiver(mRpcResponseReceiver);
        mRpcResponseReceiver = null;
        mMsgHandler.removeCallbacks(runnableCode);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!Utils.isDaemonInstalled(this)) {
            startActivity(new Intent(this, DownloadActivity.class));
            return;
        }

        final IntentFilter rpcFilter = new IntentFilter(RPCResponseReceiver.ACTION_RESP);
        if (mRpcResponseReceiver == null)
            mRpcResponseReceiver = new RPCResponseReceiver();
        rpcFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mRpcResponseReceiver, rpcFilter);

        startService(new Intent(this, RPCIntentService.class));
        getSupportActionBar().setTitle(R.string.title_activity_main);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final String useDistribution = prefs.getString("usedistribution", "core");
        getSupportActionBar().setSubtitle(getString(R.string.subtitle, useDistribution));
        final TextView mTvDaemon = findViewById(R.id.textViewDaemon);
        final String daemonVersion = "knots".equals(useDistribution) ? Packages.BITCOIN_KNOTS_NDK : "liquid".equals(useDistribution) ? Packages.BITCOIN_LIQUID_NDK : Packages.BITCOIN_NDK;
        mTvDaemon.setText(getString(R.string.subtitle, useDistribution + " " + daemonVersion));
        mTvStatus.setText(getString(R.string.status_header, mDaemonStatus.toString()));
        mMsgHandler.postDelayed(runnableCode, 1000);
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
            if (context == null || intent == null || !intent.hasExtra(RPCIntentService.PARAM_OUT_MSG))
                return;
            final String text = intent.getStringExtra(RPCIntentService.PARAM_OUT_MSG);
            if (text == null)
                return;
            final ProgressBar pb = findViewById(R.id.progressBarSyncBlock);
            final TextView textStatus = findViewById(R.id.textViewSyncBlock);

            switch (text) {
                case "OK":
                    if (mDaemonStatus == DaemonStatus.STARTING || mDaemonStatus == DaemonStatus.UNKNOWN){
                        mDaemonStatus = DaemonStatus.RUNNING;
                        mTvStatus.setText(getString(R.string.status_header, mDaemonStatus.toString()));
                    }
                    else if (mDaemonStatus == DaemonStatus.STOPPED ){
                        // if we get here it means that the daemon is *actually* running but the screen is reflecting
                        // as OFF. This is a bad state and we will simply try to stop the daemon and get
                        // back to a consistent state
                        stopDaemonAndSetStatus();
                    }
                    // for mDaemonStatus = STOPPING or RUNNING we don't have to do anything, the next time it refreshes
                    // the right status will get reflected
                    break;
                case "exception":
                    if (intent.hasExtra("exception")) {
                        final String exe = intent.getStringExtra("exception");
                        if (exe != null)
                            Log.i(TAG, exe);
                    }

                    if (mDaemonStatus == DaemonStatus.STOPPING || mDaemonStatus == DaemonStatus.UNKNOWN){
                        mDaemonStatus = DaemonStatus.STOPPED;
                        mTvStatus.setText(getString(R.string.status_header, mDaemonStatus.toString()));
                    }
                    else if (mDaemonStatus == DaemonStatus.STARTING || mDaemonStatus == DaemonStatus.RUNNING){
                        // if we get here it means that the daemon is *actually not* running but the screen is reflecting
                        // as if its running or we are trying to start it. This is a bad state and we will simply
                        // try to stop the daemon and get back to a consistent state
                        stopDaemonAndSetStatus();
                    }
                    //for mDaemonStatus = STOPPED we don't have to do anything
                    break;
                case "stopped":
                    mDaemonStatus = DaemonStatus.STOPPED;
                    mTvStatus.setText(getString(R.string.status_header, mDaemonStatus.toString()));
                    break;
                case "localonion":
                    if (mDaemonStatus == DaemonStatus.STARTING || mDaemonStatus == DaemonStatus.UNKNOWN){
                        mDaemonStatus = DaemonStatus.RUNNING;
                        mTvStatus.setText(getString(R.string.status_header, mDaemonStatus.toString()));
                    }
                    else if (mDaemonStatus == DaemonStatus.STOPPED ){
                        // if we get here it means that the daemon is *actually* running but the screen is reflecting
                        // as OFF. This is a bad state and we will simply try to stop the daemon and get
                        // back to a consistent state
                        stopDaemonAndSetStatus();
                    }
                    // for mDaemonStatus = STOPPING or RUNNING we don't have to do anything, the next time it refreshes
                    // the right status will get reflected
                    if (!intent.hasExtra(RPCIntentService.PARAM_ONION_MSG))
                        return;
                    final String onion = intent.getStringExtra(RPCIntentService.PARAM_ONION_MSG);
                    if (onion == null)
                        return;
                    mQrCodeText.setText(onion);
                    final ByteMatrix matrix;
                    try {
                        matrix = Encoder.encode(onion, ErrorCorrectionLevel.M).getMatrix();
                    } catch (final WriterException e) {
                        return;
                    }
                    final int height = matrix.getHeight() * SCALE;
                    final int width = matrix.getWidth() * SCALE;
                    final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    for (int x = 0; x < width; ++x)
                        for (int y = 0; y < height; ++y)
                            bitmap.setPixel(x, y, matrix.get(x / SCALE, y / SCALE) == 1 ? Color.BLACK : 0);
                    mImageViewQr.setImageBitmap(bitmap);

                    final int max = 100;
                    final int blocks = intent.getIntExtra("blocks", 0);
                    final int percent = intent.getIntExtra("sync", -1);

                    pb.setMax(max);
                    pb.setProgress(percent);
                    textStatus.setText(getString(R.string.progress_bar_message, percent, blocks));
                    break;
                default:
                    Log.e(TAG, "Unexpected broadcast result - " + text);
            }
        }
    }
}
