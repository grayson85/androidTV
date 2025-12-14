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
import com.pxf.fftv.plus.model.video.Video;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

public class VideoHistoryActivity extends AppCompatActivity implements VideoHistoryAdapter.OnHistoryItemClickListener {

    RecyclerView video_history_recycler_view;

    TextView top_bar_menu_right_note;

    private LinkedList<VideoHistory> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_history);
        video_history_recycler_view = findViewById(R.id.video_history_recycler_view);
        top_bar_menu_right_note = findViewById(R.id.top_bar_menu_right_note);
        initGongGao();
        Ui.configTopBar(this, "选中视频时菜单键可删除");

        findViewById(R.id.btn_clear_all).setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("清空历史")
                    .setMessage("确定要清空所有播放历史吗？")
                    .setPositiveButton("清空", (dialog, which) -> {
                        if (historyList != null) {
                            historyList.clear();
                            InternalFileSaveUtil.getInstance(this).put("video_history", historyList);
                            // Refresh Adapter
                            VideoHistoryAdapter adapter = new VideoHistoryAdapter(this, historyList, this);
                            video_history_recycler_view.setAdapter(adapter);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        // Focus animation for Clear button
        Ui.setMenuFocusAnimator(this, findViewById(R.id.btn_clear_all), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initGongGao();
        historyList = (LinkedList<VideoHistory>) InternalFileSaveUtil.getInstance(this).get("video_history");

        android.util.Log.d("VideoHistory",
                "Loading history: " + (historyList == null ? "null" : historyList.size() + " items"));

        video_history_recycler_view.setLayoutManager(new GridLayoutManager(this, 5) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return FFTVApplication.screenHeight / 2;
            }
        });
        if (historyList != null && !historyList.isEmpty()) {
            android.util.Log.d("VideoHistory", "First item: " + historyList.get(0).getTitle());
            VideoHistoryAdapter adapter = new VideoHistoryAdapter(this, historyList, this);
            video_history_recycler_view.setAdapter(adapter);
        } else {
            android.util.Log.d("VideoHistory", "History list is empty or null");
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

        Video video = new Video();
        video.setTitle(item.getTitle());
        video.setImageUrl(item.getPicUrl());
        video.setId(item.getId());
        // We might not have all details here, but we set what we have.
        // The detail activity might need to fetch more info or just show what's
        // available.
        // If the history item has a link to the full video details (like an ID), that
        // would be better,
        // but based on VideoHistory class, we only have url, title, etc.

        // Construct a part for the video so it can be played if needed
        ArrayList<Video.Part> parts = new ArrayList<>();
        Video.Part part = new Video.Part();
        part.setTitle(item.getSubTitle());
        part.setUrl(item.getUrl());
        parts.add(part);
        video.setParts(parts);

        // Also set VodSource as VideoDetailActivity uses it
        ArrayList<Video.VodSource> sources = new ArrayList<>();
        Video.VodSource source = new Video.VodSource();
        source.sourceName = "History"; // Or some default
        source.part = parts;
        sources.add(source);
        video.setVodSource(sources);

        Intent intent = new Intent(this, com.pxf.fftv.plus.contract.detail.VideoDetailActivity.class);
        startActivity(intent);
        EventBus.getDefault().postSticky(new com.pxf.fftv.plus.contract.detail.VideoDetailEvent(video));
    }

    @Override
    public void onHistoryItemMenuClick(int position) {
        historyList.remove(position);
        InternalFileSaveUtil.getInstance(this).put("video_history", historyList);
        VideoHistoryAdapter adapter = new VideoHistoryAdapter(this, historyList, this);
        video_history_recycler_view.setAdapter(adapter);
    }

    private void initGongGao() {
        if (top_bar_menu_right_note != null) {
            top_bar_menu_right_note.setVisibility(TextView.GONE);
        }
    }
}
