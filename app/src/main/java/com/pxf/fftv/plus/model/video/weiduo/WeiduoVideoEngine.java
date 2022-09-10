package com.pxf.fftv.plus.model.video.weiduo;

import android.util.Log;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.common.CommonUtils;
import com.pxf.fftv.plus.model.IVideoEngine;
import com.pxf.fftv.plus.model.VideoEngineParam;
import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

import static com.pxf.fftv.plus.Const.LOG_TAG;

public class WeiduoVideoEngine implements IVideoEngine {

    private volatile static WeiduoVideoEngine mInstance;

    private WeiduoVideoEngine() {
    }

    public static WeiduoVideoEngine getInstance() {
        if (mInstance == null) {
            synchronized (WeiduoVideoEngine.class) {
                if (mInstance == null) {
                    mInstance = new WeiduoVideoEngine();
                }
            }
        }
        return mInstance;
    }

    private static final String BASE_URL = "http://video.api.vitocms.cn:997";
    private static final String TOKEN = "host=99.meetpt.cn&token=86320531fb9aba3e5c0fce946c7fb090";
    private static final String MOVIE_URL = BASE_URL + "/api/v2/movie/list?" + TOKEN;
    private static final String TELEPLAY_URL = BASE_URL + "/api/v2/dianshi/list?" + TOKEN;
    private static final String CARTOON_URL = BASE_URL + "/api/v2/dongman/list?" + TOKEN;
    private static final String SHOW_URL = BASE_URL + "/api/v2/zongyi/list?" + TOKEN;
    public static final String ANALYSIS_URL = BASE_URL + "/api/v2/play?" + TOKEN + "&id=";
    // 改为从后台获取
    // public static final String ANALYSIS_PLAY_URL = "http://k377.cc/json/?url=";

    @Override
    public ArrayList<Video> getVideos(Const.VideoType type, int page) {
        ArrayList<Video> videos = new ArrayList<>();
        String url = "";
        switch (type) {
            // 电影
            case MOVIE_LATEST:
                url = MOVIE_URL + "&page=" + page;
                break;
            case MOVIE_ACTION:
                url = MOVIE_URL + "&cat=106&page=" + page;
                break;
            case MOVIE_COMEDY:
                url = MOVIE_URL + "&cat=103&page=" + page;
                break;
            case MOVIE_LOVE:
                url = MOVIE_URL + "&cat=100&page=" + page;
                break;
            case MOVIE_SCARY:
                url = MOVIE_URL + "&cat=102&page=" + page;
                break;
            case MOVIE_SCIENCE:
                url = MOVIE_URL + "&cat=104&page=" + page;
                break;
            case MOVIE_STORY:
                url = MOVIE_URL + "&cat=112&page=" + page;
                break;
            case MOVIE_WAR:
                url = MOVIE_URL + "&cat=108&page=" + page;
                break;
            // 电视剧
            case TELEPLAY_LATEST:
                url = TELEPLAY_URL + "&page=" + page;
                break;
            case TELEPLAY_EA:
                url = TELEPLAY_URL + "&area=17&page=" + page;
                break;
            case TELEPLAY_CHINA:
                url = TELEPLAY_URL + "&area=10&page=" + page;
                break;
            case TELEPLAY_JAPAN:
                url = TELEPLAY_URL + "&area=15&page=" + page;
                break;
            case TELEPLAY_KOREA:
                url = TELEPLAY_URL + "&area=12&page=" + page;
                break;
            case TELEPLAY_OTHER:
                url = TELEPLAY_URL + "&area=14&page=" + page;
                break;
            case TELEPLAY_TAIWAN:
                url = TELEPLAY_URL + "&area=16&page=" + page;
                break;
            case TELEPLAY_HONGKONG:
                url = TELEPLAY_URL + "&area=11&page=" + page;
                break;
            // 动漫
            case CARTOON_LATEST:
                url = CARTOON_URL + "&page=" + page;
                break;
            case CARTOON_EA:
                url = CARTOON_URL + "&area=12&page=" + page;
                break;
            case CARTOON_JK:
                url = CARTOON_URL + "&area=11&page=" + page;
                break;
            case CARTOON_CHINA:
                url = CARTOON_URL + "&area=10&page=" + page;
                break;
            case CARTOON_OTHER:
                url = CARTOON_URL + "&cat=127&page=" + page;
                break;
            // 综艺
            case SHOW_LATEST:
                url = SHOW_URL + "&page=" + page;
                break;
            case SHOW_CHINA:
                url = SHOW_URL + "&area=10&page=" + page;
                break;
            case SHOW_EA:
                url = SHOW_URL + "&area=14&page=" + page;
                break;
            case SHOW_HT:
                url = SHOW_URL + "&area=11&page=" + page;
                break;
            case SHOW_JK:
                url = SHOW_URL + "&area=12&page=" + page;
                break;
        }

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                WeiduoVideoBean bean = CommonUtils.getGson().fromJson(result, WeiduoVideoBean.class);
                if (bean.getError() == 0) {
                    if (bean.getData() != null && bean.getData().getList() != null) {
                        for (int i = 0; i < bean.getData().getList().size(); i++) {
                            Video video = new Video();
                            video.setType(type);

                            WeiduoVideoBean.DataBean.ListBean item = bean.getData().getList().get(i);

                            video.setPageItemNum(bean.getData().getPage_size());
                            video.setPageCount(bean.getData().getTotal() / bean.getData().getPage_size());
                            video.setTitle(item.getTitle());
                            video.setWeiduoUrl(item.getUrl());
                            video.setImageUrl(item.getImg());
                            video.setYear(item.getStatus());

                            ArrayList<Video.Actor> actors = new ArrayList<>();
                            String[] stars = item.getStar().split(" ");
                            for (String s : stars) {
                                Video.Actor actor = new Video.Actor();
                                actor.setName(s);
                                actors.add(actor);
                            }

                            video.setActors(actors);
                            videos.add(video);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "getVideos error ", e);
            return getVideos(type, page + 1);
        }

        return videos;
    }

    @Override
    public ArrayList<Video> getVideos(VideoEngineParam param, int page) {
        ArrayList<Video> videos = new ArrayList<>();
        String url = param.getUrl() + "&page=" + page;

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                WeiduoVideoBean bean = CommonUtils.getGson().fromJson(result, WeiduoVideoBean.class);
                if (bean.getError() == 0) {
                    if (bean.getData() != null && bean.getData().getList() != null) {
                        for (int i = 0; i < bean.getData().getList().size(); i++) {
                            Video video = new Video();
                            video.setVideoEngineParam(param);

                            WeiduoVideoBean.DataBean.ListBean item = bean.getData().getList().get(i);

                            video.setPageItemNum(bean.getData().getPage_size());
                            video.setPageCount(bean.getData().getTotal() / bean.getData().getPage_size());
                            video.setTitle(item.getTitle());
                            video.setWeiduoUrl(item.getUrl());
                            video.setImageUrl(item.getImg());
                            video.setYear(item.getStatus());

                            ArrayList<Video.Actor> actors = new ArrayList<>();
                            String[] stars = item.getStar().split(" ");
                            for (String s : stars) {
                                Video.Actor actor = new Video.Actor();
                                actor.setName(s);
                                actors.add(actor);
                            }

                            video.setActors(actors);
                            videos.add(video);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "getVideos error ", e);
        }

        return videos;
    }
}
