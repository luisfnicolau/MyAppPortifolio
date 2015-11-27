package com.example.android.myappportifolio.PopularMovies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.example.android.myappportifolio.R;

/**
 * Created by Luis on 10/28/2015.
 */
public class PopularMoviesSettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_headers_popular_movies);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_unit_key_popular_movies)));
    }


    private void bindPreferenceSummaryToValue(Preference preference) {
        //Set listener to watch for values changes
        preference.setOnPreferenceChangeListener(this);

        //Trigger the listener with the current preference value
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
        // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(getApplicationContext(), PopularMoviesMain.class);
        startActivity(intent);
    }
}
