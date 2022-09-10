package com.pxf.fftv.plus.player;

import android.app.Activity;
import android.content.Context;

public interface IVideoPlayer {

    void play(Activity activity, String url, String title, String subtitle, int currentPart, String picUrl, int lastPosition);
}
