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
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();
    private RPCResponseReceiver mRpcResponseReceiver;
    private TextView mTvStatus;
    private Switch mSwitchCore;
    private TextView mQrCodeText;
    private ImageView mImageViewQr;
    private final static int SCALE = 4;
    private Timer mTimer;


    private void postStart() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final String useDistribution = prefs.getString("usedistribution", "core");
        mTvStatus.setText(getString(R.string.runningturnoff, useDistribution, "knots".equals(useDistribution) ? Packages.BITCOIN_KNOTS_NDK : "liquid".equals(useDistribution) ? Packages.BITCOIN_LIQUID_NDK : Packages.BITCOIN_NDK));
        mSwitchCore.setText(R.string.switchcoreoff);
        if (!mSwitchCore.isChecked()) {
            mSwitchCore.setOnCheckedChangeListener(null);
            mSwitchCore.setChecked(true);
            setSwitch();
        }
    }
    private void refresh() {
        final Intent i = new Intent(this, RPCIntentService.class);
        i.putExtra("REQUEST", "localonion");
        startService(i);
    }

    private void postConfigure() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String useDistribution = prefs.getString("usedistribution", "core");
        mTvStatus.setText(getString(R.string.stoppedturnon, useDistribution, "knots".equals(useDistribution) ? Packages.BITCOIN_KNOTS_NDK : "liquid".equals(useDistribution) ? Packages.BITCOIN_LIQUID_NDK : Packages.BITCOIN_NDK));
        mSwitchCore.setText(R.string.switchcoreon);
        if (mSwitchCore.isChecked()) {
            mSwitchCore.setOnCheckedChangeListener(null);
            mSwitchCore.setChecked(false);
            setSwitch();
        }
    }

    private void setSwitch() {
        mSwitchCore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    final SharedPreferences.Editor e = prefs.edit();
                    e.putBoolean("magicallystarted", false);
                    e.apply();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(MainActivity.this, ABCoreService.class));
                    } else {
                        startService(new Intent(MainActivity.this, ABCoreService.class));
                    }
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer.purge();
                    }
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                }
                            });
                        }
                    }, 1000, 1000);
                } else {
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
        final Toolbar toolbar = findViewById(R.id.toolbar);
        mTvStatus = findViewById(R.id.textView);
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
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Copied to clipboard!", Toast.LENGTH_LONG).show();
            }
        };
        mImageViewQr.setOnClickListener(cliboard);
        mQrCodeText.setOnClickListener(cliboard);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRpcResponseReceiver != null)
            unregisterReceiver(mRpcResponseReceiver);
        mRpcResponseReceiver = null;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
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
                    break;
                case "localonion":
                    final String onion = intent.getStringExtra(RPCIntentService.PARAM_ONION_MSG);
                    if (onion != null && mTimer != null) {
                        mTimer.cancel();
                        mTimer.purge();
                    }
                    mQrCodeText.setText(onion);
                    final ByteMatrix matrix;
                    try {
                        matrix = Encoder.encode(onion, ErrorCorrectionLevel.M).getMatrix();
                    } catch (final WriterException e) {
                        throw new RuntimeException(e);
                    }
                    final int height = matrix.getHeight() * SCALE;
                    final int width = matrix.getWidth() * SCALE;
                    final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    for (int x = 0; x < width; ++x)
                        for (int y = 0; y < height; ++y)
                            bitmap.setPixel(x, y, matrix.get(x / SCALE, y / SCALE) == 1 ? Color.BLACK : 0);
                    mImageViewQr.setImageBitmap(bitmap);
            }
        }
    }
}
