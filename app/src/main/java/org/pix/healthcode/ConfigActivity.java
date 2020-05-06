package org.pix.healthcode;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Window;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config);
        prefs = getPreferenceManager().getSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onSharedPreferenceChanged(prefs, "KEY_PROVINCE");
        onSharedPreferenceChanged(prefs, "KEY_CITY");
        onSharedPreferenceChanged(prefs, "KEY_HOTLINE");
        onSharedPreferenceChanged(prefs, "KEY_NAME");
        onSharedPreferenceChanged(prefs, "KEY_ID");
        onSharedPreferenceChanged(prefs, "KEY_CONTENT");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        } else if(pref instanceof ListPreference) {
            ListPreference lp = (ListPreference) pref;
            lp.setSummary(lp.getValue());
        }
    }
}
