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

    // Use method to get current BASE_URL dynamically
    private static String getApiBase() {
        String apiBase = Const.BASE_URL + "/api.php/provide/vod/";
        Log.d("CMSVideoEngine", "Using API Base: " + apiBase + " (BASE_URL=" + Const.BASE_URL + ")");
        return apiBase;
    }

    /**
     * Format source name to be more user-friendly
     * Returns the actual source name from the API
     */
    private String formatSourceName(String rawName, int index) {
        // Return the actual source name (wj, mt, etc.) instead of generic "源 X"
        return rawName;
    }

    /**
     * Build URL for video list by category
     * Using ac=list to get basic info + vod_id
     */
    private String buildListUrl(int categoryId, int page) {
        return getApiBase() + "?ac=list&t=" + categoryId + "&pg=" + page;
    }

    /**
     * Build URL for category list with time filter
     */
    private String buildListUrl(int categoryId, int page, int hours) {
        return getApiBase() + "?ac=list&t=" + categoryId + "&pg=" + page + "&h=" + hours;
    }

    /**
     * Build URL for search by keyword
     */
    private String buildSearchUrl(String keyword, int page) {
        return getApiBase() + "?ac=detail&wd=" + keyword + "&pg=" + page;
    }

    /**
     * Build URL for detail by IDs
     */
    private String buildDetailUrl(String ids) {
        // Use ac=detail to get full video details including play URLs
        String url = getApiBase() + "?ac=detail&ids=" + ids;
        Log.d("CMSVideoEngine", "Detail URL: " + url);
        return url;
    }

    @Override
    public ArrayList<Video> getVideos(Const.VideoType type, int page) {
        ArrayList<Video> videos = new ArrayList<>();
        String url = "";

        switch (type) {
            // 电影
            case MOVIE_LATEST:
                url = buildListUrl(1, page); // Parent category ID
                break;
            case MOVIE_ACTION:
                url = buildListUrl(8, page); // Fixed: was 6, correct is 8
                break;
            case MOVIE_COMEDY:
                url = buildListUrl(9, page); // Fixed: was 7, correct is 9
                break;
            case MOVIE_LOVE:
                url = buildListUrl(10, page); // Fixed: was 8, correct is 10
                break;
            case MOVIE_SCIENCE:
                url = buildListUrl(11, page); // Fixed: was 9, correct is 11
                break;
            case MOVIE_SCARY:
                url = buildListUrl(12, page); // Fixed: was 10, correct is 12
                break;
            case MOVIE_STORY:
                url = buildListUrl(13, page); // Fixed: was 11, correct is 13
                break;
            case MOVIE_WAR:
                url = buildListUrl(14, page); // Fixed: was 12, correct is 14
                break;
            // 电视剧
            case TELEPLAY_LATEST:
                url = buildListUrl(2, page); // Parent category ID
                break;
            case TELEPLAY_CHINA:
                url = buildListUrl(15, page); // Fixed: was 13, correct is 15 (国产剧)
                break;
            case TELEPLAY_HONGKONG:
                url = buildListUrl(18, page); // Fixed: was 14, needs update based on API
                break;
            case TELEPLAY_KOREA:
                url = buildListUrl(21, page); // Fixed: was 15, correct is 21 (韩国剧)
                break;
            case TELEPLAY_JAPAN:
                url = buildListUrl(22, page); // Fixed: was 16, correct is 22 (日本剧)
                break;
            case TELEPLAY_TAIWAN:
                url = buildListUrl(26, page); // Keep as-is if correct
                break;
            case TELEPLAY_EA:
                url = buildListUrl(18, page); // Fixed: was 27, correct is 18 (美国剧)
                break;
            case TELEPLAY_SGMY:
                url = buildListUrl(28, page); // Keep as-is if this is correct
                break;
            case TELEPLAY_OTHER:
                url = buildListUrl(29, page); // Keep as-is if this is correct
                break;
            // 综艺
            case SHOW_LATEST:
                url = buildListUrl(3, page); // Parent category ID
                break;
            case SHOW_CHINA:
                url = buildListUrl(24, page); // Fixed: was 30, correct is 24 (大陆综艺)
                break;
            case SHOW_JK:
                url = buildListUrl(25, page); // Fixed: was 31, correct is 25 (日韩综艺)
                break;
            case SHOW_HT:
                url = buildListUrl(26, page); // Fixed: was 32, correct is 26 (港台综艺)
                break;
            case SHOW_EA:
                url = buildListUrl(33, page); // Keep as-is if this is correct
                break;
            // 动漫
            case CARTOON_LATEST:
                url = buildListUrl(4, page); // Parent category ID
                break;
            case CARTOON_CHINA:
                url = buildListUrl(28, page); // Fixed: was 34, correct is 28 (国产动漫)
                break;
            case CARTOON_JK:
                url = buildListUrl(29, page); // Fixed: was 35, correct is 29 (日韩动漫)
                break;
            case CARTOON_EA:
                url = buildListUrl(30, page); // Fixed: was 36, correct is 30 (欧美动漫)
                break;
            case CARTOON_OTHER:
                url = buildListUrl(28, page); // Same as CARTOON_CHINA
                break;
        }

        Request request = new Request.Builder().url(url).build();

        Response response = null;
        try {
            response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                CMSVideoBean bean = CommonUtils.getGson().fromJson(response.body().string(), CMSVideoBean.class);
                if (bean.getList() != null) {
                    for (int i = 0; i < bean.getList().size(); i++) {
                        Video video = new Video();
                        CMSVideoBean.Item item = bean.getList().get(i);

                        // Parse vod_id from API
                        video.setId(item.getVod_id());

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
                        if (item.getVod_class() != null && !item.getVod_class().isEmpty()) {
                            video.setTypeText(item.getVod_class());
                        } else {
                            video.setTypeText(item.getType_name());
                        }
                        if (item.getVod_blurb() != null && !item.getVod_blurb().isEmpty()) {
                            video.setDescription(item.getVod_blurb());
                        } else if (item.getVod_content() != null && !item.getVod_content().isEmpty()) {
                            video.setDescription(item.getVod_content());
                        }
                        video.setImageUrl(item.getVod_pic());
                        video.setYear(item.getVod_year());
                        video.setArea(item.getVod_area());
                        video.setLanguage(item.getVod_lang());
                        video.setRemarks(item.getVod_remarks());

                        // 不同资源
                        String[] from = item.getVod_play_from().split("\\$\\$\\$");

                        // 不同的集数
                        String[] resources = item.getVod_play_url().split("\\$\\$\\$");

                        // Log.wtf("CMSVideoEng", resources[0]);
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
                            source.sourceName = formatSourceName(from[z], z);
                            source.part = parts;
                            vodSources.add(source);

                            video.setParts(parts);
                        }
                        video.setVodSource(vodSources);
                        videos.add(video);
                    }
                }
            }
            // }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.close();
        }

        return videos;
    }

    @Override
    public ArrayList<Video> getVideos(VideoEngineParam param, int page) {
        ArrayList<Video> videos = new ArrayList<>();
        String url = param.getUrl() + "&pg=" + page;

        Request request = new Request.Builder().url(url).build();

        Response response = null;
        try {
            response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();

                CMSVideoBean bean = CommonUtils.getGson().fromJson(result, CMSVideoBean.class);
                if (bean.getList() != null) {
                    for (int i = 0; i < bean.getList().size(); i++) {
                        Video video = new Video();
                        CMSVideoBean.Item item = bean.getList().get(i);

                        // Parse vod_id from API
                        video.setId(item.getVod_id());

                        video.setVideoEngineParam(param);
                        video.setPageCount(bean.getPagecount());
                        video.setPageItemNum(Integer.parseInt(bean.getLimit()));
                        video.setPage(Integer.parseInt(bean.getPage()));
                        video.setTitle(item.getVod_name());

                        ArrayList<Video.Actor> actors = new ArrayList<>();
                        if (item.getVod_actor() != null && !item.getVod_actor().isEmpty()) {
                            String[] stars = item.getVod_actor().split(",");
                            for (String s : stars) {
                                Video.Actor actor = new Video.Actor();
                                actor.setName(s);
                                actors.add(actor);
                            }
                        }
                        video.setActors(actors);

                        ArrayList<Video.Director> directors = new ArrayList<>();
                        if (item.getVod_director() != null && !item.getVod_director().isEmpty()) {
                            Video.Director director = new Video.Director();
                            director.setName(item.getVod_director());
                            directors.add(director);
                        }
                        video.setDirectors(directors);

                        if (item.getVod_class() != null && !item.getVod_class().isEmpty()) {
                            video.setTypeText(item.getVod_class());
                        } else {
                            video.setTypeText(item.getType_name());
                        }
                        if (item.getVod_blurb() != null && !item.getVod_blurb().isEmpty()) {
                            video.setDescription(item.getVod_blurb());
                        } else if (item.getVod_content() != null && !item.getVod_content().isEmpty()) {
                            video.setDescription(item.getVod_content());
                        }
                        video.setImageUrl(item.getVod_pic());
                        video.setYear(item.getVod_year());
                        video.setArea(item.getVod_area());
                        video.setLanguage(item.getVod_lang());
                        video.setRemarks(item.getVod_remarks());

                        // 不同资源 - Add null safety for ac=list responses
                        ArrayList<Video.VodSource> vodSources = new ArrayList<>();
                        if (item.getVod_play_from() != null && !item.getVod_play_from().isEmpty()
                                && item.getVod_play_url() != null && !item.getVod_play_url().isEmpty()) {
                            String[] from = item.getVod_play_from().split("\\$\\$\\$");
                            String[] resources = item.getVod_play_url().split("\\$\\$\\$");

                            for (int z = 0; z < from.length; z++) {
                                ArrayList<Video.Part> parts = new ArrayList<>();
                                Video.VodSource source = new Video.VodSource();
                                if (z < resources.length) {
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
                                }
                                source.sourceName = formatSourceName(from[z], z);
                                source.part = parts;
                                vodSources.add(source);
                                video.setParts(parts);
                            }
                        }
                        video.setVodSource(vodSources);
                        videos.add(video);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.close();
        }

        return videos;
    }

    /**
     * Search videos by keyword
     * 
     * @param keyword Search keyword
     * @param page    Page number
     * @return List of matching videos
     */
    public ArrayList<Video> searchVideos(String keyword, int page) {
        ArrayList<Video> videos = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return videos;
        }

        String url = buildSearchUrl(keyword, page);
        Request request = new Request.Builder().url(url).build();

        Response response = null;
        try {
            response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                CMSVideoBean bean = CommonUtils.getGson().fromJson(response.body().string(), CMSVideoBean.class);
                if (bean.getList() != null) {
                    for (int i = 0; i < bean.getList().size(); i++) {
                        Video video = new Video();
                        CMSVideoBean.Item item = bean.getList().get(i);

                        // Parse vod_id from API
                        video.setId(item.getVod_id());

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

                        if (item.getVod_class() != null && !item.getVod_class().isEmpty()) {
                            video.setTypeText(item.getVod_class());
                        } else {
                            video.setTypeText(item.getType_name());
                        }
                        if (item.getVod_blurb() != null && !item.getVod_blurb().isEmpty()) {
                            video.setDescription(item.getVod_blurb());
                        } else if (item.getVod_content() != null && !item.getVod_content().isEmpty()) {
                            video.setDescription(item.getVod_content());
                        }
                        video.setImageUrl(item.getVod_pic());
                        video.setYear(item.getVod_year());
                        video.setArea(item.getVod_area());
                        video.setLanguage(item.getVod_lang());
                        video.setRemarks(item.getVod_remarks());

                        String[] from = item.getVod_play_from().split("\\$\\$\\$");
                        String[] resources = item.getVod_play_url().split("\\$\\$\\$");
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
                            source.sourceName = formatSourceName(from[z], z);
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
        } finally {
            if (response != null)
                response.close();
        }

        return videos;
    }

    /**
     * Get complete video details by ID (used when ac=list video needs play URLs)
     */
    public Video getVideoDetail(int vodId) {
        String url = buildDetailUrl(String.valueOf(vodId));
        Log.d("CMSVideoEngine", "Fetching detail for vod_id: " + vodId + ", URL: " + url);

        Request request = new Request.Builder().url(url).build();

        Response response = null;
        try {
            response = CommonUtils.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                CMSVideoBean bean = CommonUtils.getGson().fromJson(response.body().string(), CMSVideoBean.class);
                if (bean.getList() != null && !bean.getList().isEmpty()) {
                    // Assuming that for a detail request by ID, the list will contain only one item
                    CMSVideoBean.Item item = bean.getList().get(0);
                    Video video = new Video();

                    // Parse vod_id from API
                    video.setId(item.getVod_id());

                    // Set basic info
                    video.setTitle(item.getVod_name());
                    video.setImageUrl(item.getVod_pic());
                    if (item.getVod_class() != null && !item.getVod_class().isEmpty()) {
                        video.setTypeText(item.getVod_class());
                    } else {
                        video.setTypeText(item.getType_name());
                    }
                    if (item.getVod_blurb() != null && !item.getVod_blurb().isEmpty()) {
                        video.setDescription(item.getVod_blurb());
                    } else if (item.getVod_content() != null && !item.getVod_content().isEmpty()) {
                        video.setDescription(item.getVod_content());
                    }
                    video.setYear(item.getVod_year());
                    video.setArea(item.getVod_area());
                    video.setLanguage(item.getVod_lang());
                    video.setRemarks(item.getVod_remarks());

                    // Set actors
                    ArrayList<Video.Actor> actors = new ArrayList<>();
                    if (item.getVod_actor() != null && !item.getVod_actor().isEmpty()) {
                        String[] stars = item.getVod_actor().split(",");
                        for (String s : stars) {
                            Video.Actor actor = new Video.Actor();
                            actor.setName(s);
                            actors.add(actor);
                        }
                    }
                    video.setActors(actors);

                    // Set directors
                    ArrayList<Video.Director> directors = new ArrayList<>();
                    if (item.getVod_director() != null && !item.getVod_director().isEmpty()) {
                        Video.Director director = new Video.Director();
                        director.setName(item.getVod_director());
                        directors.add(director);
                    }
                    video.setDirectors(directors);

                    if (item.getVod_blurb() != null && !item.getVod_blurb().isEmpty()) {
                        video.setDescription(item.getVod_blurb());
                    } else if (item.getVod_content() != null && !item.getVod_content().isEmpty()) {
                        video.setDescription(item.getVod_content());
                    }

                    // Debug logging to see what play data we have
                    Log.d("CMSVideoEngine", "Video: " + item.getVod_name());
                    Log.d("CMSVideoEngine", "  vod_year: " + item.getVod_year());
                    Log.d("CMSVideoEngine", "  vod_area: " + item.getVod_area());
                    Log.d("CMSVideoEngine", "  vod_lang: " + item.getVod_lang());
                    Log.d("CMSVideoEngine", "  vod_content: "
                            + (item.getVod_content() == null ? "NULL" : "LENGTH=" + item.getVod_content().length()));

                    // Set play sources
                    ArrayList<Video.VodSource> vodSources = new ArrayList<>();
                    if (item.getVod_play_url() != null && !item.getVod_play_url().isEmpty()) {
                        String[] resources = item.getVod_play_url().split("\\$\\$\\$");
                        String[] from;

                        if (item.getVod_play_from() != null && !item.getVod_play_from().isEmpty()) {
                            from = item.getVod_play_from().split("\\$\\$\\$");
                        } else {
                            // If vod_play_from is empty, generate default source names
                            from = new String[resources.length];
                            for (int k = 0; k < resources.length; k++) {
                                from[k] = "默认播放源 " + (k + 1);
                            }
                        }

                        // Use the size of 'from' array, but ensure we cover all resources if 'from' is
                        // synthesized or matching
                        // If real 'from' is provided but shorter than resources, we might miss some
                        // resources if we loop by from.length.
                        // But standard behavior usually implies they match.
                        // Reliable fallback: max of both, ensuring we handle index bounds.
                        int loopCount = Math.max(from.length, resources.length);

                        for (int z = 0; z < loopCount; z++) {
                            ArrayList<Video.Part> parts = new ArrayList<>();
                            Video.VodSource source = new Video.VodSource();

                            // Get source name safely and format it
                            if (z < from.length) {
                                source.sourceName = formatSourceName(from[z], z);
                            } else {
                                source.sourceName = "源 " + (z + 1);
                            }

                            if (z < resources.length) {
                                String[] sets = resources[z].split("\\#");
                                for (int j = 0; j < sets.length; j++) {
                                    String[] urlArray = sets[j].split("\\$");
                                    if (urlArray.length >= 2) { // Changed to >= 2 to be safer
                                        Video.Part part = new Video.Part();
                                        part.setTitle(urlArray[0]);
                                        part.setUrl(urlArray[1]);
                                        parts.add(part);
                                    } else if (urlArray.length == 1 && sets[j].endsWith(".m3u8")) {
                                        // Handle case where it might just be the URL without title
                                        Video.Part part = new Video.Part();
                                        part.setTitle("第" + (j + 1) + "集");
                                        part.setUrl(urlArray[0]);
                                        parts.add(part);
                                    }
                                }
                            }
                            source.part = parts;
                            vodSources.add(source);
                        }
                    }
                    video.setVodSource(vodSources);

                    Log.d("CMSVideoEngine", "Detail loaded: " + vodSources.size() + " sources");
                    return video;
                }
            }
        } catch (Exception e) {
            Log.e("CMSVideoEngine", "Error fetching video detail", e);
            e.printStackTrace();
        } finally {
            if (response != null)
                response.close();
        }

        return null;
    }
}
