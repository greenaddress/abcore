package com.greenaddress.abcore;

import android.util.Log;

import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ProcessLogger extends Thread {

    private final String mTAG;
    private final InputStream mInputStream;
    private final OnError mOnError;

    ProcessLogger(final InputStream inputStream, String name, OnError onErrorCallback) {
        super();
        mTAG = ProcessLogger.class.getSimpleName() + "-" + name;
        this.mInputStream = inputStream;
        this.mOnError = onErrorCallback;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    public void run() {
        try {
            final InputStreamReader isr = new InputStreamReader(mInputStream);
            final BufferedReader br = new BufferedReader(isr);
            String line;
            final String[] errors = new String[3];

            int counter = 0;
            while ((line = br.readLine()) != null) {
                Log.v(mTAG, line);
                errors[counter++ % 3] = line;
            }
            if (mOnError != null)
                mOnError.onError(errors);

        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            IOUtils.closeQuietly(mInputStream);
        }
    }

    interface OnError {
        void onError(String[] error);
    }
}