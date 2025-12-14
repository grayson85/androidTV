package com.pxf.fftv.plus;

import android.os.Environment;

/**
 * 全局配置
 */
public class Const {

    public static final String LOG_TAG = "FFTV_LOG";
    // public static final String BASE_URL = "http://yunshi.meetpt.cn"; 后台地址
    public static String BASE_URL = "https://your-api-server.com"; // CMS API地址

    // Convert to methods to use current BASE_URL dynamically
    public static String getVipPayUrl() {
        return BASE_URL + "/api.php?action=juhepay";
    }

    public static String getVipOrderQueryUrl() {
        return BASE_URL + "/api.php?action=orderQuery";
    }

    public static String getRegisterUrl() {
        return BASE_URL + "/api.php?action=register";
    }

    public static String getLoginUrl() {
        return BASE_URL + "/api.php?action=login";
    }

    public static String getReturnUrl() {
        return BASE_URL + "/api.php?action=fankui";
    }

    public static String getBaseDataUrl() {
        return BASE_URL + "/app/version";
    }

    public static String getCardCodeUrl() {
        return BASE_URL + "/api.php?action=checkkami";
    }

    public static String getRefreshTokenUrl() {
        return BASE_URL + "/api.php?action=getinfo";
    }

    // Static fields for backward compatibility - Updated when BASE_URL changes via
    // refreshUrls()
    public static String VIP_PAY_URL = getVipPayUrl();
    public static String VIP_ORDER_QUERY = getVipOrderQueryUrl();
    public static String REGISTER_URL = getRegisterUrl();
    public static String LOGIN_URL = getLoginUrl();
    public static String RETURN_URL = getReturnUrl();
    public static String BASE_DATA_URL = getBaseDataUrl();
    public static String CARD_CODE_URL = getCardCodeUrl();
    public static String REFRESH_TOKEN_URL = getRefreshTokenUrl();

    // Call this after updating BASE_URL to refresh all dependent fields
    public static void refreshUrls() {
        VIP_PAY_URL = getVipPayUrl();
        VIP_ORDER_QUERY = getVipOrderQueryUrl();
        REGISTER_URL = getRegisterUrl();
        LOGIN_URL = getLoginUrl();
        RETURN_URL = getReturnUrl();
        BASE_DATA_URL = getBaseDataUrl();
        CARD_CODE_URL = getCardCodeUrl();
        REFRESH_TOKEN_URL = getRefreshTokenUrl();
    }

    public static final String EXTERNAL_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/fftv";
    public static final String EXTERNAL_FILE_UPDATE_APK_PATH = EXTERNAL_FILE_PATH + "/update.apk";

    public static final int PLAY_REQUEST_CODE = 100;
    public static final int SETTING_REQUEST_CODE = 200;
    public static final int VIDEO_HISTORY_NUM = 100;
    public static final int VIDEO_COLLECTION_NUM = 100;
    public static final int VIDEO_LIST_COLUMN = 5;
    public static final int ANIMATION_DURATION = 300;
    public static final int ANIMATION_ZOOM_IN_DURATION = 200;
    public static final int ANIMATION_ZOOM_OUT_DURATION = 100;
    public static final float ANIMATION_ZOOM_IN_SCALE = 1.15f;
    public static final float ANIMATION_ZOOM_OUT_SCALE = 1.1f;

    public static final int HOME_PAGE_COUNT = 6;
    public static final int HOME_PAGE_PERSON = 0;
    public static final int HOME_PAGE_RECOMMEND = 1;
    public static final int HOME_PAGE_MOVIE = 2;
    public static final int HOME_PAGE_TELEPLAY = 3;
    public static final int HOME_PAGE_CARTOON = 4;
    public static final int HOME_PAGE_SHOW = 5;

    public static final int ACCOUNT_EVER_VIP = -1;
    public static final int ACCOUNT_NO_VIP = 0;

    public enum VideoType {
        MOVIE_LATEST, /* 最新电影 */
        TELEPLAY_LATEST, /* 最新电视剧 */
        CARTOON_LATEST, /* 最新动漫 */
        SHOW_LATEST, /* 最新综艺 */

        MOVIE_ACTION, /* 动作电影 */
        MOVIE_COMEDY, /* 喜剧电影 */
        MOVIE_LOVE, /* 爱情电影 */
        MOVIE_SCIENCE, /* 科幻电影 */
        MOVIE_SCARY, /* 恐怖电影 */
        MOVIE_STORY, /* 剧情电影 */
        MOVIE_WAR, /* 战争电影 */

        TELEPLAY_CHINA, /* 大陆剧 */
        TELEPLAY_HONGKONG, /* 港剧 */
        TELEPLAY_JAPAN, /* 日剧 */
        TELEPLAY_KOREA, /* 韩剧 */
        TELEPLAY_EA, /* 欧美剧 */
        TELEPLAY_TAIWAN, /* 台湾剧 */
        TELEPLAY_SGMY,
        TELEPLAY_OTHER, /* 其它剧 */

        CARTOON_CHINA, /* 大陆动漫 */
        CARTOON_JK, /* 日韩动漫 */
        CARTOON_EA, /* 欧美动漫 */
        CARTOON_OTHER, /* 其它动漫 */

        SHOW_CHINA, /* 大陆综艺 */
        SHOW_HT, /* 港台综艺 */
        SHOW_EA, /* 欧美综艺 */
        SHOW_JK /* 日韩综艺 */;

    }

    public static final String VIDEO_1 = "video_weiduo";
    public static final String VIDEO_2 = "video_cms";
    public static final String VIDEO_3 = "video_ok";
    public static final String VIDEO_4 = "video_zd";
    public static final String DEFAULT_VIDEO = VIDEO_2;

    public static final String PLAY_1 = "native_player_engine";
    public static final String PLAY_2 = "tbs_player_engine";
    public static final String PLAY_3 = "ijk_player_engine";
    public static final String PLAY_4 = "exo_player_engine";
    public static final String DEFAULT_PLAY = PLAY_4;

    public static final Boolean DEFAULT_AUTO_LOGIN = true;

    public static final int REQUEST_CODE_INSTALL_UNKNOWN_APK = 0x000001;
    public static final String GONGGAO = "公告:本站所有内容均来自互联网分享站点所提供的公开引用资源，未提供影视资源上传、存储服务。";
    public static final boolean FEATURE_1 = true;
    public static final boolean FEATURE_2 = true;
    public static final boolean FEATURE_3 = true;
    public static final boolean FEATURE_4 = true;
    public static final boolean FEATURE_5 = true;
    public static final boolean FEATURE_6 = false;
    public static final boolean FEATURE_7 = true;
    public static final boolean FEATURE_8 = true;
    public static final boolean FEATURE_9 = true;
    public static final boolean FEATURE_10 = true;
    public static final boolean FEATURE_11 = true;
    public static final boolean FEATURE_12 = false;
    public static final boolean FEATURE_13 = true;
}
