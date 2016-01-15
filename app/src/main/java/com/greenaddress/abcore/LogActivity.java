package com.greenaddress.abcore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            // Verify if RandomAccessFile is better than linear scanning given the line issues?
            // FIXME: This is not good enough when the file is big, we should use RAF above.
            final File f = new File(Utils.getDataDir(this) + (Utils.isTestnet(this)?"/testnet3/debug.log":"/debug.log"));
            if (!f.exists()) {
                ((EditText) findViewById(R.id.editText))
                        .setText("No debug file exists yet");
                return;
            }

            // only show the last 1000 lines
            final String[] lines = new String[1000];
            int count = 0;
            String line;

            final BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
            while ((line = bufferedReader.readLine()) != null) {
                lines[count % lines.length] = line;
                ++count;
            }

            final StringBuilder sb = new StringBuilder();
            for (final String l: lines) {
                if (l != null){
                    sb.append(l);
                    sb.append("\n");
                }
            }
            final EditText et = (EditText)  findViewById(R.id.editText);
            final String txt = sb.toString();
            et.getText().clearSpans();
            et.getText().clear();
            et.setText(txt);
            et.setSelection(txt.length());
            et.setKeyListener(null);
            IOUtils.closeQuietly(bufferedReader);

        } catch (IOException e) {
        }
    }
}
