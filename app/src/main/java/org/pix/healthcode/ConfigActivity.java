package org.pix.healthcode;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;

import java.util.Arrays;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = "ConfigActivity";
    private SharedPreferences sp;
    private int userIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        userIndex = getIntent().getIntExtra("INDEX", 0);
        String xmlName = "R.xml.config"+userIndex;
        int xmlId = ResourceUtil.getId(this, xmlName);
        addPreferencesFromResource(xmlId);
        sp = getPreferenceManager().getSharedPreferences();
        setTitle(getString(R.string.menu_user) + (userIndex+1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume(userIndex="+userIndex);
        ListPreference listPreference = (ListPreference) findPreference("KEY_COLOR"+userIndex);
        String defColorName = getResources().getStringArray(R.array.code_color_names)[0];
        String colorName = sp.getString("KEY_COLOR"+userIndex, defColorName);
        listPreference.setSummary(colorName);

        listPreference = (ListPreference) findPreference("KEY_PROVINCE"+userIndex);
        String defProvince = getResources().getStringArray(R.array.provinces)[0];
        String province = sp.getString("KEY_PROVINCE"+userIndex, defProvince);
        listPreference.setSummary(province);

        listPreference = (ListPreference) findPreference("KEY_CITY"+userIndex);
        int provinceIndex = Arrays.asList(getResources().getStringArray(R.array.provinces)).indexOf(province);
        String provinceId = getResources().getStringArray(R.array.provinces_id)[provinceIndex];
        int id = ResourceUtil.getId(this, "R.array." + provinceId + "_cities");
        String defCity = getResources().getStringArray(id)[0];
        listPreference.setSummary(sp.getString("KEY_CITY"+userIndex, defCity));
        listPreference.setEntries(id);
        listPreference.setEntryValues(id);

        EditTextPreference editTextPreference = (EditTextPreference) findPreference("KEY_HOTLINE"+userIndex);
        id = ResourceUtil.getId(this, "R.array." + provinceId + "_telcodes");
        String defHotline = getResources().getStringArray(id)[0] + "12345-6";
        editTextPreference.setSummary(sp.getString("KEY_HOTLINE"+userIndex, defHotline));

        editTextPreference = (EditTextPreference) findPreference("KEY_NAME"+userIndex);
        String defaultNameIdName = "R.string.default_name"+userIndex;
        int defaultNameId = ResourceUtil.getId(this, defaultNameIdName);
        editTextPreference.setSummary(sp.getString("KEY_NAME"+userIndex, getString(defaultNameId)));

        editTextPreference = (EditTextPreference) findPreference("KEY_ID"+userIndex);
        String defaultIdIdName = "R.string.default_id"+userIndex;
        int defaultIdId = ResourceUtil.getId(this, defaultIdIdName);
        editTextPreference.setSummary(sp.getString("KEY_ID"+ userIndex, getString(defaultIdId)));

        editTextPreference = (EditTextPreference) findPreference("KEY_CONTENT"+userIndex);
        if(editTextPreference != null) {
            editTextPreference.setSummary(sp.getString("KEY_CONTENT" + userIndex, getString(R.string.default_content)));
            PreferenceCategory category = (PreferenceCategory) findPreference("KEY_OTHER");
            category.removePreference(editTextPreference);
        }

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
            if(key.equals("KEY_PROVINCE"+ userIndex)) {
                String province = lp.getValue();
                int provinceIndex = Arrays.asList(getResources().getStringArray(R.array.provinces)).indexOf(province);
                String provinceId = getResources().getStringArray(R.array.provinces_id)[provinceIndex];
                int id = ResourceUtil.getId(this, "R.array." + provinceId + "_cities");
                ListPreference prefCity = (ListPreference) findPreference("KEY_CITY"+ userIndex);
                prefCity.setEntries(id);
                prefCity.setEntryValues(id);
                prefCity.setValueIndex(0);

                updateHotline(lp.getValue(), 0);

            } else if(key.equals("KEY_CITY"+ userIndex)) {
                ListPreference prefProvince = (ListPreference) findPreference("KEY_PROVINCE"+ userIndex);
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
        EditTextPreference prefHotline = (EditTextPreference) findPreference("KEY_HOTLINE"+userIndex);
        prefHotline.setText(hotline);
    }
}
