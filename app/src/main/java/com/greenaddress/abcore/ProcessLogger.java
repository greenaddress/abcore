package com.greenaddress.abcore;

import android.util.Log;

import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ProcessLogger extends Thread {

    private final static String TAG = ProcessLogger.class.getName();
    private final InputStream is;
    private final OnError er;

    ProcessLogger(final InputStream is, OnError er) {
        super();
        this.is = is;
        this.er = er;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    public void run() {
        try {
            final InputStreamReader isr = new InputStreamReader(is);
            final BufferedReader br = new BufferedReader(isr);
            String line;
            final String[] errors = new String[3];

            int counter = 0;
            while ((line = br.readLine()) != null) {
                Log.v(TAG, line);
                errors[counter++ % 3] = line;
            }
            if (er != null) {
                er.OnError(errors);
            }

        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    interface OnError {
        void OnError(String[] error);
    }
}