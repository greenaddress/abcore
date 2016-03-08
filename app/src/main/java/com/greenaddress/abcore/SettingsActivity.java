package com.greenaddress.abcore;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

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
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private final static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
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
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || RepoPreferenceFragment.class.getName().equals(fragmentName)
                || CorePreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class RepoPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_mirror);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("debianRepo"));

            final ListPreference armArchRepo = (ListPreference) findPreference("archarmRepo");
            final ListPreference archi386Repo = (ListPreference) findPreference("archi386Repo");
            if (Utils.getArch().equals("amd64") || Utils.getArch().equals("i386")) {
                bindPreferenceSummaryToValue(archi386Repo);
                getPreferenceScreen().removePreference(armArchRepo);

            } else {
                bindPreferenceSummaryToValue(armArchRepo);
                getPreferenceScreen().removePreference(archi386Repo);
            }

            final SwitchPreference arch = (SwitchPreference) findPreference("archisenabled");

            final SwitchPreference debian = (SwitchPreference) findPreference("debianisenabled");

            debian.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {

                    final Preference.OnPreferenceChangeListener l = arch.getOnPreferenceChangeListener();
                    arch.setOnPreferenceChangeListener(null);
                    arch.setChecked(!(Boolean) newValue);
                    arch.setOnPreferenceChangeListener(l);

                    return true;
                }
            });

            arch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {

                    final Preference.OnPreferenceChangeListener l = debian.getOnPreferenceChangeListener();
                    debian.setOnPreferenceChangeListener(null);
                    debian.setChecked(!(Boolean) newValue);
                    debian.setOnPreferenceChangeListener(l);

                    return true;
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CorePreferenceFragment extends PreferenceFragment {

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 21) {
                if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                    final String directory = data
                            .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);

                    final Properties p = new Properties();
                    try {
                        DownloadInstallCoreIntentService.configureCore(getActivity());

                        p.load(new FileInputStream(Utils.getBitcoinConf(getActivity())));
                        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        final SharedPreferences.Editor e = prefs.edit();

                        p.setProperty("datadir", directory);
                        e.putString("datadir", directory);
                        e.apply();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        p.store(new FileOutputStream(Utils.getBitcoinConf(getActivity())), "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    findPreference("datadir").setSummary(directory);
                    new File(directory).mkdir();

                }
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            final Properties p = new Properties();
            try {
                p.load(new FileInputStream(Utils.getBitcoinConf(getActivity())));

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                final SharedPreferences.Editor e = prefs.edit();

                e.putBoolean("testnet", p.getProperty("testnet", "0").equals("1"));
                e.putBoolean("upnp", p.getProperty("upnp", "0").equals("1"));
                e.putBoolean("disablewallet", p.getProperty("disablewallet", "0").equals("1"));

                e.putString("datadir", p.getProperty("datadir", Utils.getDataDir(getActivity())));
                e.apply();

            } catch (IOException e) {
                e.printStackTrace();
            }

            final Preference.OnPreferenceChangeListener ps = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                    p.setProperty(preference.getKey(), (Boolean) newValue ? "1" : "0");
                    try {
                        p.store(new FileOutputStream(Utils.getBitcoinConf(getActivity())), "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;

                }
            };
            addPreferencesFromResource(R.xml.pref_conf);
            findPreference("testnet").setOnPreferenceChangeListener(ps);
            findPreference("upnp").setOnPreferenceChangeListener(ps);
            findPreference("disablewallet").setOnPreferenceChangeListener(ps);
            findPreference("datadir").setSummary(p.getProperty("datadir", Utils.getDataDir(getActivity())));
            findPreference("datadir").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Intent chooserIntent = new Intent(getActivity(), DirectoryChooserActivity.class);
                    final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                            .initialDirectory(Utils.getLargestFilesDir(getActivity()).getAbsolutePath())
                            .newDirectoryName("./bitcoin")
                            .allowReadOnlyDirectory(true)
                            .allowNewDirectoryNameModification(true)
                            .build();

                    chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

                    startActivityForResult(chooserIntent, 21);
                    return false;
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
