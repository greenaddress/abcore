package com.greenaddress.abcore;

import android.util.Log;

import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ProcessLogger extends Thread {

    final InputStream is;

    final static String TAG = ProcessLogger.class.getName();

    ProcessLogger(InputStream is) {
        super();
        this.is = is;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    public void run() {
        try {
            final InputStreamReader isr = new InputStreamReader(is);
            final BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                Log.v(TAG, line);
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
