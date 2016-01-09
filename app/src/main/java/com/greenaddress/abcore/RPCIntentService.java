package com.greenaddress.abcore;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;


public class RPCIntentService extends IntentService {

    public RPCIntentService() {
        super(RPCIntentService.class.getName());
    }

    Properties getBitcoinConf() throws IOException {
        final Properties p = new Properties();
        final InputStream i = new BufferedInputStream(new FileInputStream(Utils.getBitcoinConf(this)));
        try {p.load(i);} finally {IOUtils.closeQuietly(i);}
        return p;
    }

    BitcoinJSONRPCClient getRpc() throws IOException {

        final Properties p = getBitcoinConf();
        final String user = p.getProperty("rpcuser", "bitcoinrpc");
        final String password = p.getProperty("rpcpassword");
        final String host = p.getProperty("rpcconnect", "127.0.0.1");
        final String port = p.getProperty("rpcport");

        final String testnet = p.getProperty("testnet");

        final String nonMainnet = testnet == null || !testnet.equals("1") ?  p.getProperty("regtest"): testnet;

        final String url = "http://" + user + ':' + password + "@" + host + ":" + (port == null ? "8332" : port) + "/";
        final String testUrl = "http://" + user + ':' + password + "@" + host + ":" + (port == null ? "18332" : port) + "/";

        return new BitcoinJSONRPCClient(nonMainnet == null || !nonMainnet.equals("1")? url : testUrl);
    }

    private static final String TAG = RPCIntentService.class.getName();

    public static final String PARAM_OUT_MSG = "rpccore";

    private void broadcastPeerlist() throws IOException {
        final BitcoindRpcClient bitcoin = getRpc();

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.RPCResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, "peerlist");

        final List<BitcoindRpcClient.PeerInfoResult> pir = bitcoin.getPeerInfo();
        final ArrayList<String> peers = new ArrayList<>();
        // find the most common blockchain height that is higher than hardcoded constant
        for (final BitcoindRpcClient.PeerInfoResult r : pir) {
            peers.add(String.format("%s - %s - %s", r.getAddr(), r.getSubVer(),r.getStartingHeight()));
        }
        broadcastIntent.putStringArrayListExtra("peerlist", peers);

        sendBroadcast(broadcastIntent);

    }

    private void broadcastProgress() throws IOException {
        final BitcoindRpcClient bitcoin = getRpc();

        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.RPCResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, "progress");

        final List<BitcoindRpcClient.PeerInfoResult> pir = bitcoin.getPeerInfo();
        // FIXME: find the most common blockchain height that is higher than hardcoded constant
        int max = -1;
        for (final BitcoindRpcClient.PeerInfoResult r : pir) {
            final int h = r.getStartingHeight();
            if (h > max && h != 0) {
                max = h;
            }
        }

        broadcastIntent.putExtra("max", max);
        broadcastIntent.putExtra("sync", bitcoin.getBlockCount());

        sendBroadcast(broadcastIntent);

    }

    private void broadcastError(final Exception e) {
        Log.e(TAG, e.getClass().getName());
        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.RPCResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, "exception");
        broadcastIntent.putExtra("exception", e.getMessage());
        sendBroadcast(broadcastIntent);
    }


    @Override
    protected void onHandleIntent(final Intent intent) {

        if (intent.getStringExtra("stop") != null) {
            try {
                getRpc().query("stop");
            } catch (final BitcoinRPCException | IOException e) {
                broadcastError(e);
            }
            return;
        }

        final String request = intent.getStringExtra("REQUEST");


        try {

            if (request != null) {
                if (request.equals("peerlist")) {
                    broadcastPeerlist();
                    return;
                } else if (request.equals("progress")) {
                    broadcastProgress();
                    return;
                }
            }

            final BitcoindRpcClient bitcoin = getRpc();

            Log.i(TAG, "" + bitcoin.getEstimateFee(1));

            final Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.RPCResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, "OK");

            broadcastIntent.putExtra("FEE", "" + bitcoin.getEstimateFee(1));
            broadcastIntent.putExtra("INFO", "" + bitcoin.getInfo());


            final List<BitcoindRpcClient.PeerInfoResult> pir = bitcoin.getPeerInfo();
            // find the most common blockchain height that is higher than hardcoded constant
            for (final BitcoindRpcClient.PeerInfoResult r : pir) {
                Log.i(TAG, "Blocks synched " + r.getSyncedBlocks());
                Log.i(TAG, "Blocks starting " + r.getStartingHeight()); /*winner is*/
                Log.i(TAG, "Blocks headers " + r.getSyncedHeaders());
            }

            Log.i(TAG, "Blocks " + bitcoin.getInfo().blocks());

            sendBroadcast(broadcastIntent);

        } catch (final BitcoinRPCException | IOException i) {
            broadcastError(i);
        }
    }
}
