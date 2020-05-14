package org.pix.healthcode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements Handler.Callback, SharedPreferences.OnSharedPreferenceChangeListener {
    private Handler handler = null;
    private Timer timer = null;
    private SharedPreferences sp;
    private boolean isHz;

    @Override
    public boolean handleMessage(Message msg) {
        updateDateTimeView();
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        switch(key) {
            case "KEY_STYLE":
                updatePage();
                break;
            case "KEY_COLOR":
                updateQrcode();
                updateCheckpoints();
                break;
            case "KEY_PROVINCE":
                updateCheckpoints();
                break;
            case "KEY_CITY":
                updateCityViews();
                break;
            case "KEY_HOTLINE":
                updateHotlineView();
                break;
            case "KEY_NAME":
                updateNameView();
                break;
            case "KEY_ID":
                updateIdView();
                break;
            case "KEY_CONTENT":
                updateQrcode();
                break;
        }
    }

    private void updatePage() {
        updateCityViews();
        updateNameView();
        updateIdView();
        updateQrcode();
        updateCheckpoints();
        updateHotlineView();
    }
    private void updateCheckpoints() {
        String defProvince = getResources().getStringArray(R.array.provinces)[0];
        String province = sp.getString("KEY_PROVINCE", defProvince);
        String[] colorNames = getResources().getStringArray(R.array.code_names);
        String colorName = sp.getString("KEY_COLOR", colorNames[0]);
        int index = Arrays.asList(colorNames).indexOf(colorName);
        String fmtStr = getResources().getStringArray(R.array.checkpoints)[index];
        String text = String.format(fmtStr, colorName, province);
        text = text.concat(getString(R.string.notes));
        TextView view = findViewById(R.id.txtCheckpoints);
        view.setText(text);
    }
    private void updateCityViews() {
        String defCity = getResources().getStringArray(R.array.北京市cities)[0];
        String city = sp.getString("KEY_CITY", defCity);
        TextView view = findViewById(R.id.txtTitleCity);
        view.setText(city);
        if(!isHz) {
            view = findViewById(R.id.txtIdCity);
            if(view != null) {
                view.setText(city);
            }
        }
    }
    private void updateDateTimeView() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        if(isHz) {
            DateFormat fmt = new SimpleDateFormat("M月dd日");
            TextView txtDate = findViewById(R.id.txtDate);
            txtDate.setText(fmt.format(date));
            fmt = new SimpleDateFormat("HH:mm:ss");
            TextView txtTime = findViewById(R.id.txtTime);
            txtTime.setText(fmt.format(date));
        } else {
            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TextView txtDateTime = findViewById(R.id.txtDateTime);
            txtDateTime.setText(fmt.format(date));
        }
    }
    private void updateHotlineView() {
        String defHotline = getResources().getStringArray(R.array.北京市telcodes)[0] + "12345-6";
        String hotline = sp.getString("KEY_HOTLINE", defHotline);
        TextView view = findViewById(R.id.txtHotline);
        view.setText(hotline);
    }
    private void updateNameView() {
        String name = sp.getString("KEY_NAME", getString(R.string.default_name));
        TextView view = findViewById(R.id.txtUserName);
        view.setText(name);
    }
    private void updateIdView() {
        if(isHz) {
            return;
        }
        ToggleButton btnIdVisibility = findViewById(R.id.btnIdVisibility);
        boolean visible = btnIdVisibility.isChecked();
        TextView view = findViewById(R.id.txtIdentityId);
        if(visible) {
            String idStr = sp.getString("KEY_ID", getString(R.string.default_id));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<idStr.length(); i++) {
                sb.append(idStr.charAt(i));
                if(i % 4 == 3) {
                    sb.append(' ');
                }
            }
            view.setText(sb.toString());
        } else {
            view.setText("**** **** **** **** **");
        }
    }
    private void updateQrcode() {
        ImageView imgQrcode = findViewById(R.id.imgQrcode);
        String text = sp.getString("KEY_CONTENT", getString(R.string.default_content));
        String defColorName = getResources().getStringArray(R.array.code_names)[0];
        String resIdStr = "R.color."+sp.getString("KEY_COLOR", defColorName);
        int resId = ResourceUtil.getId(this, resIdStr);
        int color = getColor(resId);
        Bitmap bmp = genQrcode(text, color);
        if(bmp != null) {
            imgQrcode.setImageBitmap(bmp);
        }
        if(isHz) {
            TextView hospitleTipView = findViewById(R.id.txtHospitleTip);
            hospitleTipView.setTextColor(color);
            RelativeLayout layout = findViewById(R.id.bgHealthColor);
            layout.setBackgroundColor(color);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        loadConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isHz = sp.getString("KEY_CITY", "").equals("杭州");
        if(isHz) {
            setContentView(R.layout.activity_hz);
        } else {
            setContentView(R.layout.activity_default);
        }
        updatePage();
        sp.registerOnSharedPreferenceChangeListener(this);
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        timer = new Timer();
        timer.schedule(tt, 0, 66);
    }

    @Override
    protected void onPause() {
        timer.cancel();
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void loadConfig() {
        if(!sp.contains("KEY_COLOR")) {
            String defColorName = getResources().getStringArray(R.array.code_names)[0];
            sp.edit().putString("KEY_COLOR", defColorName).apply();
        }
        String defProvince = getResources().getStringArray(R.array.provinces)[0];
        if(!sp.contains("KEY_PROVINCE")) {
            sp.edit().putString("KEY_PROVINCE", defProvince).apply();
        }
        if(!sp.contains("KEY_CITY")) {
            String resName = "R.array."+ sp.getString("KEY_PROVINCE", defProvince)+"cities";
            int resId = ResourceUtil.getId(this, resName);
            String defCity = getResources().getStringArray(resId)[0];
            sp.edit().putString("KEY_CITY", defCity).apply();
        }
        if(!sp.contains("KEY_HOTLINE")) {
            String resName = "R.array."+ sp.getString("KEY_PROVINCE", defProvince)+"telcodes";
            int resId = ResourceUtil.getId(this, resName);
            String defHotline = getResources().getStringArray(resId)[0];
            if(!defHotline.startsWith("+")) {
                defHotline = defHotline.concat("12345-6");
            }
            sp.edit().putString("KEY_HOTLINE", defHotline).apply();
        }
    }

    public void quit(View view) {
        finish();
    }

    public void showError(View view) {
        Toast t = Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        t.show();
    }

    public void toggleIdNumber(View view) {
        updateIdView();
    }

    public void showOptions(View view) {
        startActivity(new Intent(this, ConfigActivity.class));
    }

    private Bitmap genQrcode(String text, int color) {
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 640, 640, hints);
            int w = bitMatrix.getWidth();
            int h = bitMatrix.getHeight();
            int[] pixels = new int[w * h];
            for(int y=0; y<h; y++) {
                for(int x=0; x<w; x++) {
                    pixels[y*w+x] = bitMatrix.get(x, y) ? color : Color.TRANSPARENT;
                }
            }
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels, 0, w, 0, 0, w, h);
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
