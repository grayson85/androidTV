package com.pxf.fftv.plus.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.pxf.fftv.plus.Const;

public class IjkVideoPlayer implements IVideoPlayer{

    private volatile static IjkVideoPlayer mInstance;

    private IjkVideoPlayer() {
    }

    static IjkVideoPlayer getInstance() {
        if (mInstance == null) {
            synchronized (IjkVideoPlayer.class) {
                if (mInstance == null) {
                    mInstance = new IjkVideoPlayer();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void play(Activity activity, String url, String title, String subtitle, int currentPart, String picUrl, int lastPosition) {
        Intent intent = new Intent(activity, IjkMediaPlayerActivity.class);
        intent.putExtra(VideoPlayer.KEY_VIDEO_URL, url);
        intent.putExtra(VideoPlayer.KEY_VIDEO_TITLE, title);
        intent.putExtra(VideoPlayer.KEY_VIDEO_SUB_TITLE, subtitle);
        intent.putExtra(VideoPlayer.KEY_VIDEO_CURRENT_PART, currentPart);
        intent.putExtra(VideoPlayer.KEY_VIDEO_PIC, picUrl);
        intent.putExtra(VideoPlayer.KEY_VIDEO_LAST_POSITION, lastPosition);
        activity.startActivityForResult(intent, Const.PLAY_REQUEST_CODE);
    }
}
