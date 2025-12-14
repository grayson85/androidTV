package com.pxf.fftv.plus.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.pxf.fftv.plus.Const;

public class IjkVideoPlayer implements IVideoPlayer {

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
    public void play(Activity activity, String url, String title, String subtitle, int currentPart, String picUrl,
            int lastPosition, int id) {
        Intent intent;
        if (isPhone(activity) && Const.FEATURE_5) {
            intent = new Intent(activity, IjkPlayerPhoneActivity.class);
        } else {
            intent = new Intent(activity, IjkMediaPlayerActivity.class);
        }
        intent.putExtra(VideoPlayer.KEY_VIDEO_URL, url);
        intent.putExtra(VideoPlayer.KEY_VIDEO_TITLE, title);
        intent.putExtra(VideoPlayer.KEY_VIDEO_SUB_TITLE, subtitle);
        intent.putExtra(VideoPlayer.KEY_VIDEO_CURRENT_PART, currentPart);
        intent.putExtra(VideoPlayer.KEY_VIDEO_PIC, picUrl);
        intent.putExtra(VideoPlayer.KEY_VIDEO_LAST_POSITION, lastPosition);
        intent.putExtra(VideoPlayer.KEY_VIDEO_ID, id);
        activity.startActivityForResult(intent, Const.PLAY_REQUEST_CODE);
    }

    // 20220910 - Added new feature ijkPlayer for Phone
    private boolean isPhone(Activity activity) {
        TelephonyManager telephony = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        return telephony.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }
}
