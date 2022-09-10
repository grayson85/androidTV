package com.pxf.fftv.plus.player;

import android.app.Activity;
import android.content.Intent;

import com.pxf.fftv.plus.Const;

public class NativePlayer implements IVideoPlayer {

    private volatile static NativePlayer mInstance;

    private NativePlayer() {
    }

    static NativePlayer getInstance() {
        if (mInstance == null) {
            synchronized (NativePlayer.class) {
                if (mInstance == null) {
                    mInstance = new NativePlayer();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void play(Activity activity, String url, String title, String subtitle, int currentPart, String picUrl, int lastPosition) {
        Intent intent = new Intent(activity, NativePlayerActivity.class);
        intent.putExtra(VideoPlayer.KEY_VIDEO_URL, url);
        intent.putExtra(VideoPlayer.KEY_VIDEO_TITLE, title);
        intent.putExtra(VideoPlayer.KEY_VIDEO_SUB_TITLE, subtitle);
        intent.putExtra(VideoPlayer.KEY_VIDEO_CURRENT_PART, currentPart);
        intent.putExtra(VideoPlayer.KEY_VIDEO_PIC, picUrl);
        intent.putExtra(VideoPlayer.KEY_VIDEO_LAST_POSITION, lastPosition);
        activity.startActivityForResult(intent, Const.PLAY_REQUEST_CODE);
    }
}
