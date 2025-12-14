package com.pxf.fftv.plus.bean;

import java.util.List;

public class MatchBean {
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<ListBean> dataList;

        public List<ListBean> getList() {
            return dataList;
        }

        public void setList(List<ListBean> dataList) {
            this.dataList = dataList;
        }

        public static class ListBean {
            private String matchtime;
            private String hteam_name;
            private String ateam_name;
            private String hteam_logo;
            private String ateam_logo;
            private String video_url;
            private List<UrlBean> live_urls;
            private List<UrlBean> mirror_live_urls;
            private int status_up; // 1=upcoming, 2=playing, 10=finished
            private String kick_off_time; // Match time

            public String getMatchtime() {
                return matchtime;
            }

            public void setMatchtime(String matchtime) {
                this.matchtime = matchtime;
            }

            public int getStatus_up() {
                return status_up;
            }

            public void setStatus_up(int status_up) {
                this.status_up = status_up;
            }

            public String getKick_off_time() {
                return kick_off_time;
            }

            public void setKick_off_time(String kick_off_time) {
                this.kick_off_time = kick_off_time;
            }

            public String getHteam_name() {
                return hteam_name;
            }

            public void setHteam_name(String hteam_name) {
                this.hteam_name = hteam_name;
            }

            public String getAteam_name() {
                return ateam_name;
            }

            public void setAteam_name(String ateam_name) {
                this.ateam_name = ateam_name;
            }

            public String getHteam_logo() {
                return hteam_logo;
            }

            public void setHteam_logo(String hteam_logo) {
                this.hteam_logo = hteam_logo;
            }

            public String getAteam_logo() {
                return ateam_logo;
            }

            public void setAteam_logo(String ateam_logo) {
                this.ateam_logo = ateam_logo;
            }

            public String getVideo_url() {
                return video_url;
            }

            public void setVideo_url(String video_url) {
                this.video_url = video_url;
            }

            public List<UrlBean> getLive_urls() {
                return live_urls;
            }

            public void setLive_urls(List<UrlBean> live_urls) {
                this.live_urls = live_urls;
            }

            public List<UrlBean> getMirror_live_urls() {
                return mirror_live_urls;
            }

            public void setMirror_live_urls(List<UrlBean> mirror_live_urls) {
                this.mirror_live_urls = mirror_live_urls;
            }

            public static class UrlBean {
                private String url;
                private String name; // Optional, if available

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        }
    }
}
