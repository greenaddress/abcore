package com.greenaddress.abcore;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LogActivity extends AppCompatActivity {

    private static final int LOOP_DELAY = 600;

    private final Handler mMsgHandler = new Handler();

    private final Runnable taskLoopRunnable = new Runnable() {
        @Override
        public void run() {
            runLogTask();
        }
    };

    private File mLogFile;

    private UpdateLogTask mUpdateLogTask;

    private final LogTaskCallback mLogTaskCallback = new LogTaskCallback() {
        @Override
        public void onLogFileRead(String txt) {
            final EditText et = findViewById(R.id.editText);
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
            }
            mUpdateLogTask = null;
            mMsgHandler.postDelayed(taskLoopRunnable, LOOP_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String useDistribution = prefs.getString("usedistribution", "core");

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(getString(R.string.subtitle, useDistribution));
        setSupportActionBar(toolbar);

        final String daemon = "liquid".equals(useDistribution) ? "/liquidv1/debug.log" : "/debug.log";
        mLogFile = new File(Utils.getDataDir(this) + (Utils.isTestnet(this) ? "/testnet3/debug.log" : daemon));
    }

    private void runLogTask() {
        mUpdateLogTask = new UpdateLogTask();
        mUpdateLogTask.setTaskCallback(mLogTaskCallback);
        mUpdateLogTask.execute(mLogFile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runLogTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMsgHandler.removeCallbacks(taskLoopRunnable);
        if (mUpdateLogTask != null) {
            mUpdateLogTask.setTaskCallback(null);
        }
    }

    private static String getLastLines(final File file, final int lines) {
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

    private static class UpdateLogTask extends AsyncTask<File, Void, String> {

        private LogTaskCallback mTaskCallback;

        @Override
        protected String doInBackground(File... files) {
            return getLogsFromFile(files[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mTaskCallback != null) {
                mTaskCallback.onLogFileRead(result);
            }
        }

        void setTaskCallback(LogTaskCallback taskCallback) {
            mTaskCallback = taskCallback;
        }

        private String getLogsFromFile(File file) {

            if (!file.exists()) {
                return "No debug file exists yet";
            }

            for (int lines = 1000; lines > 0; --lines) {
                final String txt = LogActivity.getLastLines(file, lines);
                if (txt != null) {
                    return txt;
                }
            }
            return "Failed to get logs.";
        }
    }

    private interface LogTaskCallback {
        void onLogFileRead(String logs);
    }

}
