package org.pix.healthcode;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import java.util.Arrays;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config);
        sp = getPreferenceManager().getSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListPreference listPreference = (ListPreference) findPreference("KEY_COLOR");
        String defColorName = getResources().getStringArray(R.array.code_color_names)[0];
        String colorName = sp.getString("KEY_COLOR", defColorName);
        listPreference.setSummary(colorName);

        listPreference = (ListPreference) findPreference("KEY_PROVINCE");
        String defProvince = getResources().getStringArray(R.array.provinces)[0];
        String province = sp.getString("KEY_PROVINCE", defProvince);
        listPreference.setSummary(province);

        listPreference = (ListPreference) findPreference("KEY_CITY");
        int provinceIndex = Arrays.asList(getResources().getStringArray(R.array.provinces)).indexOf(province);
        String provinceId = getResources().getStringArray(R.array.provinces_id)[provinceIndex];
        int id = ResourceUtil.getId(this, "R.array." + provinceId + "_cities");
        String defCity = getResources().getStringArray(id)[0];
        listPreference.setSummary(sp.getString("KEY_CITY", defCity));
        listPreference.setEntries(id);
        listPreference.setEntryValues(id);

        EditTextPreference editTextPreference = (EditTextPreference) findPreference("KEY_HOTLINE");
        id = ResourceUtil.getId(this, "R.array." + provinceId + "_telcodes");
        String defHotline = getResources().getStringArray(id)[0] + "12345-6";
        editTextPreference.setSummary(sp.getString("KEY_HOTLINE", defHotline));

        editTextPreference = (EditTextPreference) findPreference("KEY_NAME");
        editTextPreference.setSummary(sp.getString("KEY_NAME", getString(R.string.default_name)));

        editTextPreference = (EditTextPreference) findPreference("KEY_ID");
        editTextPreference.setSummary(sp.getString("KEY_ID", getString(R.string.default_id)));

        editTextPreference = (EditTextPreference) findPreference("KEY_CONTENT");
        editTextPreference.setSummary(sp.getString("KEY_CONTENT", getString(R.string.default_content)));

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
            if(key.equals("KEY_PROVINCE")) {
                String province = lp.getValue();
                int provinceIndex = Arrays.asList(getResources().getStringArray(R.array.provinces)).indexOf(province);
                String provinceId = getResources().getStringArray(R.array.provinces_id)[provinceIndex];
                int id = ResourceUtil.getId(this, "R.array." + provinceId + "_cities");
                ListPreference prefCity = (ListPreference) findPreference("KEY_CITY");
                prefCity.setEntries(id);
                prefCity.setEntryValues(id);
                prefCity.setValueIndex(0);

                updateHotline(lp.getValue(), 0);

            } else if(key.equals("KEY_CITY")) {
                ListPreference prefProvince = (ListPreference) findPreference("KEY_PROVINCE");
                updateHotline(prefProvince.getValue(), lp.findIndexOfValue(lp.getValue()));
            }
        }
    }

    private void updateHotline(String province, int index) {
        int provinceIndex = Arrays.asList(getResources().getStringArray(R.array.provinces)).indexOf(province);
        String provinceId = getResources().getStringArray(R.array.provinces_id)[provinceIndex];
        int id = ResourceUtil.getId(this, "R.array." + provinceId + "_telcodes");
        String[] telcodeArray = getResources().getStringArray(id);
        String hotline = telcodeArray[index];
        if(!hotline.startsWith("+")) {
            hotline = hotline.concat("-12345-6");
        }
        EditTextPreference prefHotline = (EditTextPreference) findPreference("KEY_HOTLINE");
        prefHotline.setText(hotline);
    }
}
