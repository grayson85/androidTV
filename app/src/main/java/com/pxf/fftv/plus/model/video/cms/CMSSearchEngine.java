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

        try {
            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                CMSSearchBean bean = CommonUtils.getGson().fromJson(response.body().string(), CMSSearchBean.class);

                for (int i = 0; i < bean.getList().size(); i++) {
                    Video video = new Video();
                    CMSSearchBean.VideoItem item = bean.getList().get(i);
                    String[] source = item.getVod_play_from().split("\\$\\$\\$");
                    int filterIndex = Arrays.asList(source).indexOf("wjm3u8");

                    if (!(filterIndex == 0 && source.length == 1)) {
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


                        ArrayList<Video.Part> parts = new ArrayList<>();
                        // 不同资源
                        String[] resources = item.getVod_play_url().split("\\$\\$\\$");
                        // 不同的集数
                        //String[] sets = resources[0].split("\\#");
                        String[] sets;
                        if (filterIndex == 0){
                            sets = resources[1].split("\\#");
                        }else{
                            sets = resources[0].split("\\#");
                        }
                        for (int j = 0; j < sets.length; j++) {
                            String[] urlArray = sets[j].split("\\$");
                            if (urlArray.length == 2) {
                                Video.Part part = new Video.Part();
                                part.setTitle(urlArray[0]);
                                part.setUrl(urlArray[1]);
                                parts.add(part);
                            }
                        }

                        video.setParts(parts);

                        videos.add(video);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videos;
    }
}
