package com.pxf.fftv.plus.model.video.cms;

import android.util.Log;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.model.IVideoEngine;
import com.pxf.fftv.plus.model.VideoEngineParam;
import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Request;
import okhttp3.Response;

public class CMSVideoEngine implements IVideoEngine {

    private volatile static CMSVideoEngine mInstance;

    private CMSVideoEngine() {
    }

    public static CMSVideoEngine getInstance() {
        if (mInstance == null) {
            synchronized (CMSVideoEngine.class) {
                if (mInstance == null) {
                    mInstance = new CMSVideoEngine();
                }
            }
        }
        return mInstance;
    }

    private static final String BASE_URL = Const.BASE_URL + "/api.php/provide/vod/?ac=detail";
    @Override
    public ArrayList<Video> getVideos(Const.VideoType type, int page) {
        ArrayList<Video> videos = new ArrayList<>();
        String url = "";

        switch (type) {
            // 电影
            case MOVIE_LATEST:
                url = BASE_URL + "&t1=1&pg=" + page;
                break;
            case MOVIE_ACTION:
                url = BASE_URL + "&t=6&pg=" + page;
                break;
            case MOVIE_COMEDY:
                url = BASE_URL + "&t=7&pg=" + page;
                break;
            case MOVIE_LOVE:
                url = BASE_URL + "&t=8&pg=" + page;
                break;
            case MOVIE_SCIENCE:
                url = BASE_URL + "&t=9&pg=" + page;
                break;
            case MOVIE_SCARY:
                url = BASE_URL + "&t=10&pg=" + page;
                break;
            case MOVIE_STORY:
                url = BASE_URL + "&t=11&pg=" + page;
                break;
            case MOVIE_WAR:
                url = BASE_URL + "&t=12&pg=" + page;
                break;
            // 电视剧
            case TELEPLAY_LATEST:
                url = BASE_URL + "&t1=2&pg=" + page;
                break;
            case TELEPLAY_CHINA:
                url = BASE_URL + "&t=13&pg=" + page;
                break;
            case TELEPLAY_HONGKONG:
                url = BASE_URL + "&t=14&pg=" + page;
                break;
            case TELEPLAY_KOREA:
                url = BASE_URL + "&t=15&pg=" + page;
                break;
            case TELEPLAY_JAPAN:
                url = BASE_URL + "&t=16&pg=" + page;
                break;
            case TELEPLAY_TAIWAN:
                url = BASE_URL + "&t=26&pg=" + page;
                break;
            case TELEPLAY_EA:
                url = BASE_URL + "&t=27&pg=" + page;
                break;
            case TELEPLAY_SGMY:
                url = BASE_URL + "&t=28&pg=" + page;
                break;
            case TELEPLAY_OTHER:
                url = BASE_URL + "&t=29&pg=" + page;
                break;
            // 综艺
            case SHOW_LATEST:
                url = BASE_URL + "&t1=3&pg=" + page;
                break;
            case SHOW_CHINA:
                url = BASE_URL + "&t=30&pg=" + page;
                break;
            case SHOW_JK:
                url = BASE_URL + "&t=31&pg=" + page;
                break;
            case SHOW_HT:
                url = BASE_URL + "&t=32&pg=" + page;
                break;
            case SHOW_EA:
                url = BASE_URL + "&t=33&pg=" + page;
                break;
            // 动漫
            case CARTOON_LATEST:
                url = BASE_URL + "&t1=4&pg=" + page;
                break;
            case CARTOON_CHINA:
                url = BASE_URL + "&t=34&pg=" + page;
                break;
            case CARTOON_JK:
                url = BASE_URL + "&t=35&pg=" + page;
                break;
            case CARTOON_EA:
                url = BASE_URL + "&t=36&pg=" + page;
                break;
            case CARTOON_OTHER:
                url = BASE_URL + "&t=34&pg=" + page;
                break;
        }

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                CMSVideoBean bean = CommonUtils.getGson().fromJson(response.body().string(), CMSVideoBean.class);
                if (bean.getList() != null) {
                    for (int i = 0; i < bean.getList().size(); i++) {
                        Video video = new Video();
                        CMSVideoBean.Item item = bean.getList().get(i);
                            video.setType(type);
                            video.setPageCount(bean.getPagecount());
                            video.setPageItemNum(Integer.parseInt(bean.getLimit()));
                            video.setVodPlayFrom(item.getVod_play_from());
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
                            video.setTypeText(item.getVod_class());
                            video.setDescription(item.getVod_content());
                            video.setImageUrl(item.getVod_pic());
                            video.setYear(item.getVod_year());
                            video.setArea(item.getVod_area());
                            video.setLanguage(item.getVod_lang());


                            // 不同资源
                            String[] from = item.getVod_play_from().split("\\$\\$\\$");

                            // 不同的集数
                            String[] resources = item.getVod_play_url().split("\\$\\$\\$");
                            //Log.wtf("CMSVideoEng", resources[0]);
                            //video.setVodPlayFrom(from[0]);
                            ArrayList<Video.VodSource> vodSources = new ArrayList<>();
                            for(int z=0; z<from.length; z++) {
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
                }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videos;
    }

    @Override
    public ArrayList<Video> getVideos(VideoEngineParam param, int page) {
        ArrayList<Video> videos = new ArrayList<>();
        String url = param.getUrl() + "&pg=" + page;

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();

                CMSVideoBean bean = CommonUtils.getGson().fromJson(result, CMSVideoBean.class);
                if (bean.getList() != null) {
                    for (int i = 0; i < bean.getList().size(); i++) {
                        Video video = new Video();
                        CMSVideoBean.Item item = bean.getList().get(i);
                        video.setVideoEngineParam(param);
                        video.setPageCount(bean.getPagecount());
                        video.setPageItemNum(Integer.parseInt(bean.getLimit()));
                        video.setPage(Integer.parseInt(bean.getPage()));
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

                        video.setTypeText(item.getVod_class());
                        video.setDescription(item.getVod_content());
                        video.setImageUrl(item.getVod_pic());
                        video.setYear(item.getVod_year());
                        video.setArea(item.getVod_area());
                        video.setLanguage(item.getVod_lang());


                        // 不同资源
                        String[] from = item.getVod_play_from().split("\\$\\$\\$");

                        // 不同的集数
                        String[] resources = item.getVod_play_url().split("\\$\\$\\$");
                        //Log.wtf("CMSVideoEng", resources[0]);
                        //video.setVodPlayFrom(from[0]);
                        ArrayList<Video.VodSource> vodSources = new ArrayList<>();
                        for(int z=0; z<from.length; z++) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videos;
    }
}
