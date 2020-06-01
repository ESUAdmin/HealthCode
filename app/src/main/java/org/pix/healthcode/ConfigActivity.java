package org.pix.healthcode;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = "ConfigActivity";
    private SharedPreferences sharedPrefs;
    private int userIndex;
    private PrefsConfig cfg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cfg = new PrefsConfig(this);
        sharedPrefs = getPreferenceManager().getSharedPreferences();
        userIndex = getIntent().getIntExtra("INDEX", 0);
        String xmlName = "R.xml.config"+userIndex;
        int xmlId = ResourceUtil.getId(this, xmlName);
        addPreferencesFromResource(xmlId);
        String title = String.format(getString(R.string.menu_edit_user), (userIndex+1));
        setTitle(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume(userIndex="+userIndex);
        ListPreference listPreference = (ListPreference) findPreference("KEY_PROVINCE"+userIndex);
        listPreference.setSummary(cfg.getProvince(userIndex));

        listPreference = (ListPreference) findPreference("KEY_CITY"+userIndex);
        listPreference.setSummary(cfg.getCity(userIndex));
        int resId = cfg.getCitiesResId(userIndex);
        listPreference.setEntries(resId);
        listPreference.setEntryValues(resId);

        EditTextPreference editTextPreference = (EditTextPreference) findPreference("KEY_HOTLINE"+userIndex);
        editTextPreference.setSummary(cfg.getHotline(userIndex));

        editTextPreference = (EditTextPreference) findPreference("KEY_NAME"+userIndex);
        editTextPreference.setSummary(cfg.getUserName(userIndex));

        editTextPreference = (EditTextPreference) findPreference("KEY_ID"+userIndex);
        editTextPreference.setSummary(cfg.getUserId(userIndex));

        editTextPreference = (EditTextPreference) findPreference("KEY_CONTENT"+userIndex);
        if(editTextPreference != null) {
            String content = sharedPrefs.getString("KEY_CONTENT"+userIndex, getString(R.string.default_content));
            editTextPreference.setSummary(content);
            PreferenceCategory category = (PreferenceCategory) findPreference("KEY_OTHER");
            category.removePreference(editTextPreference);
        }

        listPreference = (ListPreference) findPreference("KEY_COLOR"+userIndex);
        listPreference.setSummary(cfg.getColorName(userIndex));

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
            lp.setSummary(lp.getEntry());
            if(key.equals("KEY_PROVINCE"+userIndex)) {
                cfg.resetCity(userIndex);
                ListPreference prefCity = (ListPreference) findPreference("KEY_CITY"+userIndex);
                int resId = cfg.getCitiesResId(userIndex);
                prefCity.setEntries(resId);
                prefCity.setEntryValues(resId);
                prefCity.setValueIndex(0);
                prefCity.setSummary(cfg.getCity(userIndex));

                cfg.resetHotline(userIndex);
                EditTextPreference prefHotline = (EditTextPreference) findPreference("KEY_HOTLINE"+userIndex);
                String hotline = cfg.getHotline(userIndex);
                prefHotline.setText(hotline);
                prefHotline.setSummary(hotline);

            } else if(key.equals("KEY_CITY"+userIndex)) {
                cfg.resetHotline(userIndex);
                EditTextPreference prefHotline = (EditTextPreference) findPreference("KEY_HOTLINE"+userIndex);
                String hotline = cfg.getHotline(userIndex);
                prefHotline.setText(hotline);
                prefHotline.setSummary(hotline);
            }
        }
    }

}
