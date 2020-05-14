package org.pix.healthcode;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

import java.io.File;

public final class ResourceUtil {

    public static int getId(Context context, String resName) {
        String[] tan = resName.split("\\.");
        if (tan.length < 2 || tan.length > 3) {
            return 0;
        }
        String defPackage, defType, name;
        if (tan.length == 3) {
            if (!tan[0].equals("R")) {
                defPackage = tan[0];
            } else {
                defPackage = context.getPackageName();
            }
            defType = tan[1];
            name = tan[2];
        } else {
            defPackage = context.getPackageName();
            defType = tan[0];
            name = tan[1];
        }
        Resources res = context.getResources();
        return res.getIdentifier(name, defType, defPackage);
    }

    public static String getString(Context context, String resName) {
        return getString(context, resName, null);
    }

    public static String getString(Context context, String resName, String defValue) {
        int resId = getId(context, "string."+resName);
        try {
            return context.getString(resId);
        } catch (NotFoundException e) {
            return defValue;
        }
    }

    public static int getInt(Context context, String resName, int defValue) {
        int resId = getId(context, resName);
        try {
            return context.getResources().getInteger(resId);
        } catch (NotFoundException e) {
            return defValue;
        }
    }

    public static int[] getIntArray(Context context, String resName) {
        int resId = getId(context, resName);
        try {
            return context.getResources().getIntArray(resId);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public static String[] getStringArray(Context context, String resName) {
        int resId = getId(context, resName);
        try {
            return context.getResources().getStringArray(resId);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public final static CharSequence appDataPath = File.separator+"data"+File.separator+"data";

    public static String getDataPath(Context c) {
        return appDataPath+File.separator+c.getPackageName();
    }

    public static String getDatabasePath(Context c) {
        return appDataPath+File.separator+c.getPackageName()+File.separator+"databases";
    }

    public static String getFileStreamPath(Context c) {
        return appDataPath+File.separator+c.getPackageName()+File.separator+"files";
    }

    public static String getSharedPrefsPath(Context c) {
        return appDataPath+File.separator+c.getPackageName()+File.separator+"shared_prefs";
    }

}
