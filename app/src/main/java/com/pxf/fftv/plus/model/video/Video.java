package com.pxf.fftv.plus.model.video;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.model.VideoEngineParam;

import java.io.Serializable;
import java.util.ArrayList;

public class Video implements Serializable {

    private static final long serialVersionUID = -2877968125244128597L;

    private int id; // MacCMS vod_id

    private String title;

    private String description;

    private ArrayList<Director> directors;

    private ArrayList<Actor> actors;

    private ArrayList<VodSource> sources;

    private ArrayList<Part> parts;

    private String year;

    private Const.VideoType type;

    private String typeText;

    private String imageUrl;

    private String area;

    private String language;

    private String directorText;

    private String actorText;

    private String typeName;

    private String vodPlayFrom;

    // 维多专用
    private String weiduoUrl;

    // 总页数
    private int pageCount;

    // 一页的数量
    private int pageItemNum;

    // Fixed page loading issue
    private int page;

    // VOD remarks (update status like "更新至第24集", "HD", "完结")
    private String remarks;

    private VideoEngineParam videoEngineParam;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Director> getDirectors() {
        return directors;
    }

    public void setDirectors(ArrayList<Director> directors) {
        this.directors = directors;
    }

    public ArrayList<Actor> getActors() {
        return actors;
    }

    public void setActors(ArrayList<Actor> actors) {
        this.actors = actors;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public void setParts(ArrayList<Part> parts) {
        this.parts = parts;
    }

    // 20220910 - Added new feature sorting
    public void synParts(Video video, int position) {
        this.parts = video.getParts();
        this.getVodSource().get(position).part = video.getParts();
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Const.VideoType getType() {
        return type;
    }

    public void setType(Const.VideoType type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageItemNum() {
        return pageItemNum;
    }

    public void setPageItemNum(int pageItemNum) {
        this.pageItemNum = pageItemNum;
    }

    public String getVodPlayFrom() {
        return vodPlayFrom;
    }

    public void setVodPlayFrom(String vodPlayFrom) {
        this.vodPlayFrom = vodPlayFrom;
    }

    // Fixed page loading issue
    public int getPage() {
        return page;
    }

    // Fixed page loading issue
    public void setPage(int page) {
        this.page = page;
    }

    public String getWeiduoUrl() {
        return weiduoUrl;
    }

    public void setWeiduoUrl(String weiduoUrl) {
        this.weiduoUrl = weiduoUrl;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getDirectorText() {
        return directorText;
    }

    public void setDirectorText(String directorText) {
        this.directorText = directorText;
    }

    public String getActorText() {
        return actorText;
    }

    public void setActorText(String actorText) {
        this.actorText = actorText;
    }

    public VideoEngineParam getVideoEngineParam() {
        return videoEngineParam;
    }

    public void setVideoEngineParam(VideoEngineParam videoEngineParam) {
        this.videoEngineParam = videoEngineParam;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ArrayList<VodSource> getVodSource() {
        return sources;
    }

    public void setVodSource(ArrayList<VodSource> sources) {
        this.sources = sources;
    }

    public static class Director implements Serializable {

        private static final long serialVersionUID = 998358480289322234L;

        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Actor implements Serializable {

        private static final long serialVersionUID = 6105685485863835909L;

        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Part implements Serializable {

        private static final long serialVersionUID = 4977297726929376318L;

        String title;

        String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class VodSource implements Serializable {
        public String sourceName;
        public ArrayList<Video.Part> part;
    }
}
