package com.pxf.fftv.plus.model.video.cms;

import android.util.Log;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.model.ISearchEngine;
import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Request;
import okhttp3.Response;

public class CMSSearchEngine implements ISearchEngine {

    private volatile static CMSSearchEngine mInstance;

    private CMSSearchEngine() {
    }

    public static CMSSearchEngine getInstance() {
        if (mInstance == null) {
            synchronized (CMSSearchEngine.class) {
                if (mInstance == null) {
                    mInstance = new CMSSearchEngine();
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
                .url(Const.BASE_URL + "/api.php/provide/vod/?ac=detail&wd=" + words)
                .build();

        Response response = null;
        try {
            response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                CMSSearchBean bean = CommonUtils.getGson().fromJson(response.body().string(), CMSSearchBean.class);

                for (int i = 0; i < bean.getList().size(); i++) {
                    Video video = new Video();
                    CMSSearchBean.VideoItem item = bean.getList().get(i);

                    video.setTitle(item.getVod_name());

                    ArrayList<Video.Actor> actors = new ArrayList<>();
                    String[] stars = item.getVod_actor().split(",");
                    for (String s : stars) {
                        Video.Actor actor = new Video.Actor();
                        actor.setName(s);
                        actors.add(actor);
                    }
                    video.setActors(actors);

                    ArrayList<Video.Director> directors = new ArrayList<>();
                    Video.Director director = new Video.Director();
                    director.setName(item.getVod_director());
                    directors.add(director);
                    video.setDirectors(directors);

                    video.setDescription(item.getVod_content());
                    video.setImageUrl(item.getVod_pic());
                    video.setYear(item.getVod_year());
                    video.setArea(item.getVod_area());
                    video.setLanguage(item.getVod_lang());

                    // 不同资源
                    String[] from = item.getVod_play_from().split("\\$\\$\\$");

                    // 不同的集数
                    String[] resources = item.getVod_play_url().split("\\$\\$\\$");

                    // video.setVodPlayFrom(from[0]);
                    ArrayList<Video.VodSource> vodSources = new ArrayList<>();
                    for (int z = 0; z < from.length; z++) {
                        ArrayList<Video.Part> parts = new ArrayList<>();
                        Video.VodSource source = new Video.VodSource();
                        String[] sets = resources[z].split("\\#");
                        for (int j = 0; j < sets.length; j++) {
                            String[] urlArray = sets[j].split("\\$");
                            if (urlArray.length == 2) {
                                Video.Part part = new Video.Part();
                                part.setTitle(urlArray[0]);
                                part.setUrl(urlArray[1]);
                                parts.add(part);
                            }
                        }
                        source.sourceName = from[z];
                        source.part = parts;
                        vodSources.add(source);

                        video.setParts(parts);
                    }
                    video.setVodSource(vodSources);
                    videos.add(video);
                }
            }
            // }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return videos;
    }
}
