package com.greenaddress.abcore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class ConsoleActivity extends AppCompatActivity {

    private RPCResponseReceiver rpcResponseReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        final EditText console = findViewById(R.id.editText2);
        console.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, final int actionId, final KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN) &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    if (event == null || !event.isShiftPressed()) {
                        consoleRequest(console.getText().toString());
                        return true; // consume.
                    }
                return false; // pass on to other listeners.
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(rpcResponseReceiver);
        rpcResponseReceiver = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter filter = new IntentFilter(RPCResponseReceiver.ACTION_RESP);
        if (rpcResponseReceiver == null)
            rpcResponseReceiver = new RPCResponseReceiver();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(rpcResponseReceiver, filter);
    }

    private void consoleRequest(final String cmd) {
        final Intent i = new Intent(this, RPCIntentService.class);
        i.putExtra("CONSOLE_REQUEST", cmd);
        startService(i);
    }

    class RPCResponseReceiver extends BroadcastReceiver {

        static final String ACTION_RESP = "com.greenaddress.intent.action.RPC_PROCESSED";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String text = intent.getStringExtra(RPCIntentService.PARAM_OUT_MSG);
            final EditText history = findViewById(R.id.editText);

            switch (text) {
                case "CONSOLE_REQUEST":
                    final EditText console = findViewById(R.id.editText2);
                    final String res = intent.getStringExtra("res");

                    history.setText(String.format("%s -> %s", console.getText().toString(), res));
                    console.setText("");
                    break;
                case "exception":
                    Snackbar.make(findViewById(android.R.id.content),
                            "Daemon is not running", Snackbar.LENGTH_INDEFINITE).show();
                    break;
            }
        }
    }
}
