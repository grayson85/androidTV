package com.pxf.fftv.plus.model;

import android.content.Context;

import static com.pxf.fftv.plus.Const.DEFAULT_AUTO_LOGIN;
import static com.pxf.fftv.plus.Const.DEFAULT_PLAY;
import static com.pxf.fftv.plus.Const.DEFAULT_VIDEO;

public class DataModel {

    private volatile static DataModel mInstance;

    private DataModel() {
    }

    static DataModel getInstance() {
        if (mInstance == null) {
            synchronized (DataModel.class) {
                if (mInstance == null) {
                    mInstance = new DataModel();
                }
            }
        }
        return mInstance;
    }

    private static final String APP_PREFERENCE = "app_preference";

    private static final String PREFERENCE_KEY_VIDEO_ENGINE = "preference_key_video_engine";
    private static final String PREFERENCE_KEY_PLAYER_ENGINE = "preference_key_player_engine";

    private static final String PREFERENCE_KEY_AUTO_LOGIN = "preference_key_auto_login";
    private static final String PREFERENCE_KEY_ACCOUNT = "preference_key_account";
    private static final String PREFERENCE_KEY_PASSWORD = "preference_key_password";

    private static final String PREFERENCE_KEY_LAST_PLAY_URL = "preference_key_last_play_url";
    private static final String PREFERENCE_KEY_LAST_PLAY_NAME = "preference_key_last_play_name";

    public void setVideoEngine(Context context, String videoEngine) {
        save(context, PREFERENCE_KEY_VIDEO_ENGINE, videoEngine);
    }

    public String getVideoEngine(Context context) {
        return get(context, PREFERENCE_KEY_VIDEO_ENGINE, DEFAULT_VIDEO);
    }

    public void setPlayerEngine(Context context, String playerEngine) {
        save(context, PREFERENCE_KEY_PLAYER_ENGINE, playerEngine);
    }

    public String getPlayerEngine(Context context) {
        return get(context, PREFERENCE_KEY_PLAYER_ENGINE, DEFAULT_PLAY);
    }

    public void setAccount(Context context, String account) {
        save(context, PREFERENCE_KEY_ACCOUNT, account);
    }

    public String getAccount(Context context) {
        return get(context, PREFERENCE_KEY_ACCOUNT, "");
    }

    public void setPassword(Context context, String account) {
        save(context, PREFERENCE_KEY_PASSWORD, account);
    }

    public String getPassword(Context context) {
        return get(context, PREFERENCE_KEY_PASSWORD, "");
    }

    public void setAutoLogin(Context context, Boolean auto) {
        context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).edit().putBoolean(PREFERENCE_KEY_AUTO_LOGIN, auto).apply();
    }

    public Boolean isAutoLogin(Context context) {
        return context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).getBoolean(PREFERENCE_KEY_AUTO_LOGIN, DEFAULT_AUTO_LOGIN);
    }

    public void setLastPlayUrl(Context context, String url) {
        context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).edit().putString(PREFERENCE_KEY_LAST_PLAY_URL, url).apply();
    }

    public String getLastPlayUrl(Context context) {
        return context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).getString(PREFERENCE_KEY_LAST_PLAY_URL, "");
    }

    public void setLastPlayName(Context context, String name) {
        context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).edit().putString(PREFERENCE_KEY_LAST_PLAY_NAME, name).apply();
    }

    public String getLastPlayName(Context context) {
        return context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).getString(PREFERENCE_KEY_LAST_PLAY_NAME, "");
    }

    private void save(Context context, String key, String value) {
        context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }

    private String get(Context context, String key, String defaultValue) {
        return context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).getString(key, defaultValue);
    }
}
