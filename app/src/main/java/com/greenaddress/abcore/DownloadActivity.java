package com.greenaddress.abcore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class DownloadActivity extends AppCompatActivity {
    private final static String TAG = DownloadActivity.class.getName();
    private DownloadInstallCoreResponseReceiver mDownloadInstallCoreResponseReceiver;
    private ProgressBar mPB;
    private Button mButtonCore;
    private Button mButtonKnots;
    private Button mButtonLiquid;
    private TextView mTvStatus;
    private TextView mTvDetails;
    private View mContent;

    private static String niceFlat(final float f, final String s) {
        if ((int) f == f)
            return String.format("@ %s %s", f, s);
        return String.format(Locale.US, "@ %.2f %s", f, s);
    }

    private static String getSpeed(final int bytesPerSec) {
        if (bytesPerSec == 0)
            return "";

        if (bytesPerSec >= 1024 * 1024 * 1024)
            return niceFlat((float) bytesPerSec / (1024 * 1024 * 1024), "GB/s");

        if (bytesPerSec >= 1024 * 1024)
            return niceFlat((float) bytesPerSec / (1024 * 1024), "MB/s");

        if (bytesPerSec >= 1024)
            return String.format("@ %s KB/s", bytesPerSec / 1024);

        return String.format("@ %s B/s", bytesPerSec);
    }

    private void showSnackMsg(final String msg) {
        if (msg != null && !msg.trim().isEmpty())
            Snackbar.make(mContent, msg, Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mPB = findViewById(R.id.progressBar);
        mTvStatus = findViewById(R.id.textView);
        mButtonCore = findViewById(R.id.buttonCore);
        mButtonKnots = findViewById(R.id.buttonKnots);
        mButtonLiquid = findViewById(R.id.buttonLiquid);
        mTvDetails = findViewById(R.id.textViewDetails);
        mContent = findViewById(android.R.id.content);

        try {
            Utils.getArch();
        } catch (final Utils.ABIsUnsupported e) {
            mButtonCore.setEnabled(false);
            mButtonKnots.setEnabled(false);
            mButtonLiquid.setEnabled(false);
            final String msg = getString(R.string.abis_unsupported, TextUtils.join(",", Build.SUPPORTED_ABIS));
            mTvStatus.setText(msg);
            showSnackMsg(msg);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mDownloadInstallCoreResponseReceiver);
        mDownloadInstallCoreResponseReceiver = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Utils.isDaemonInstalled(this))
            finish();

        final IntentFilter downloadFilter = new IntentFilter(DownloadInstallCoreResponseReceiver.ACTION_RESP);
        if (mDownloadInstallCoreResponseReceiver == null)
            mDownloadInstallCoreResponseReceiver = new DownloadInstallCoreResponseReceiver();
        downloadFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mDownloadInstallCoreResponseReceiver, downloadFilter);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor e = prefs.edit();

        mButtonCore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                e.putString("usedistribution","core");
                e.apply();
                mPB.setVisibility(View.VISIBLE);
                startService(new Intent(DownloadActivity.this, DownloadInstallCoreIntentService.class));
                disableWhileDownloading();
            }
        });

        mButtonKnots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                e.putString("usedistribution","knots");
                e.apply();
                mPB.setVisibility(View.VISIBLE);
                startService(new Intent(DownloadActivity.this, DownloadInstallCoreIntentService.class));
                disableWhileDownloading();
            }
        });

        mButtonLiquid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                e.putString("usedistribution","liquid");
                e.apply();
                mPB.setVisibility(View.VISIBLE);
                startService(new Intent(DownloadActivity.this, DownloadInstallCoreIntentService.class));
                disableWhileDownloading();
            }
        });


        if (DownloadInstallCoreIntentService.HAS_BEEN_STARTED)
            disableWhileDownloading();
    }

    private void disableWhileDownloading() {
        mButtonCore.setEnabled(false);
        mButtonKnots.setEnabled(false);
        mButtonLiquid.setEnabled(false);
        mTvStatus.setText(R.string.waitfetchingconfiguring);
    }

    public class DownloadInstallCoreResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.greenaddress.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String text = intent.getStringExtra(DownloadInstallCoreIntentService.PARAM_OUT_MSG);
            switch (text) {
                case "OK":
                    finish();
                    break;
                case "exception":
                    final String exe = intent.getStringExtra("exception");
                    Log.i(TAG, exe);
                    mPB.setProgress(0);
                    mPB.setVisibility(View.GONE);
                    mTvDetails.setText(exe);
                    mButtonCore.setEnabled(true);
                    mButtonKnots.setEnabled(true);
                    mButtonLiquid.setEnabled(true);
                    mTvStatus.setText(R.string.failedretry);
                    break;
                case "ABCOREUPDATE":

                    mTvDetails.setText(String.format("%s %s", intent.getStringExtra("ABCOREUPDATETXT"), getSpeed(intent.getIntExtra("ABCOREUPDATESPEED", 0))));

                    mPB.setMax(intent.getIntExtra("ABCOREUPDATEMAX", 100));
                    mPB.setProgress(intent.getIntExtra("ABCOREUPDATE", 0));
                    mPB.setVisibility(View.VISIBLE);

                    break;
            }
        }
    }
}
