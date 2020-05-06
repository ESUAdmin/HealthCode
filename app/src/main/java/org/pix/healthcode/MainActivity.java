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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements Handler.Callback, SharedPreferences.OnSharedPreferenceChangeListener {
    private Handler handler = null;
    private Timer timer = null;
    private SharedPreferences prefs;

    @Override
    public boolean handleMessage(Message msg) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        String strTime = fmt.format(date);
        TextView txtDateTime = findViewById(R.id.txtDateTime);
        txtDateTime.setText(strTime);
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        switch(key) {
            case "KEY_PROVINCE":
                String value = sp.getString(key, getString(R.string.default_province));
                TextView view = findViewById(R.id.txtReadme);
                String readme = String.format(getString(R.string.usage), value);
                view.setText(readme);
                break;
            case "KEY_CITY":
                value = sp.getString(key, getString(R.string.default_city));
                view = findViewById(R.id.txtTitleCity);
                view.setText(value);
                view = findViewById(R.id.txtIdCity);
                view.setText(value);
                break;
            case "KEY_HOTLINE":
                value = sp.getString(key, getString(R.string.default_hotline));
                view = findViewById(R.id.txtHotline);
                view.setText(value);
                break;
            case "KEY_NAME":
                value = sp.getString(key, getString(R.string.default_name));
                view = findViewById(R.id.txtUserName);
                view.setText(value);
                break;
            case "KEY_ID":
                value = sp.getString(key, getString(R.string.default_id));
                ToggleButton btnIdVisibility = findViewById(R.id.btnIdVisibility);
                boolean visible = btnIdVisibility.isChecked();
                if(visible) {
                    view = findViewById(R.id.txtUserName);
                    view.setText(value);
                }
                break;
            case "KEY_CONTENT":
                ImageView imgQrcode = findViewById(R.id.imgQrcode);
                value = sp.getString(key, getString(R.string.default_content));
                Bitmap bmp = genQrcode(value, getColor(R.color.colorQrcode));
                if(bmp != null) {
                    imgQrcode.setImageBitmap(bmp);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        handler = new Handler(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        loadConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        super.onPause();
    }

    private void loadConfig() {
        onSharedPreferenceChanged(prefs, "KEY_PROVINCE");
        onSharedPreferenceChanged(prefs, "KEY_CITY");
        onSharedPreferenceChanged(prefs, "KEY_NAME");
        onSharedPreferenceChanged(prefs, "KEY_ID");
        onSharedPreferenceChanged(prefs, "KEY_HOTLINE");
        onSharedPreferenceChanged(prefs, "KEY_CONTENT");
    }

    public void quit(View view) {
        finish();
    }

    public void showError(View view) {
        Toast t = Toast.makeText(this, R.string.tips, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        t.show();
    }

    public void toggleIdNumber(View view) {
        ToggleButton btn = (ToggleButton) view;
        TextView txtId = findViewById(R.id.txtIdentityId);
        if(btn.isChecked()) {
            txtId.setText(prefs.getString("KEY_ID", getString(R.string.default_id)));
        } else {
            txtId.setText("**** **** **** **** **");
        }
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
