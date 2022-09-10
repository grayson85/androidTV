package com.pxf.fftv.plus.contract.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.FFTVApplication;
import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.common.InternalFileSaveUtil;
import com.pxf.fftv.plus.common.Ui;
import com.pxf.fftv.plus.player.NativePlayerActivity;
import com.pxf.fftv.plus.player.VideoHistoryEvent;
import com.pxf.fftv.plus.player.VideoPlayer;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoHistoryActivity extends AppCompatActivity implements VideoHistoryAdapter.OnHistoryItemClickListener {

    @BindView(R.id.video_history_recycler_view)
    RecyclerView video_history_recycler_view;

    @BindView(R.id.top_bar_menu_right_note)
    TextView top_bar_menu_right_note;

    private LinkedList<VideoHistory> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_history);
        ButterKnife.bind(this);
        initGongGao();
        Ui.configTopBar(this, "选中视频时菜单键可删除");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initGongGao();
        historyList = (LinkedList<VideoHistory>) InternalFileSaveUtil.getInstance(this).get("video_history");
        video_history_recycler_view.setLayoutManager(new GridLayoutManager(this, 5) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return FFTVApplication.screenHeight / 2;
            }
        });
        if (historyList != null) {
            VideoHistoryAdapter adapter = new VideoHistoryAdapter(this, historyList, this);
            video_history_recycler_view.setAdapter(adapter);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onHistoryItemClick(int position) {
        VideoHistory item = historyList.get(position);
        VideoPlayer.getVideoPlayer(this).play(this, item.getUrl(), item.getTitle(), item.getSubTitle(), -1, item.getPicUrl(), item.getLastPosition());
    }

    @Override
    public void onHistoryItemMenuClick(int position) {
        historyList.remove(position);
        InternalFileSaveUtil.getInstance(this).put("video_history", historyList);
        VideoHistoryAdapter adapter = new VideoHistoryAdapter(this, historyList, this);
        video_history_recycler_view.setAdapter(adapter);
    }

    private void initGongGao(){
        top_bar_menu_right_note.setVisibility(TextView.GONE);
    }
}
