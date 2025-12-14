package com.pxf.fftv.plus.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.pxf.fftv.plus.Const;

public class EXOVideoPlayer implements IVideoPlayer {

    private volatile static EXOVideoPlayer mInstance;

    private EXOVideoPlayer() {
    }

    static EXOVideoPlayer getInstance() {
        if (mInstance == null) {
            synchronized (EXOVideoPlayer.class) {
                if (mInstance == null) {
                    mInstance = new EXOVideoPlayer();
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
            intent = new Intent(activity, EXOPlayerPhoneActivity.class);
        } else {
            intent = new Intent(activity, EXOPlayerActivity.class);
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

    private boolean isPhone(Activity activity) {
        TelephonyManager telephony = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        return telephony.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }
}
