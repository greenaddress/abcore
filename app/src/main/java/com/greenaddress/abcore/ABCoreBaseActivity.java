package com.greenaddress.abcore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class ABCoreBaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.configuration:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.peerview:
                startActivity(new Intent(this, PeerActivity.class));
                return true;
            case R.id.debug:
                startActivity(new Intent(this, LogActivity.class));
                return true;
            case R.id.console:
                startActivity(new Intent(this, ConsoleActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void setUpTopNavBar(){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String useDistribution = prefs.getString("usedistribution", "core");
        getSupportActionBar().setSubtitle(getString(R.string.subtitle, useDistribution));
    }
}
