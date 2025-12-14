package com.pxf.fftv.plus.player;

import android.content.Context;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.model.Model;

public class VideoPlayer {

    public static final String KEY_VIDEO_URL = "key_video_url";
    public static final String KEY_VIDEO_TITLE = "key_video_title";
    public static final String KEY_VIDEO_SUB_TITLE = "key_video_sub_title";
    public static final String KEY_VIDEO_CURRENT_PART = "key_video_current_part";
    public static final String KEY_VIDEO_PIC = "key_video_pic";
    public static final String KEY_VIDEO_LAST_POSITION = "key_video_last_position";
    public static final String KEY_VIDEO_ID = "key_video_id";

    public static IVideoPlayer getVideoPlayer(Context context) {
        switch (Model.getData().getPlayerEngine(context)) {
            case Const.PLAY_3:
                return IjkVideoPlayer.getInstance();
            case Const.PLAY_4:
                // 支持音轨切换
                return EXOVideoPlayer.getInstance();
            default:
                // Const.NATIVE_PLAYER_ENGINE or Const.PLAY_2 (TBS removed)
                // 原生播放
                return NativePlayer.getInstance();
        }
    }

    public static IVideoPlayer getVideoPlayer(int context) {
        switch (context) {
            case 3:
                return IjkVideoPlayer.getInstance();
            case 4:
                // 支持音轨切换
                return EXOVideoPlayer.getInstance();
            default:
                // Const.NATIVE_PLAYER_ENGINE or 2 (TBS removed)
                // 原生播放
                return NativePlayer.getInstance();
        }
    }
}
