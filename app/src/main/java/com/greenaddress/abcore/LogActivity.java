package com.greenaddress.abcore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String useDistribution = prefs.getString("usedistribution", "core");
        getSupportActionBar().setSubtitle(getString(R.string.subtitle, useDistribution));
    }

    private String getLastLines(final File file, final int lines) {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new RandomAccessFile(file, "r");
            final long fileLength = fileHandler.length() - 1;
            final StringBuilder sb = new StringBuilder();
            int line = 0;

            for (long filePointer = fileLength; filePointer != -1; --filePointer) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer < fileLength)
                        ++line;
                } else if (readByte == 0xD) {
                    if (filePointer < fileLength - 1)
                        ++line;
                }

                if (line >= lines)
                    break;
                sb.append((char) readByte);
            }

            return sb.reverse().toString();
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileHandler != null)
                try {
                    fileHandler.close();
                } catch (final IOException ignored) {
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String useDistribution = prefs.getString("usedistribution", "core");
        final String daemon = "liquid".equals(useDistribution) ? "/liquidv1/debug.log" : "/debug.log";

        final File f = new File(Utils.getDataDir(this) + (Utils.isTestnet(this) ? "/testnet3/debug.log" : daemon));
        if (!f.exists()) {
            ((EditText) findViewById(R.id.editText))
                    .setText("No debug file exists yet");
            return;
        }

        final EditText et = findViewById(R.id.editText);
        for (int lines = 1000; lines > 0; --lines) {
            final String txt = getLastLines(f, lines);
            if (txt != null) {
                et.getText().clearSpans();
                et.getText().clear();
                et.setText(txt);
                try {
                    et.setSelection(txt.length());
                } catch (final IndexOutOfBoundsException e) {
                    // pass
                    // FIXME: Scroll to bottom doesn't work for some mobile (LG)
                }
                et.setKeyListener(null);
                return;
            }
        }
    }

}
