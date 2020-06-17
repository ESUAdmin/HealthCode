package org.pix.healthcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

public final class PrefsConfig {
    private Context context;
    private SharedPreferences sharedPrefs;

    public PrefsConfig(Context context) {
        super();
        this.context = context;
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private int getProvinceIndex(int userIndex) {
        String[] provinceValues = context.getResources().getStringArray(R.array.province_values);
        String defProvinceValue = provinceValues[0];
        String provinceValue = sharedPrefs.getString("KEY_PROVINCE"+userIndex, defProvinceValue);
        List<String> provinceValueList = Arrays.asList(provinceValues);
        int index = provinceValueList.indexOf(provinceValue);
        if(index != -1) {
            return index;
        } else {
            String[] provinceNameValues = context.getResources().getStringArray(R.array.provinces);
            List<String> provinceNameValueList = Arrays.asList(provinceNameValues);
            index = provinceNameValueList.indexOf(provinceValue);
            if(index == -1) {
                index = 0;
            }
            sharedPrefs.edit().putString("KEY_PROVINCE"+userIndex, provinceValues[index]).apply();
            return index;
        }
    }
    private String getProvinceValue(int userIndex) {
        String[] provinceValues = context.getResources().getStringArray(R.array.province_values);
        String defProvinceValue = provinceValues[0];
        String provinceValue = sharedPrefs.getString("KEY_PROVINCE"+userIndex, defProvinceValue);
        List<String> provinceValueList = Arrays.asList(provinceValues);
        int index = provinceValueList.indexOf(provinceValue);
        if(index != -1) {
            return provinceValue;
        } else {
            String[] provinceNameValues = context.getResources().getStringArray(R.array.provinces);
            List<String> provinceNameValueList = Arrays.asList(provinceNameValues);
            index = provinceNameValueList.indexOf(provinceValue);
            if(index == -1) {
                index = 0;
            }
            sharedPrefs.edit().putString("KEY_PROVINCE"+userIndex, provinceValues[index]).apply();
            return provinceValues[index];
        }
    }

    public String getProvince(int userIndex) {
        String[] provinces = context.getResources().getStringArray(R.array.provinces);
        int provinceIndex = getProvinceIndex(userIndex);
        return provinces[provinceIndex];
    }
    public String getProvince() {
        return getProvince(getUserIndex());
    }

    public void resetCity(int userIndex) {
        int citiesResId = getCitiesResId(userIndex);
        String[] cities = context.getResources().getStringArray(citiesResId);
        String defCity = cities[0];
        sharedPrefs.edit().putString("KEY_CITY"+userIndex, defCity).apply();
    }
    public String getCity(int userIndex) {
        int citiesResId = getCitiesResId(userIndex);
        String[] cities = context.getResources().getStringArray(citiesResId);
        String defCity = cities[0];
        return sharedPrefs.getString("KEY_CITY"+userIndex, defCity);
    }
    public String getCity() {
        return getCity(getUserIndex());
    }

    public int getCitiesResId(int userIndex) {
        String citiesResIdName = "R.array."+getProvinceValue(userIndex)+"_cities";
        return ResourceUtil.getId(context, citiesResIdName);
    }
    public int getCitiesResId() {
        return getCitiesResId(getUserIndex());
    }

    private int getCityIndex(int userIndex) {
        int citiesResId = getCitiesResId(userIndex);
        String[] cities = context.getResources().getStringArray(citiesResId);
        String defCityValue = cities[0];
        String cityValue = sharedPrefs.getString("KEY_CITY"+userIndex, defCityValue);
        List<String> cityValueList = Arrays.asList(cities);
        int cityIndex = cityValueList.indexOf(cityValue);
        if(cityIndex == -1) {
            resetCity(userIndex);
            cityIndex = 0;
        }
        return cityIndex;
    }

    public void resetHotline(int userIndex) {
        int telcodesResId = getTelcodesResId(userIndex);
        String[] telcodes = context.getResources().getStringArray(telcodesResId);
        int cityIndex = getCityIndex(userIndex);
        String defHotline = telcodes[cityIndex];
        if(!defHotline.startsWith("+")) {
            defHotline = defHotline.concat("-12345-6");
        }
        sharedPrefs.edit().putString("KEY_HOTLINE"+userIndex, defHotline).apply();
    }
    public String getHotline(int userIndex) {
        int telcodesResId = getTelcodesResId(userIndex);
        String[] telcodes = context.getResources().getStringArray(telcodesResId);
        int cityIndex = getCityIndex(userIndex);
        String defHotline = telcodes[cityIndex];
        if(!defHotline.startsWith("+")) {
            defHotline = defHotline.concat("-12345-6");
        }
        return sharedPrefs.getString("KEY_HOTLINE"+userIndex, defHotline);
    }
    public String getHotline() {
        return getHotline(getUserIndex());
    }

    public int getTelcodesResId(int userIndex) {
        String telcodesResIdName = "R.array."+getProvinceValue(userIndex)+"_telcodes";
        return ResourceUtil.getId(context, telcodesResIdName);
    }
    public int getTelcodesResId() {
        return getTelcodesResId(getUserIndex());
    }

    private int getColorIndex(int userIndex) {
        String[] colorValues = context.getResources().getStringArray(R.array.code_color_values);
        String defColorValue = colorValues[0];
        String colorValue = sharedPrefs.getString("KEY_COLOR"+userIndex, defColorValue);
        List<String> colorValueList = Arrays.asList(colorValues);
        int index = colorValueList.indexOf(colorValue);
        if(index != -1) {
            return index;
        } else {
            String[] colorNameValues = context.getResources().getStringArray(R.array.code_color_names);
            List<String> colorNameValueList = Arrays.asList(colorNameValues);
            index = colorNameValueList.indexOf(colorValue);
            if(index == -1) {
                index = 0;
            }
            sharedPrefs.edit().putString("KEY_COLOR"+userIndex, colorValues[index]).apply();
            return index;
        }
    }

    public String getCheckpoint(int userIndex) {
        int colorIndex = getColorIndex(userIndex);
        return context.getResources().getStringArray(R.array.checkpoints)[colorIndex];
    }
    public String getCheckpoint() {
        return getCheckpoint(getUserIndex());
    }

    public int getColorValue(int userIndex) {
        String[] colorResValues = context.getResources().getStringArray(R.array.code_color_values);
        String defColorResValue = colorResValues[0];
        String colorResValue = sharedPrefs.getString("KEY_COLOR"+userIndex, defColorResValue);
        List<String> colorResValueList = Arrays.asList(colorResValues);
        int index = colorResValueList.indexOf(colorResValue);
        if(index == -1) {
            String[] colorNames = context.getResources().getStringArray(R.array.code_color_names);
            List<String> colorNameList = Arrays.asList(colorNames);
            index = colorNameList.indexOf(colorResValue);
            if(index == -1) {
                index = 0;
            }
            colorResValue = colorResValues[index];
            sharedPrefs.edit().putString("KEY_COLOR"+userIndex, colorResValue).apply();
        }
        String colorResIdName = "R.color."+colorResValue;
        int colorResId = ResourceUtil.getId(context, colorResIdName);
        if(colorResId == 0) {
            colorResId = R.color.green;
        }
        return context.getColor(colorResId);
    }
    public int getColorValue() {
        return getColorValue(getUserIndex());
    }

    public String getColorName(int userIndex) {
        String[] colorNames = context.getResources().getStringArray(R.array.code_color_names);
        int colorIndex = getColorIndex(userIndex);
        return colorNames[colorIndex];
    }
    public String getColorName() {
        return getColorName(getUserIndex());
    }

    public String getUserName(int userIndex) {
        String defaultUserNameResIdName = "R.string.default_name"+userIndex;
        int defaultUserNameResId = ResourceUtil.getId(context, defaultUserNameResIdName);
        return sharedPrefs.getString("KEY_NAME"+userIndex, context.getString(defaultUserNameResId));
    }
    public String getUserName() {
        return getUserName(getUserIndex());
    }

    public String getUserId(int userIndex) {
        String defaultUserIdResIdName = "R.string.default_id"+ userIndex;
        int defaultUserIdResId = ResourceUtil.getId(context, defaultUserIdResIdName);
        return sharedPrefs.getString("KEY_ID"+userIndex, context.getString(defaultUserIdResId));
    }
    public String getUserId() {
        return getUserId(getUserIndex());
    }

    public String getCodeContent(int userIndex) {
        String text = sharedPrefs.getString("KEY_CONTENT"+userIndex, context.getString(R.string.default_content));
        StringBuilder sb = new StringBuilder(text);
        sb.append("&city="+getCity());
        sb.append("&name="+getUserName());
        sb.append("&id="+getUserId());
        sb.append("&ts="+System.currentTimeMillis());
        return sb.toString();
    }
    public String getCodeContent() {
        return getCodeContent(getUserIndex());
    }

    public int getUserIndex() {
        return sharedPrefs.getInt("KEY_USER_INDEX", 0);
    }
    public void setUserIndex(int userIndex) {
        sharedPrefs.edit().putInt("KEY_USER_INDEX", userIndex).apply();
    }

    public boolean isHangzhou(int userIndex) {
        return "杭州".equals(getCity(userIndex));
    }
    public boolean isHangzhou() {
        return isHangzhou(getUserIndex());
    }


    public void load() {
        if(!sharedPrefs.contains("KEY_USER_INDEX")) {
            sharedPrefs.edit().putInt("KEY_USER_INDEX", 0).apply();
        }
        for(int i=0; i<2; i++) {
            load(i);
        }
    }
    public void load(int userIndex) {
        String[] provinceValues = context.getResources().getStringArray(R.array.province_values);
        String defProvinceValue = provinceValues[0];
        if(!sharedPrefs.contains("KEY_PROVINCE"+userIndex)) {
            sharedPrefs.edit().putString("KEY_PROVINCE"+userIndex, defProvinceValue).apply();
        }
        if(!sharedPrefs.contains("KEY_CITY"+userIndex)) {
            int resId = getCitiesResId(userIndex);
            String defCity = context.getResources().getStringArray(resId)[0];
            sharedPrefs.edit().putString("KEY_CITY"+userIndex, defCity).apply();
        }
        if(!sharedPrefs.contains("KEY_HOTLINE"+userIndex)) {
            int resId = getTelcodesResId(userIndex);
            String defHotline = context.getResources().getStringArray(resId)[0];
            if(!defHotline.startsWith("+")) {
                defHotline = defHotline.concat("-12345-6");
            }
            sharedPrefs.edit().putString("KEY_HOTLINE"+userIndex, defHotline).apply();
        }
        if(!sharedPrefs.contains("KEY_NAME"+userIndex)) {
            sharedPrefs.edit().putString("KEY_NAME"+userIndex, getUserName(userIndex)).apply();
        }
        if(!sharedPrefs.contains("KEY_ID"+userIndex)) {
            sharedPrefs.edit().putString("KEY_ID"+userIndex, getUserId(userIndex)).apply();
        }
        if(!sharedPrefs.contains("KEY_CONTENT"+userIndex)) {
            sharedPrefs.edit().putString("KEY_CONTENT"+userIndex, getCodeContent(userIndex)).apply();
        }
        if(!sharedPrefs.contains("KEY_COLOR"+userIndex)) {
            String defColorResValue = context.getResources().getStringArray(R.array.code_color_values)[0];
            sharedPrefs.edit().putString("KEY_COLOR"+userIndex, defColorResValue).apply();
        }
    }
}
