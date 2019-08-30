package com.greenaddress.abcore;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import androidx.appcompat.app.ActionBar;
/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private final static String TAG = SettingsActivity.class.getName();

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(final Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static void deleteRF(final File f) {

        Log.v(TAG, "Deleting " + f.getAbsolutePath() + "/" + f.getName());
        if (f.isDirectory())
            for (File child : f.listFiles())
                deleteRF(child);

        //noinspection ResultOfMethodCallIgnored
        f.delete();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(final List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(final String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || AndroidPreferenceFragment.class.getName().equals(fragmentName)
                || CorePreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CorePreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            final Properties p = new Properties();
            try {

                p.load(new FileInputStream(Utils.getBitcoinConf(getActivity())));

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                final SharedPreferences.Editor e = prefs.edit();

                e.putBoolean("restartOnBoot", p.getProperty("restartOnBoot", "0").equals("1"));
                e.putBoolean("testnet", p.getProperty("testnet", "0").equals("1"));
                e.putBoolean("upnp", p.getProperty("upnp", "0").equals("1"));
                e.putBoolean("blocksonly", p.getProperty("blocksonly", "0").equals("1"));
                e.putBoolean("disablewallet", p.getProperty("disablewallet", "0").equals("1"));
                e.putString("datadir", p.getProperty("datadir", Utils.getDataDir(getActivity())));
                if (p.containsKey("prune")) {
                    e.putString("prune", p.getProperty("prune"));
                    e.putBoolean("pruning", true);
                } else
                    e.putBoolean("pruning", false);

                e.apply();

            } catch (final IOException e) {
                e.printStackTrace();
            }

            final Preference.OnPreferenceChangeListener ps = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    p.setProperty(preference.getKey(), (Boolean) newValue ? "1" : "0");
                    try {
                        p.store(new FileOutputStream(Utils.getBitcoinConf(getActivity())), "");
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            };
            addPreferencesFromResource(R.xml.pref_conf);
            findPreference("testnet").setOnPreferenceChangeListener(ps);
            findPreference("upnp").setOnPreferenceChangeListener(ps);
            findPreference("blocksonly").setOnPreferenceChangeListener(ps);
            findPreference("disablewallet").setOnPreferenceChangeListener(ps);
            findPreference("restartOnBoot").setOnPreferenceChangeListener(ps);
            findPreference("datadir").setSummary(p.getProperty("datadir", Utils.getDataDir(getActivity())));
            findPreference("prune").setSummary(p.getProperty("prune", "1000"));
            findPreference("prune").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object o) {
                    p.setProperty("prune", o.toString());
                    preference.setSummary(o.toString());
                    try {

                        p.store(new FileOutputStream(Utils.getBitcoinConf(getActivity())), "");
                    } catch (final IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
            });
            findPreference("pruning").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object o) {
                    if (!(Boolean) o)
                        p.remove("prune");
                    else {
                        p.setProperty("prune", p.getProperty("prune", "1000"));
                        findPreference("prune").setSummary(p.getProperty("prune", "1000"));
                    }

                    try {
                        p.store(new FileOutputStream(Utils.getBitcoinConf(getActivity())), "");
                    } catch (final IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(final MenuItem item) {
            final int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AndroidPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            addPreferencesFromResource(R.xml.pref_android);

            findPreference("deletecore").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final File dir = Utils.getDir(getActivity());
                    deleteRF(new File(dir, "shachecks"));
                    deleteRF(new File(dir, "bitcoind"));
                    deleteRF(new File(dir, "liquidd"));
                    getActivity().finish();
                    return true;
                }
            });

            findPreference("deletecoredatadir").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    deleteRF(new File(Utils.getDataDir(getActivity())));
                    try {
                        DownloadInstallCoreIntentService.configureCore(getActivity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getActivity().finish();
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(final MenuItem item) {
            final int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
