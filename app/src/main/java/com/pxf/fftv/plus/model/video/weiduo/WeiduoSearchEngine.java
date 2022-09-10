package com.pxf.fftv.plus.model.video.weiduo;

import android.util.Log;

import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.model.ISearchEngine;
import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

public class WeiduoSearchEngine implements ISearchEngine {

    private volatile static WeiduoSearchEngine mInstance;

    private WeiduoSearchEngine() {
    }

    public static WeiduoSearchEngine getInstance() {
        if (mInstance == null) {
            synchronized (WeiduoSearchEngine.class) {
                if (mInstance == null) {
                    mInstance = new WeiduoSearchEngine();
                }
            }
        }
        return mInstance;
    }

    @Override
    public ArrayList<Video> getVideoListFromJson(String words) {
        Log.d("fftv", "search " + words);

        ArrayList<Video> videos = new ArrayList<>();

        Request request = new Request.Builder()
                .url("http://video.api.vitocms.cn:997/api/v2/search?kw=" + words + "&host=99.meetpt.cn&token=86320531fb9aba3e5c0fce946c7fb090")
                .build();
        try {
            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                WeiduoSearchBean bean = CommonUtils.getGson().fromJson(result, WeiduoSearchBean.class);

                for (int i = 0; i < bean.getData().getVideo().getList().size(); i++) {
                    Video video = new Video();
                    WeiduoSearchBean.DataBean.VideoBean.ListBean item = bean.getData().getVideo().getList().get(i);

                    video.setTitle(item.getTitle());
                    video.setTypeName(item.getType());

                    ArrayList<Video.Actor> actors = new ArrayList<>();
                    String[] stars = item.getStar().split(" ");
                    for (String s : stars) {
                        Video.Actor actor = new Video.Actor();
                        actor.setName(s);
                        actors.add(actor);
                    }
                    video.setActors(actors);

                    ArrayList<Video.Director> directors = new ArrayList<>();
                    Video.Director director = new Video.Director();
                    director.setName("未知");
                    directors.add(director);
                    video.setDirectors(directors);

                    video.setDescription(item.getDesc());
                    video.setImageUrl(item.getImg());
                    video.setYear(item.getYear());
                    video.setArea(item.getArea());
                    video.setLanguage("未知");
                    video.setWeiduoUrl(item.getUrl());

                    videos.add(video);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videos;
    }
}
