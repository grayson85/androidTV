package com.pxf.fftv.plus.player;

import android.os.Bundle;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.PlayerManager;

//20220910 - Added new feature ijkPlayer for Phone
public class IjkPlayerPhoneActivity extends AppCompatActivity implements PlayerManager.PlayerStateListener {
    private PlayerManager player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_player_phone);
        initPlayer();
    }
    private void initPlayer() {
        player = new PlayerManager(this);
        player.setFullScreenOnly(true);
        player.setScaleType(PlayerManager.SCALETYPE_FILLPARENT);
        player.playInFullScreen(true);
        player.setPlayerStateListener(this);
        player.play(getIntent().getStringExtra(VideoPlayer.KEY_VIDEO_URL));
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (player.gestureDetector.onTouchEvent(event))
            return true;
        return super.onTouchEvent(event);
    }
    @Override
    public void onComplete() {
    }
    @Override
    public void onError() {
    }
    @Override
    public void onLoading() {
    }
    @Override
    public void onPlay() {
    }

}
