package com.greenaddress.abcore;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.compress.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BitcoinConfEditActivity extends AppCompatActivity {
    private final static String TAG = BitcoinConfEditActivity.class.getName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin_conf_edit);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save file
        OutputStream f = null;
        try {
            f = new FileOutputStream(Utils.getBitcoinConf(this));
            IOUtils.copy(new ByteArrayInputStream(((EditText) findViewById(R.id.editText))
                    .getText().toString().getBytes(StandardCharsets.UTF_8)), f);

        } catch (final IOException e) {
            Log.i(TAG, e.getMessage());
        } finally {
            IOUtils.closeQuietly(f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load file
        try {
            DownloadInstallCoreIntentService.configureCore(this);
            final InputStream f = new FileInputStream(Utils.getBitcoinConf(this));
            ((EditText) findViewById(R.id.editText))
                    .setText(new String(IOUtils.toByteArray(
                            f)));
            IOUtils.closeQuietly(f);
        } catch (final IOException e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
