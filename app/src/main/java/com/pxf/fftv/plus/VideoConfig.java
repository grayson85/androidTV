package com.pxf.fftv.plus;

import java.io.Serializable;

public class VideoConfig {

        // Use method instead of constant to get current BASE_URL value
        private static String getBaseCmsUrl() {
                return Const.BASE_URL + "/api.php/provide/vod/?ac=list";
        }

        private static final String BASE_OK_URL = "http://www.apiokzy.com/inc/feifei3";
        private static final String BASE_ZD_URL = "http://www.zdziyuan.com/inc/feifei3.4";

        private static final String WEIDUO_BASE_URL = "http://video.api.vitocms.cn:997";
        private static final String WEIDUO_TOKEN = "host=99.meetpt.cn&token=86320531fb9aba3e5c0fce946c7fb090";
        private static final String WEIDUO_MOVIE_URL = WEIDUO_BASE_URL + "/api/v2/movie/list?" + WEIDUO_TOKEN;
        private static final String WEIDUO_TELEPLAY_URL = WEIDUO_BASE_URL + "/api/v2/dianshi/list?" + WEIDUO_TOKEN;
        private static final String WEIDUO_CARTOON_URL = WEIDUO_BASE_URL + "/api/v2/dongman/list?" + WEIDUO_TOKEN;
        private static final String WEIDUO_SHOW_URL = WEIDUO_BASE_URL + "/api/v2/zongyi/list?" + WEIDUO_TOKEN;
        public static final String WEIDUO_ANALYSIS_URL = WEIDUO_BASE_URL + "/api/v2/play?" + WEIDUO_TOKEN + "&id=";
        // 改为从CMS后台获取
        // public static final String WEIDUO_ANALYSIS_PLAY_URL =
        // "http://k377.cc/json/?url=";

        public static class Video1 implements Serializable {

                private static final long serialVersionUID = 185060226246835170L;

                private String title;

                private boolean hideSubTitle;

                private boolean hideAD;

                private Video2[] video2s;

                public Video1(String title, boolean hideSubTitle, boolean hideAD, Video2[] video2s) {
                        this.title = title;
                        this.hideSubTitle = hideSubTitle;
                        this.hideAD = hideAD;
                        this.video2s = video2s;
                }

                public boolean isHideSubTitle() {
                        return hideSubTitle;
                }

                public boolean isHideAD() {
                        return hideAD;
                }

                public String getTitle() {
                        return title;
                }

                public Video2[] getVideo2s() {
                        return video2s;
                }
        }

        public static class Video2 implements Serializable {

                private static final long serialVersionUID = 2286931107904413875L;

                private String title;

                private String subTitle;

                private String url;

                public Video2(String title, String subTitle, String url) {
                        this.title = title;
                        this.subTitle = subTitle;
                        this.url = url;
                }

                public String getTitle() {
                        return title;
                }

                public String getSubTitle() {
                        return subTitle;
                }

                public String getUrl() {
                        return url;
                }
        }

        public static class VideoScreen implements Serializable {

                private static final long serialVersionUID = -119171082092932416L;

                private String title;

                private String baseUrl;

                private VideoScreenSub[] types;

                private VideoScreenSub[] years;

                private VideoScreenSub[] acts;

                private VideoScreenSub[] languages;

                private VideoScreenSub[] areas;

                private VideoScreenSub[] ranks;

                public VideoScreen(String title, String baseUrl, VideoScreenSub[] types, VideoScreenSub[] years,
                                VideoScreenSub[] acts, VideoScreenSub[] languages, VideoScreenSub[] areas,
                                VideoScreenSub[] ranks) {
                        this.title = title;
                        this.baseUrl = baseUrl;
                        this.types = types;
                        this.years = years;
                        this.acts = acts;
                        this.languages = languages;
                        this.areas = areas;
                        this.ranks = ranks;
                }

                public String getTitle() {
                        return title;
                }

                public String getBaseUrl() {
                        return baseUrl;
                }

                public VideoScreenSub[] getTypes() {
                        return types;
                }

                public VideoScreenSub[] getYears() {
                        return years;
                }

                public VideoScreenSub[] getActs() {
                        return acts;
                }

                public VideoScreenSub[] getLanguages() {
                        return languages;
                }

                public VideoScreenSub[] getAreas() {
                        return areas;
                }

                public VideoScreenSub[] getRanks() {
                        return ranks;
                }
        }

        public static class VideoScreenSub implements Serializable {

                private static final long serialVersionUID = 7468958629514702844L;

                private String title;

                private String url;

                private boolean enable;

                public VideoScreenSub(String title, String url) {
                        this(title, url, true);
                }

                public VideoScreenSub(String title, String url, boolean enable) {
                        this.title = title;
                        this.url = url;
                        this.enable = enable;
                }

                public String getTitle() {
                        return title;
                }

                public String getUrl() {
                        return url;
                }

                public boolean isEnable() {
                        return enable;
                }
        }

        /**
         * 视频分类
         */
        public static final Video1[] VIDEO_CONFIG_WEIDUO = new Video1[] {
                        new Video1("精选", true, false, new Video2[] {
                                        new Video2("电影", "电影", WEIDUO_MOVIE_URL),
                                        new Video2("电视", "电视", WEIDUO_TELEPLAY_URL),
                                        new Video2("综艺", "综艺", WEIDUO_SHOW_URL),
                                        new Video2("动漫", "动漫", WEIDUO_CARTOON_URL),
                        }),
                        new Video1("电影", false, false, new Video2[] {
                                        new Video2("新片上架", "最新电影", WEIDUO_MOVIE_URL),
                                        new Video2("动作", "动作电影", WEIDUO_MOVIE_URL + "&cat=106"),
                                        new Video2("喜剧", "喜剧电影", WEIDUO_MOVIE_URL + "&cat=103"),
                                        new Video2("爱情", "爱情电影", WEIDUO_MOVIE_URL + "&cat=100"),
                                        new Video2("科幻", "科幻电影", WEIDUO_MOVIE_URL + "&cat=104"),
                                        new Video2("恐怖", "恐怖电影", WEIDUO_MOVIE_URL + "&cat=102"),
                                        new Video2("剧情", "剧情电影", WEIDUO_MOVIE_URL + "&cat=112"),
                                        new Video2("战争", "战争电影", WEIDUO_MOVIE_URL + "&cat=108"),
                                        new Video2("奇幻", "战争电影", WEIDUO_MOVIE_URL + "&cat=113"),
                                        new Video2("悬疑", "战争电影", WEIDUO_MOVIE_URL + "&cat=115")
                        }),
                        new Video1("电视剧", false, false, new Video2[] {
                                        new Video2("新剧上新", "最新剧集", WEIDUO_TELEPLAY_URL),
                                        new Video2("国产剧集", "国产剧集", WEIDUO_TELEPLAY_URL + "&area=10"),
                                        new Video2("香港剧集", "香港剧集", WEIDUO_TELEPLAY_URL + "&area=11"),
                                        new Video2("韩国剧集", "韩国剧集", WEIDUO_TELEPLAY_URL + "&area=12"),
                                        new Video2("美国剧集", "美国剧集", WEIDUO_TELEPLAY_URL + "&area=13"),
                                        new Video2("台湾剧集", "台湾剧集", WEIDUO_TELEPLAY_URL + "&area=16"),
                                        new Video2("日本剧集", "日本剧集", WEIDUO_TELEPLAY_URL + "&area=15"),
                                        new Video2("泰国剧集", "泰国剧集", WEIDUO_TELEPLAY_URL + "&area=14"),
                                        new Video2("英国剧集", "英国剧集", WEIDUO_TELEPLAY_URL + "&area=17"),
                                        new Video2("新加坡剧集", "新加坡剧集", WEIDUO_TELEPLAY_URL + "&area=18")
                        }),
                        new Video1("综艺", false, false, new Video2[] {
                                        new Video2("最新综艺", "最新综艺", WEIDUO_SHOW_URL),
                                        new Video2("大陆综艺", "大陆综艺", WEIDUO_SHOW_URL + "&area=10"),
                                        new Video2("台湾综艺", "台湾综艺", WEIDUO_SHOW_URL + "&area=11"),
                                        new Video2("韩国综艺", "韩国综艺", WEIDUO_SHOW_URL + "&area=12"),
                                        new Video2("日本综艺", "日本综艺", WEIDUO_SHOW_URL + "&area=13"),
                                        new Video2("欧美综艺", "欧美综艺", WEIDUO_SHOW_URL + "&area=14"),
                                        new Video2("香港综艺", "香港综艺", WEIDUO_SHOW_URL + "&area=15")
                        }),
                        new Video1("动漫", false, false, new Video2[] {
                                        new Video2("最新动漫", "最新动漫", WEIDUO_CARTOON_URL),
                                        new Video2("热血动漫", "热血动漫", WEIDUO_CARTOON_URL + "&cat=100"),
                                        new Video2("科幻动漫", "科幻动漫", WEIDUO_CARTOON_URL + "&cat=134"),
                                        new Video2("美少女动漫", "美少女动漫", WEIDUO_CARTOON_URL + "&cat=102"),
                                        new Video2("魔幻动漫", "魔幻动漫", WEIDUO_CARTOON_URL + "&cat=109"),
                                        new Video2("经典动漫", "经典动漫", WEIDUO_CARTOON_URL + "&cat=135"),
                                        new Video2("励志动漫", "励志动漫", WEIDUO_CARTOON_URL + "&cat=136"),
                                        new Video2("大陆动漫", "大陆动漫", WEIDUO_CARTOON_URL + "&area=10"),
                                        new Video2("日本动漫", "日本动漫", WEIDUO_CARTOON_URL + "&area=11"),
                                        new Video2("美国动漫", "美国动漫", WEIDUO_CARTOON_URL + "&area=12"),
                        })
        };

        public static final Video1[] VIDEO_CONFIG_ZD = new Video1[] {
                        new Video1("精选", true, false, new Video2[] {
                                        new Video2("电影", "电影", BASE_ZD_URL + "/index.php?cid=5&p="),
                                        new Video2("电视", "电视", BASE_ZD_URL + "/index.php?cid=12&p="),
                                        new Video2("综艺", "综艺", BASE_ZD_URL + "/index.php?cid=3&p="),
                                        new Video2("动漫", "动漫", BASE_ZD_URL + "/index.php?cid=4&p="),
                        }),
                        new Video1("电影", false, false, new Video2[] {
                                        new Video2("新片上架", "最新电影", BASE_ZD_URL + "/index.php?cid=5&p="),
                                        new Video2("动作", "动作电影", BASE_ZD_URL + "/index.php?cid=5&p="),
                                        new Video2("喜剧", "喜剧电影", BASE_ZD_URL + "/index.php?cid=6&p="),
                                        new Video2("爱情", "爱情电影", BASE_ZD_URL + "/index.php?cid=7&p="),
                                        new Video2("科幻", "科幻电影", BASE_ZD_URL + "/index.php?cid=8&p="),
                                        new Video2("恐怖", "恐怖电影", BASE_ZD_URL + "/index.php?cid=9&p="),
                                        new Video2("剧情", "剧情电影", BASE_ZD_URL + "/index.php?cid=10&p="),
                                        new Video2("战争", "战争电影", BASE_ZD_URL + "/index.php?cid=11&p=")
                        }),
                        new Video1("电视剧", false, false, new Video2[] {
                                        new Video2("新剧上新", "最新剧集", BASE_ZD_URL + "/index.php?cid=12&p="),
                                        new Video2("国产剧集", "国产剧集", BASE_ZD_URL + "/index.php?cid=12&p="),
                                        new Video2("香港剧集", "香港剧集", BASE_ZD_URL + "/index.php?cid=13&p="),
                                        new Video2("韩国剧集", "韩国剧集", BASE_ZD_URL + "/index.php?cid=14&p="),
                                        new Video2("欧美剧集", "欧美剧集", BASE_ZD_URL + "/index.php?cid=15&p="),
                                        new Video2("台湾剧集", "台湾剧集", BASE_ZD_URL + "/index.php?cid=19&p="),
                                        new Video2("日本剧集", "日本剧集", BASE_ZD_URL + "/index.php?cid=20&p="),
                                        new Video2("海外剧集", "海外剧集", BASE_ZD_URL + "/index.php?cid=21&p=")
                        }),
                        new Video1("综艺", false, false, new Video2[] {
                                        new Video2("最新综艺", "最新综艺", BASE_ZD_URL + "/index.php?cid=3&p=")
                        }),
                        new Video1("动漫", false, false, new Video2[] {
                                        new Video2("最新动漫", "最新动漫", BASE_ZD_URL + "/index.php?cid=4&p=")
                        }),
                        new Video1("其它", false, false, new Video2[] {
                                        new Video2("音乐片", "音乐片", BASE_ZD_URL + "/index.php?cid=18&p=")
                        })
        };

        public static final Video1[] VIDEO_CONFIG_OK = new Video1[] {
                        new Video1("精选", true, false, new Video2[] {
                                        new Video2("电影", "电影", BASE_OK_URL + "/index.php?cid=5&p="),
                                        new Video2("电视", "电视", BASE_OK_URL + "/index.php?cid=12&p="),
                                        new Video2("综艺", "综艺", BASE_OK_URL + "/index.php?cid=3&p="),
                                        new Video2("动漫", "动漫", BASE_OK_URL + "/index.php?cid=4&p="),
                        }),
                        new Video1("电影", false, false, new Video2[] {
                                        new Video2("新片上架", "最新电影", BASE_OK_URL + "/index.php?cid=5&p="),
                                        new Video2("动作", "动作电影", BASE_OK_URL + "/index.php?cid=5&p="),
                                        new Video2("喜剧", "喜剧电影", BASE_OK_URL + "/index.php?cid=6&p="),
                                        new Video2("爱情", "爱情电影", BASE_OK_URL + "/index.php?cid=7&p="),
                                        new Video2("科幻", "科幻电影", BASE_OK_URL + "/index.php?cid=8&p="),
                                        new Video2("恐怖", "恐怖电影", BASE_OK_URL + "/index.php?cid=9&p="),
                                        new Video2("剧情", "剧情电影", BASE_OK_URL + "/index.php?cid=10&p="),
                                        new Video2("战争", "战争电影", BASE_OK_URL + "/index.php?cid=11&p=")
                        }),
                        new Video1("电视剧", false, false, new Video2[] {
                                        new Video2("新剧上新", "最新剧集", BASE_OK_URL + "/index.php?cid=12&p="),
                                        new Video2("国产剧集", "国产剧集", BASE_OK_URL + "/index.php?cid=12&p="),
                                        new Video2("香港剧集", "香港剧集", BASE_OK_URL + "/index.php?cid=13&p="),
                                        new Video2("韩国剧集", "韩国剧集", BASE_OK_URL + "/index.php?cid=14&p="),
                                        new Video2("欧美剧集", "欧美剧集", BASE_OK_URL + "/index.php?cid=15&p="),
                                        new Video2("台湾剧集", "台湾剧集", BASE_OK_URL + "/index.php?cid=16&p="),
                                        new Video2("日本剧集", "日本剧集", BASE_OK_URL + "/index.php?cid=17&p="),
                                        new Video2("海外剧集", "海外剧集", BASE_OK_URL + "/index.php?cid=18&p=")
                        }),
                        new Video1("综艺", false, false, new Video2[] {
                                        new Video2("最新综艺", "最新综艺", BASE_OK_URL + "/index.php?cid=3&p="),
                                        new Video2("大陆综艺", "大陆综艺", BASE_OK_URL + "/index.php?cid=26&p="),
                                        new Video2("港台综艺", "港台综艺", BASE_OK_URL + "/index.php?cid=27&p="),
                                        new Video2("日韩综艺", "日韩综艺", BASE_OK_URL + "/index.php?cid=28&p="),
                                        new Video2("欧美综艺", "欧美综艺", BASE_OK_URL + "/index.php?cid=29&p=")
                        }),
                        new Video1("动漫", false, false, new Video2[] {
                                        new Video2("最新动漫", "最新动漫", BASE_OK_URL + "/index.php?cid=4&p="),
                                        new Video2("大陆动漫", "大陆动漫", BASE_OK_URL + "/index.php?cid=23&p="),
                                        new Video2("日韩动漫", "日韩动漫", BASE_OK_URL + "/index.php?cid=24&p="),
                                        new Video2("欧美动漫", "欧美动漫", BASE_OK_URL + "/index.php?cid=25&p="),
                                        new Video2("港台动漫", "港台动漫", BASE_OK_URL + "/index.php?cid=31&p="),
                                        new Video2("海外动漫", "海外动漫", BASE_OK_URL + "/index.php?cid=32&p=")
                        }),
                        new Video1("其它", false, false, new Video2[] {
                                        new Video2("纪录片", "纪录片", BASE_OK_URL + "/index.php?cid=19&p="),
                                        new Video2("微电影", "微电影", BASE_OK_URL + "/index.php?cid=20&p="),
                                        new Video2("解说", "解说", BASE_OK_URL + "/index.php?cid=33&p="),
                                        new Video2("电影解说", "电影解说", BASE_OK_URL + "/index.php?cid=34&p=")
                        })
        };

        // Method to get CMS video config dynamically (uses current BASE_URL)
        public static Video1[] getVideoConfigCms() {
                String baseUrl = getBaseCmsUrl();
                return new Video1[] {
                                new Video1("精选", true, false, new Video2[] {
                                                new Video2("Netflix电影", "电影", baseUrl + "&t=42"),
                                                new Video2("Netflix电视", "电视", baseUrl + "&t=43"),
                                                new Video2("短剧", "短剧", baseUrl + "&t=6"),
                                                new Video2("体育赛事", "体育赛事", baseUrl + "&t=7"),
                                }),
                                new Video1("电影", false, false, new Video2[] {
                                                new Video2("新片上架", "最新电影", baseUrl + "&t=1"),
                                                new Video2("动作", "动作电影", baseUrl + "&t=8"),
                                                new Video2("喜剧", "喜剧电影", baseUrl + "&t=9"),
                                                new Video2("爱情", "爱情电影", baseUrl + "&t=10"),
                                                new Video2("科幻", "科幻电影", baseUrl + "&t=11"),
                                                new Video2("恐怖", "恐怖电影", baseUrl + "&t=12"),
                                                new Video2("剧情", "剧情电影", baseUrl + "&t=13"),
                                                new Video2("战争", "战争电影", baseUrl + "&t=14"),
                                                new Video2("纪录", "纪录电影", baseUrl + "&t=20"),
                                                new Video2("Netflix", "Netflix电影", baseUrl + "&t=42")
                                }),
                                new Video1("电视剧", false, false, new Video2[] {
                                                new Video2("新剧上新", "最新剧集", baseUrl + "&t=2"),
                                                new Video2("国产剧集", "国产剧集", baseUrl + "&t=15"),
                                                new Video2("美国剧集", "美国剧集", baseUrl + "&t=18"),
                                                new Video2("韩国剧集", "韩国剧集", baseUrl + "&t=21"),
                                                new Video2("日本剧集", "日本剧集", baseUrl + "&t=22"),
                                                new Video2("泰国剧集", "泰国剧集", baseUrl + "&t=36"),
                                                new Video2("Netflix", "Netflix电视", baseUrl + "&t=43")
                                }),
                                new Video1("综艺", false, false, new Video2[] {
                                                new Video2("最新综艺", "最新综艺", baseUrl + "&t=3"),
                                                new Video2("大陆综艺", "大陆综艺", baseUrl + "&t=24"),
                                                new Video2("日韩综艺", "日韩综艺", baseUrl + "&t=25"),
                                                new Video2("港台综艺", "港台综艺", baseUrl + "&t=26")
                                }),
                                new Video1("动漫", false, false, new Video2[] {
                                                new Video2("最新动漫", "最新动漫", baseUrl + "&t=4"),
                                                new Video2("国产动漫", "国产动漫", baseUrl + "&t=28"),
                                                new Video2("日韩动漫", "日韩动漫", baseUrl + "&t=29"),
                                                new Video2("欧美动漫", "欧美动漫", baseUrl + "&t=30"),
                                                new Video2("港台动漫", "港台动漫", baseUrl + "&t=38"),
                                                new Video2("海外动漫", "海外动漫", baseUrl + "&t=39"),
                                                new Video2("有声动漫", "有声动漫", baseUrl + "&t=41")
                                })
                };
        }

        /**
         * 视频筛选
         */

        public static VideoScreenSub[] getVideoScreenCmsYear() {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int currentYear = calendar.get(java.util.Calendar.YEAR);
                java.util.List<VideoScreenSub> list = new java.util.ArrayList<>();
                list.add(new VideoScreenSub("全部", ""));
                for (int i = 0; i < 15; i++) {
                        int year = currentYear - i;
                        list.add(new VideoScreenSub(String.valueOf(year), "&year=" + year));
                }
                list.add(new VideoScreenSub("更早", "&year=other"));
                return list.toArray(new VideoScreenSub[0]);
        }

        public static final VideoScreenSub[] VIDEO_SCREEN_CMS_AREA = new VideoScreenSub[] {
                        new VideoScreenSub("全部", ""),
                        new VideoScreenSub("大陆", "&area=大陆"),
                        new VideoScreenSub("香港", "&area=香港"),
                        new VideoScreenSub("台湾", "&area=台湾"),
                        new VideoScreenSub("美国", "&area=美国"),
                        new VideoScreenSub("英国", "&area=英国"),
                        new VideoScreenSub("日本", "&area=日本"),
                        new VideoScreenSub("韩国", "&area=韩国"),
                        new VideoScreenSub("西班牙", "&area=西班牙"),
                        new VideoScreenSub("印度", "&area=印度"),
                        new VideoScreenSub("法国", "&area=法国"),
                        new VideoScreenSub("德国", "&area=德国"),
                        new VideoScreenSub("泰国", "&area=泰国"),
                        new VideoScreenSub("加拿大", "&area=加拿大"),
                        new VideoScreenSub("新加坡", "&area=新加坡"),
                        new VideoScreenSub("墨西哥", "&area=墨西哥"),
                        new VideoScreenSub("其它", "&area=其它")
        };
        public static final VideoScreenSub[] VIDEO_SCREEN_CMS_LANG = new VideoScreenSub[] {
                        new VideoScreenSub("全部", ""),
                        new VideoScreenSub("国语", "&lang=国语"),
                        new VideoScreenSub("汉语", "&lang=汉语普通话"),
                        new VideoScreenSub("英语", "&lang=英语"),
                        new VideoScreenSub("闽南语", "&lang=闽南语"),
                        new VideoScreenSub("韩语", "&lang=韩语"),
                        new VideoScreenSub("日语", "&lang=日语"),
                        new VideoScreenSub("粤语", "&lang=粤语"),
                        new VideoScreenSub("西班牙语", "&lang=西班牙语"),
                        new VideoScreenSub("其它", "&lang=其它")
        };

        // Method to get CMS video screen config dynamically (uses current BASE_URL)
        public static VideoScreen[] getVideoScreenCms() {
                String baseUrl = getBaseCmsUrl();
                return new VideoScreen[] {
                                new VideoScreen(
                                                "电影", baseUrl,
                                                new VideoScreenSub[] {
                                                                new VideoScreenSub("全部", "&t=1"),
                                                                new VideoScreenSub("动作片", "&t=8"),
                                                                new VideoScreenSub("喜剧片", "&t=9"),
                                                                new VideoScreenSub("爱情片", "&t=10"),
                                                                new VideoScreenSub("科幻片", "&t=11"),
                                                                new VideoScreenSub("恐怖片", "&t=12"),
                                                                new VideoScreenSub("剧情片", "&t=13"),
                                                                new VideoScreenSub("战争片", "&t=14"),
                                                                new VideoScreenSub("纪录片", "&t=20"),
                                                                new VideoScreenSub("悬疑片", "&t=31"),
                                                                new VideoScreenSub("动画片", "&t=32"),
                                                                new VideoScreenSub("犯罪片", "&t=33"),
                                                                new VideoScreenSub("奇幻片", "&t=34"),
                                                                new VideoScreenSub("邵氏电影", "&t=35"),
                                                                new VideoScreenSub("Netflix电影", "&t=42"),
                                                                new VideoScreenSub("动漫电影", "&t=59"),
                                                                new VideoScreenSub("惊悚片", "&t=79"),
                                                                new VideoScreenSub("家庭片", "&t=80")
                                                },
                                                getVideoScreenCmsYear(),
                                                new VideoScreenSub[] {},
                                                VIDEO_SCREEN_CMS_LANG,
                                                VIDEO_SCREEN_CMS_AREA,
                                                new VideoScreenSub[] {}),
                                new VideoScreen(
                                                "电视", baseUrl,
                                                new VideoScreenSub[] {
                                                                new VideoScreenSub("全部", "&t=2"),
                                                                new VideoScreenSub("国产剧", "&t=15"),
                                                                new VideoScreenSub("香港剧", "&t=16"),
                                                                new VideoScreenSub("台湾剧", "&t=17"),
                                                                new VideoScreenSub("美国剧", "&t=18"),
                                                                new VideoScreenSub("韩国剧", "&t=21"),
                                                                new VideoScreenSub("日本剧", "&t=22"),
                                                                new VideoScreenSub("海外剧", "&t=23"),
                                                                new VideoScreenSub("泰国剧", "&t=36"),
                                                                new VideoScreenSub("Netflix剧", "&t=43"),
                                                                new VideoScreenSub("欧美剧", "&t=57")
                                                },
                                                getVideoScreenCmsYear(),
                                                new VideoScreenSub[] {},
                                                VIDEO_SCREEN_CMS_LANG,
                                                VIDEO_SCREEN_CMS_AREA,
                                                new VideoScreenSub[] {}),
                                new VideoScreen(
                                                "综艺", baseUrl,
                                                new VideoScreenSub[] {
                                                                new VideoScreenSub("全部", "&t=3"),
                                                                new VideoScreenSub("大陆综艺", "&t=24"),
                                                                new VideoScreenSub("日韩综艺", "&t=25"),
                                                                new VideoScreenSub("港台综艺", "&t=26")
                                                },
                                                getVideoScreenCmsYear(),
                                                new VideoScreenSub[] {},
                                                VIDEO_SCREEN_CMS_LANG,
                                                VIDEO_SCREEN_CMS_AREA,
                                                new VideoScreenSub[] {}),
                                new VideoScreen(
                                                "动漫", baseUrl,
                                                new VideoScreenSub[] {
                                                                new VideoScreenSub("全部", "&t=4"),
                                                                new VideoScreenSub("国产动漫", "&t=28"),
                                                                new VideoScreenSub("日韩动漫", "&t=29"),
                                                                new VideoScreenSub("欧美动漫", "&t=30"),
                                                                new VideoScreenSub("港台动漫", "&t=38"),
                                                                new VideoScreenSub("海外动漫", "&t=39"),
                                                                new VideoScreenSub("有声动漫", "&t=41")
                                                },
                                                getVideoScreenCmsYear(),
                                                new VideoScreenSub[] {},
                                                VIDEO_SCREEN_CMS_LANG,
                                                VIDEO_SCREEN_CMS_AREA,
                                                new VideoScreenSub[] {}),
                                new VideoScreen(
                                                "短剧", baseUrl,
                                                new VideoScreenSub[] {
                                                                new VideoScreenSub("全部", "&t=6"),
                                                                new VideoScreenSub("反转爽剧", "&t=45"),
                                                                new VideoScreenSub("擦边短剧", "&t=46"),
                                                                new VideoScreenSub("短剧大全", "&t=74"),
                                                                new VideoScreenSub("爽文短剧", "&t=87")
                                                },
                                                getVideoScreenCmsYear(),
                                                new VideoScreenSub[] {},
                                                VIDEO_SCREEN_CMS_LANG,
                                                VIDEO_SCREEN_CMS_AREA,
                                                new VideoScreenSub[] {}),
                                new VideoScreen(
                                                "体育", baseUrl,
                                                new VideoScreenSub[] {
                                                                new VideoScreenSub("全部", "&t=7"),
                                                                new VideoScreenSub("篮球", "&t=37"),
                                                                new VideoScreenSub("足球", "&t=40"),
                                                                new VideoScreenSub("网球", "&t=77"),
                                                                new VideoScreenSub("斯诺克", "&t=78")
                                                },
                                                getVideoScreenCmsYear(),
                                                new VideoScreenSub[] {},
                                                VIDEO_SCREEN_CMS_LANG,
                                                VIDEO_SCREEN_CMS_AREA,
                                                new VideoScreenSub[] {})
                };
        }

        // Keep for backward compatibility - calls the method

        public static final VideoScreen[] VIDEO_SCREEN_WEIDUO = new VideoScreen[] {
                        new VideoScreen(
                                        "电影", WEIDUO_MOVIE_URL,
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("爱情", "&cat=100"),
                                                        new VideoScreenSub("动作", "&cat=106"),
                                                        new VideoScreenSub("恐怖", "&cat=102"),
                                                        new VideoScreenSub("科幻", "&cat=104"),
                                                        new VideoScreenSub("剧情", "&cat=112"),
                                                        new VideoScreenSub("犯罪", "&cat=105"),
                                                        new VideoScreenSub("奇幻", "&cat=113"),
                                                        new VideoScreenSub("战争", "&cat=108"),
                                                        new VideoScreenSub("悬疑", "&cat=115"),
                                                        new VideoScreenSub("动画", "&cat=107"),
                                                        new VideoScreenSub("文艺", "&cat=117"),
                                                        new VideoScreenSub("纪录", "&cat=118"),
                                                        new VideoScreenSub("传记", "&cat=119"),
                                                        new VideoScreenSub("歌舞", "&cat=120"),
                                                        new VideoScreenSub("古装", "&cat=121"),
                                                        new VideoScreenSub("历史", "&cat=122"),
                                                        new VideoScreenSub("惊悚", "&cat=123"),
                                                        new VideoScreenSub("其他", "&cat=other")
                                        },
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("2020", "&year=2020"),
                                                        new VideoScreenSub("2019", "&year=2019"),
                                                        new VideoScreenSub("2018", "&year=2018"),
                                                        new VideoScreenSub("2017", "&year=2017"),
                                                        new VideoScreenSub("2016", "&year=2016"),
                                                        new VideoScreenSub("2015", "&year=2015"),
                                                        new VideoScreenSub("2014", "&year=2014"),
                                                        new VideoScreenSub("2013", "&year=2013"),
                                                        new VideoScreenSub("2012", "&year=2012"),
                                                        new VideoScreenSub("2011", "&year=2011"),
                                                        new VideoScreenSub("2010", "&year=2010"),
                                                        new VideoScreenSub("2009", "&year=2009"),
                                                        new VideoScreenSub("2008", "&year=2008"),
                                                        new VideoScreenSub("2007", "&year=2007"),
                                                        new VideoScreenSub("更早", "&year=other")
                                        },
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("成龙", "&act=成龙"),
                                                        new VideoScreenSub("周星驰", "&act=周星驰"),
                                                        new VideoScreenSub("李连杰", "&act=李连杰"),
                                                        new VideoScreenSub("林正英", "&act=林正英"),
                                                        new VideoScreenSub("吴京", "&act=吴京"),
                                                        new VideoScreenSub("徐峥", "&act=徐峥"),
                                                        new VideoScreenSub("黄渤", "&act=黄渤"),
                                                        new VideoScreenSub("王宝强", "&act=王宝强"),
                                                        new VideoScreenSub("姜文", "&act=姜文"),
                                                        new VideoScreenSub("范冰冰", "&act=范冰冰"),
                                                        new VideoScreenSub("沈腾", "&act=沈腾"),
                                                        new VideoScreenSub("邓超", "&act=邓超"),
                                                        new VideoScreenSub("巩俐", "&act=巩俐"),
                                                        new VideoScreenSub("马丽", "&act=马丽"),
                                                        new VideoScreenSub("闫妮", "&act=闫妮"),
                                                        new VideoScreenSub("周冬雨", "&act=周冬雨"),
                                                        new VideoScreenSub("刘昊然", "&act=刘昊然"),
                                                        new VideoScreenSub("汤唯", "&act=汤唯"),
                                                        new VideoScreenSub("舒淇", "&act=舒淇"),
                                                        new VideoScreenSub("白百何", "&act=白百何")
                                        },
                                        new VideoScreenSub[] {},
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("大陆", "&area=10"),
                                                        new VideoScreenSub("中国香港", "&area=15"),
                                                        new VideoScreenSub("韩国", "&area=13"),
                                                        new VideoScreenSub("日本", "&area=14"),
                                                        new VideoScreenSub("美国", "&area=11"),
                                                        new VideoScreenSub("法国", "&area=12"),
                                                        new VideoScreenSub("英国", "&area=16"),
                                                        new VideoScreenSub("德国", "&area=17"),
                                                        new VideoScreenSub("中国台湾", "&area=18"),
                                                        new VideoScreenSub("泰国", "&area=21"),
                                                        new VideoScreenSub("印度", "&area=22"),
                                                        new VideoScreenSub("其它", "&area=other")
                                        },
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("最热", "&rank=rankhot"),
                                                        new VideoScreenSub("最新", "&rank=createtime"),
                                                        new VideoScreenSub("好评", "&rank=rankpoint"),
                                        }),
                        new VideoScreen(
                                        "电视", WEIDUO_TELEPLAY_URL,
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("言情", "&cat=101"),
                                                        new VideoScreenSub("剧情", "&cat=121"),
                                                        new VideoScreenSub("喜剧", "&cat=109"),
                                                        new VideoScreenSub("悬疑", "&cat=108"),
                                                        new VideoScreenSub("都市", "&cat=111"),
                                                        new VideoScreenSub("偶像", "&cat=100"),
                                                        new VideoScreenSub("古装", "&cat=104"),
                                                        new VideoScreenSub("军事", "&cat=107"),
                                                        new VideoScreenSub("警匪", "&cat=103"),
                                                        new VideoScreenSub("历史", "&cat=112"),
                                                        new VideoScreenSub("励志", "&cat=116"),
                                                        new VideoScreenSub("神话", "&cat=117"),
                                                        new VideoScreenSub("谍战", "&cat=118"),
                                                        new VideoScreenSub("青春", "&cat=119"),
                                                        new VideoScreenSub("家庭", "&cat=120"),
                                                        new VideoScreenSub("动作", "&cat=115"),
                                                        new VideoScreenSub("情景", "&cat=114"),
                                                        new VideoScreenSub("武侠", "&cat=106"),
                                                        new VideoScreenSub("科幻", "&cat=113"),
                                                        new VideoScreenSub("其他", "&cat=other")
                                        },
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("2020", "&year=2020"),
                                                        new VideoScreenSub("2019", "&year=2019"),
                                                        new VideoScreenSub("2018", "&year=2018"),
                                                        new VideoScreenSub("2017", "&year=2017"),
                                                        new VideoScreenSub("2016", "&year=2016"),
                                                        new VideoScreenSub("2015", "&year=2015"),
                                                        new VideoScreenSub("2014", "&year=2014"),
                                                        new VideoScreenSub("2013", "&year=2013"),
                                                        new VideoScreenSub("2012", "&year=2012"),
                                                        new VideoScreenSub("2011", "&year=2011"),
                                                        new VideoScreenSub("2010", "&year=2010"),
                                                        new VideoScreenSub("2009", "&year=2009"),
                                                        new VideoScreenSub("2008", "&year=2008"),
                                                        new VideoScreenSub("2007", "&year=2007"),
                                                        new VideoScreenSub("更早", "&year=other")
                                        },
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("杨幂", "&act=杨幂"),
                                                        new VideoScreenSub("热巴", "&act=热巴"),
                                                        new VideoScreenSub("张嘉译", "&act=张嘉译"),
                                                        new VideoScreenSub("赵丽颖", "&act=赵丽颖"),
                                                        new VideoScreenSub("郑爽", "&act=郑爽"),
                                                        new VideoScreenSub("赵又廷", "&act=赵又廷"),
                                                        new VideoScreenSub("胡歌", "&act=胡歌"),
                                                        new VideoScreenSub("孙俪", "&act=孙俪"),
                                                        new VideoScreenSub("韩东君", "&act=韩东君"),
                                                        new VideoScreenSub("周迅", "&act=周迅"),
                                                        new VideoScreenSub("张一山", "&act=张一山"),
                                                        new VideoScreenSub("李小璐", "&act=李小璐"),
                                                        new VideoScreenSub("吴秀波", "&act=吴秀波"),
                                                        new VideoScreenSub("李沁", "&act=李沁"),
                                                        new VideoScreenSub("陈坤", "&act=陈坤"),
                                                        new VideoScreenSub("刘亦菲", "&act=刘亦菲"),
                                                        new VideoScreenSub("唐嫣", "&act=唐嫣"),
                                                        new VideoScreenSub("李小冉", "&act=李小冉"),
                                                        new VideoScreenSub("周冬雨", "&act=周冬雨"),
                                                        new VideoScreenSub("于和伟", "&act=于和伟"),
                                                        new VideoScreenSub("李易峰", "&act=李易峰"),
                                                        new VideoScreenSub("雷佳音", "&act=雷佳音"),
                                                        new VideoScreenSub("何冰", "&act=何冰"),
                                                        new VideoScreenSub("阮经天", "&act=阮经天"),
                                                        new VideoScreenSub("梅婷", "&act=梅婷"),
                                                        new VideoScreenSub("徐峥", "&act=徐峥"),
                                                        new VideoScreenSub("祖峰", "&act=祖峰"),
                                                        new VideoScreenSub("秦海璐", "&act=秦海璐"),
                                                        new VideoScreenSub("杨紫", "&act=杨紫"),
                                                        new VideoScreenSub("任嘉伦", "&act=任嘉伦"),
                                                        new VideoScreenSub("贾乃亮", "&act=贾乃亮"),
                                                        new VideoScreenSub("罗晋", "&act=罗晋")
                                        },
                                        new VideoScreenSub[] {},
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("大陆", "&area=10"),
                                                        new VideoScreenSub("中国香港", "&area=11"),
                                                        new VideoScreenSub("中国台湾", "&area=16"),
                                                        new VideoScreenSub("韩国", "&area=12"),
                                                        new VideoScreenSub("日本", "&area=15"),
                                                        new VideoScreenSub("美国", "&area=13"),
                                                        new VideoScreenSub("英国", "&area=17"),
                                                        new VideoScreenSub("泰国", "&area=14"),
                                                        new VideoScreenSub("新加坡", "&area=18")
                                        },
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("最热", "&rank=rankhot"),
                                                        new VideoScreenSub("最新", "&rank=createtime"),
                                                        new VideoScreenSub("好评", "&rank=rankpoint"),
                                        }),
                        new VideoScreen(
                                        "综艺", WEIDUO_SHOW_URL,
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("脱口秀", "&cat=121"),
                                                        new VideoScreenSub("真人秀", "&cat=120"),
                                                        new VideoScreenSub("搞笑", "&cat=107"),
                                                        new VideoScreenSub("选秀", "&cat=101"),
                                                        new VideoScreenSub("八卦", "&cat=102"),
                                                        new VideoScreenSub("访谈", "&cat=103"),
                                                        new VideoScreenSub("情感", "&cat=104"),
                                                        new VideoScreenSub("生活", "&cat=105"),
                                                        new VideoScreenSub("晚会", "&cat=106"),
                                                        new VideoScreenSub("音乐", "&cat=108"),
                                                        new VideoScreenSub("职场", "&cat=122"),
                                                        new VideoScreenSub("美食", "&cat=123"),
                                                        new VideoScreenSub("时尚", "&cat=109"),
                                                        new VideoScreenSub("游戏", "&cat=110"),
                                                        new VideoScreenSub("少儿", "&cat=111"),
                                                        new VideoScreenSub("体育", "&cat=112"),
                                                        new VideoScreenSub("纪实", "&cat=113"),
                                                        new VideoScreenSub("科教", "&cat=114"),
                                                        new VideoScreenSub("曲艺", "&cat=115"),
                                                        new VideoScreenSub("歌舞", "&cat=116"),
                                                        new VideoScreenSub("财经", "&cat=117"),
                                                        new VideoScreenSub("汽车", "&cat=118"),
                                                        new VideoScreenSub("播报", "&cat=119"),
                                                        new VideoScreenSub("其他", "&cat=other")
                                        },
                                        new VideoScreenSub[] {},
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("邓超", "&act=邓超"),
                                                        new VideoScreenSub("陈赫", "&act=陈赫"),
                                                        new VideoScreenSub("何炅", "&act=何炅"),
                                                        new VideoScreenSub("汪涵", "&act=汪涵"),
                                                        new VideoScreenSub("王俊凯", "&act=王俊凯"),
                                                        new VideoScreenSub("黄磊", "&act=黄磊"),
                                                        new VideoScreenSub("谢娜", "&act=谢娜"),
                                                        new VideoScreenSub("黄渤", "&act=黄渤"),
                                                        new VideoScreenSub("周杰伦", "&act=周杰伦"),
                                                        new VideoScreenSub("吴亦凡", "&act=吴亦凡"),
                                                        new VideoScreenSub("赵薇", "&act=赵薇"),
                                                        new VideoScreenSub("薛之谦", "&act=薛之谦"),
                                                        new VideoScreenSub("Angelababy", "&act=Angelababy"),
                                                        new VideoScreenSub("易烊千玺", "&act=易烊千玺"),
                                                        new VideoScreenSub("岳云鹏", "&act=岳云鹏"),
                                                        new VideoScreenSub("王嘉尔", "&act=王嘉尔"),
                                                        new VideoScreenSub("鹿晗", "&act=鹿晗"),
                                                        new VideoScreenSub("杨幂", "&act=杨幂"),
                                                        new VideoScreenSub("沈腾", "&act=沈腾"),
                                                        new VideoScreenSub("罗志祥", "&act=罗志祥"),
                                                        new VideoScreenSub("张艺兴", "&act=张艺兴"),
                                                        new VideoScreenSub("潘玮柏", "&act=潘玮柏"),
                                                        new VideoScreenSub("华晨宇", "&act=华晨宇"),
                                                        new VideoScreenSub("李维嘉", "&act=李维嘉"),
                                                        new VideoScreenSub("钱枫", "&act=钱枫"),
                                                        new VideoScreenSub("宋小宝", "&act=宋小宝"),
                                                        new VideoScreenSub("贾玲", "&act=贾玲"),
                                                        new VideoScreenSub("范冰冰", "&act=范冰冰"),
                                                        new VideoScreenSub("沙溢", "&act=沙溢"),
                                                        new VideoScreenSub("撒贝宁", "&act=撒贝宁"),
                                                        new VideoScreenSub("涂磊", "&act=涂磊")
                                        },
                                        new VideoScreenSub[] {},
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("大陆", "&area=10"),
                                                        new VideoScreenSub("中国香港", "&area=15"),
                                                        new VideoScreenSub("中国台湾", "&area=11"),
                                                        new VideoScreenSub("韩国", "&area=12"),
                                                        new VideoScreenSub("日本", "&area=13"),
                                                        new VideoScreenSub("欧美", "&area=14"),
                                        },
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("最热", "&rank=rankhot"),
                                                        new VideoScreenSub("最新", "&rank=createtime"),
                                                        new VideoScreenSub("好评", "&rank=rankpoint"),
                                        }),
                        new VideoScreen(
                                        "动漫", WEIDUO_CARTOON_URL,
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("热血", "&cat=100"),
                                                        new VideoScreenSub("科幻", "&cat=134"),
                                                        new VideoScreenSub("美少女", "&cat=102"),
                                                        new VideoScreenSub("魔幻", "&cat=109"),
                                                        new VideoScreenSub("经典", "&cat=135"),
                                                        new VideoScreenSub("励志", "&cat=136"),
                                                        new VideoScreenSub("少儿", "&cat=111"),
                                                        new VideoScreenSub("冒险", "&cat=107"),
                                                        new VideoScreenSub("搞笑", "&cat=105"),
                                                        new VideoScreenSub("推理", "&cat=137"),
                                                        new VideoScreenSub("恋爱", "&cat=101"),
                                                        new VideoScreenSub("治愈", "&cat=138"),
                                                        new VideoScreenSub("幻想", "&cat=106"),
                                                        new VideoScreenSub("校园", "&cat=104"),
                                                        new VideoScreenSub("动物", "&cat=110"),
                                                        new VideoScreenSub("机战", "&cat=112"),
                                                        new VideoScreenSub("亲子", "&cat=131"),
                                                        new VideoScreenSub("儿歌", "&cat=139"),
                                                        new VideoScreenSub("运动", "&cat=103"),
                                                        new VideoScreenSub("悬疑", "&cat=108"),
                                                        new VideoScreenSub("怪物", "&cat=113"),
                                                        new VideoScreenSub("战争", "&cat=115"),
                                                        new VideoScreenSub("益智", "&cat=114"),
                                                        new VideoScreenSub("青春", "&cat=123"),
                                                        new VideoScreenSub("童话", "&cat=121"),
                                                        new VideoScreenSub("竞技", "&cat=119"),
                                                        new VideoScreenSub("动作", "&cat=126"),
                                                        new VideoScreenSub("社会", "&cat=116"),
                                                        new VideoScreenSub("友情", "&cat=117"),
                                                        new VideoScreenSub("真人版", "&cat=127"),
                                                        new VideoScreenSub("电影版", "&cat=130"),
                                                        new VideoScreenSub("OVA版", "&cat=128"),
                                                        new VideoScreenSub("TV版", "&cat=129"),
                                                        new VideoScreenSub("新番动画", "&cat=132"),
                                                        new VideoScreenSub("完结动画", "&cat=133"),
                                                        new VideoScreenSub("其他", "&cat=other")
                                        },
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("2020", "&year=2020"),
                                                        new VideoScreenSub("2019", "&year=2019"),
                                                        new VideoScreenSub("2018", "&year=2018"),
                                                        new VideoScreenSub("2017", "&year=2017"),
                                                        new VideoScreenSub("2016", "&year=2016"),
                                                        new VideoScreenSub("2015", "&year=2015"),
                                                        new VideoScreenSub("2014", "&year=2014"),
                                                        new VideoScreenSub("2013", "&year=2013"),
                                                        new VideoScreenSub("2012", "&year=2012"),
                                                        new VideoScreenSub("2011", "&year=2011"),
                                                        new VideoScreenSub("2010", "&year=2010"),
                                                        new VideoScreenSub("2009", "&year=2009"),
                                                        new VideoScreenSub("2008", "&year=2008"),
                                                        new VideoScreenSub("2007", "&year=2007"),
                                                        new VideoScreenSub("2006", "&year=2006"),
                                                        new VideoScreenSub("2005", "&year=2005"),
                                                        new VideoScreenSub("2004", "&year=2004"),
                                                        new VideoScreenSub("更早", "&year=other")
                                        },
                                        new VideoScreenSub[] {},
                                        new VideoScreenSub[] {},
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("大陆", "&area=10"),
                                                        new VideoScreenSub("日本", "&area=11"),
                                                        new VideoScreenSub("美国", "&area=12")
                                        },
                                        new VideoScreenSub[] {
                                                        new VideoScreenSub("全部", ""),
                                                        new VideoScreenSub("最热", "&rank=rankhot"),
                                                        new VideoScreenSub("最新", "&rank=createtime"),
                                                        new VideoScreenSub("好评", "&rank=rankpoint"),
                                        })
        };
}
