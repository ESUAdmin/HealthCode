package org.pix.healthcode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements Handler.Callback, SharedPreferences.OnSharedPreferenceChangeListener, PopupMenu.OnMenuItemClickListener {
    private final static int MSG_UPDATE_TIME = 0;
    private final static int MSG_PROMPT_OPTIONS = 1;

    private Handler handler = null;
    private Timer timer = null;
    private SharedPreferences sharedPrefs;
    private PrefsConfig cfg;
    private Timer promptAnimTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(this);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        cfg = new PrefsConfig(this);
        cfg.load();
        setDisplayLanguage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLayout();
        updateUI();
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        timer = new Timer();
        timer.schedule(tt, 0, 66);
        boolean isNewbie = sharedPrefs.getBoolean("KEY_NEWBIE", true);
        if(isNewbie) {
            promptAnimTimer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            };
            promptAnimTimer.schedule(task, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(10));
        }
    }

    @Override
    protected void onPause() {
        timer.cancel();
        if(promptAnimTimer != null) {
            promptAnimTimer.cancel();
            promptAnimTimer = null;
        }
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MSG_UPDATE_TIME:
                updateDateTimeView();
                break;
            case MSG_PROMPT_OPTIONS:
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.prompt_anim);
                ImageButton btnOptions = findViewById(R.id.btnOptions);
                btnOptions.startAnimation(animation);
                break;
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        int changedUserIndex = key.charAt(key.length()-1) - '0';
        if(changedUserIndex == cfg.getUserIndex()) {
            String sKey = key.substring(0, key.length()-1);
            switch(sKey) {
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
                default:
                    break;
            }
        }
    }

    private void updateUI() {
        updateCityViews();
        updateNameView();
        updateIdView();
        updateQrcode();
        updateCheckpoints();
        updateHotlineView();
    }
    private void updateCheckpoints() {
        TextView view = findViewById(R.id.txtCheckpoints);
        if(view != null) {
            String province = cfg.getProvince();
            String colorName = cfg.getColorName();
            String checkpoint = cfg.getCheckpoint();
            String text = String.format(checkpoint, colorName, province);
            text = text.concat(getString(R.string.notes));
            view.setText(text);
        }
    }
    private void updateCityViews() {
        String province = cfg.getProvince();
        TextView view = findViewById(R.id.txtTitleCity);
        if(view != null) {
            view.setText(province);
        }
        view = findViewById(R.id.txtIdCity);
        if(view != null) {
            view.setText(province);
        }
    }
    private void updateDateTimeView() {
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        if(findViewById(R.id.txtDateTime) != null) {
            DateFormat fmt = new SimpleDateFormat("MM月dd日 HH:mm:ss", Locale.getDefault());
            TextView txtDateTime = findViewById(R.id.txtDateTime);
            txtDateTime.setText(fmt.format(date));
        } else {
            DateFormat fmt = new SimpleDateFormat("M", Locale.getDefault());
            TextView txtMonth = findViewById(R.id.txtMonth);
            txtMonth.setText(fmt.format(date));
            fmt = new SimpleDateFormat("dd", Locale.getDefault());
            TextView txtDay = findViewById(R.id.txtDay);
            txtDay.setText(fmt.format(date));
            fmt = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            TextView txtTime = findViewById(R.id.txtTime);
            txtTime.setText(fmt.format(date));
        }
    }
    private void updateHotlineView() {
        TextView view = findViewById(R.id.txtHotline);
        if(view != null) {
            String hotline = cfg.getHotline();
            view.setText(hotline);
        }
    }
    private void updateNameView() {
        TextView view = findViewById(R.id.txtUserName);
        if(view != null) {
            String name = cfg.getUserName();
            view.setText(name);
        }
    }
    private void updateIdView() {
        ToggleButton btnIdVisibility = findViewById(R.id.btnIdVisibility);
        if(btnIdVisibility != null) {
            boolean visible = btnIdVisibility.isChecked();
            String userId = cfg.getUserId();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < userId.length(); i++) {
                sb.append(visible ? userId.charAt(i) : '*');
                if (i % 4 == 3) {
                    sb.append(' ');
                }
            }
            TextView view = findViewById(R.id.txtIdentityId);
            if(view != null) {
                view.setText(sb.toString());
            }
        }
    }
    private void updateQrcode() {
        ImageView imgQrcode = findViewById(R.id.imgQrcode);
        String codeContent = cfg.getCodeContent();
        int colorValue = cfg.getColorValue();
        Bitmap bmp = genQrcode(codeContent, colorValue);
        if(bmp != null) {
            imgQrcode.setImageBitmap(bmp);
        }

        TextView hospitleTipView = findViewById(R.id.txtHospitleTip);
        if(hospitleTipView != null) {
            hospitleTipView.setTextColor(colorValue);
        }
        RelativeLayout bgHealthColor = findViewById(R.id.bgHealthColor);
        if(bgHealthColor != null) {
            bgHealthColor.setBackgroundColor(colorValue);
        }
        ImageView frameImageView = findViewById(R.id.qrcode_frame);
        if(frameImageView != null) {
            GradientDrawable frameShape = (GradientDrawable) frameImageView.getBackground();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            frameShape.setStroke((int) (6 * dm.density), colorValue);
        }
    }

    private void setLayout() {
        String province = cfg.getProvince();
        String city = cfg.getCity();
        if("杭州".equals(city)) {
            setBrightness(0.618f);
            setContentView(R.layout.activity_hangzhou);
        } else if("山东省".equals(province)) {
            setBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
            setContentView(R.layout.activity_sandong);
        } else {
            setBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
            setContentView(R.layout.activity_default);
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

    public void switchUser(View view) {
        int userIndex = cfg.getUserIndex();
        userIndex = (userIndex + 1) % 2;
        cfg.setUserIndex(userIndex);
        setLayout();
        updateUI();
    }

    public void refreshQrcode(View view) {
        updateQrcode();
    }

    public void showOptions(View view) {
        PopupMenu pm = new PopupMenu(this, view);
        Menu menu = pm.getMenu();
        getMenuInflater().inflate(R.menu.menu, menu);
        SubMenu sm = menu.findItem(R.id.id_customization).getSubMenu();
        for(int i=0; i<2; i++) {
            String title = String.format(getString(R.string.menu_edit_user), (i+1));
            sm.getItem(i).setTitle(title);
        }
        sm = menu.findItem(R.id.select_language).getSubMenu();
        String lang = sharedPrefs.getString("LANGUAGE", Locale.getDefault().getLanguage());
        if (lang.equals(Locale.CHINESE.getLanguage())) {
            sm.findItem(R.id.id_lang_zh).setChecked(true);
        } else {
            sm.findItem(R.id.id_lang_en).setChecked(true);
        }
        pm.setOnMenuItemClickListener(this);
        pm.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.id_cust1:
            case R.id.id_cust2:
                Intent intent = new Intent(this, ConfigActivity.class);
                intent.putExtra("INDEX", item.getItemId()==R.id.id_cust1 ? 0 : 1);
                startActivity(intent);
                sharedPrefs.edit().putBoolean("KEY_NEWBIE", false).apply();
                if(promptAnimTimer != null) {
                    promptAnimTimer.cancel();
                    promptAnimTimer = null;
                }
                break;
            case R.id.id_lang_en:
                sharedPrefs.edit().putString("LANGUAGE", Locale.ENGLISH.getLanguage()).commit();
                recreate();
                break;
            case R.id.id_lang_zh:
                sharedPrefs.edit().putString("LANGUAGE", Locale.CHINESE.getLanguage()).commit();
                recreate();
                break;
            case R.id.id_help_and_suggestion:
                WebViewActivity.startActivity(this, R.string.menu_help_and_suggestion, "file:////android_asset/help.html");
                break;
            case R.id.id_app_info:
                WebViewActivity.startActivity(this, R.string.menu_app_info, "file:////android_asset/appinfo.html");
                break;
            case R.id.id_privacy:
                WebViewActivity.startActivity(this, R.string.menu_privacy, "https://morrowind.github.io/privacypolicy/health-code-demo.html");
                break;
            default:
                break;
        }
        return false;
    }

    private void setDisplayLanguage() {
        String langStr = sharedPrefs.getString("LANGUAGE", Locale.getDefault().getLanguage());
        if (!langStr.equals(Locale.CHINESE.getLanguage()) && !langStr.equals(Locale.ENGLISH.getLanguage())) {
            langStr = Locale.ENGLISH.getLanguage();
        }
        Locale locale = new Locale(langStr);
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(conf, dm);
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

    public void setBrightness(float brightness) {
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }
}
