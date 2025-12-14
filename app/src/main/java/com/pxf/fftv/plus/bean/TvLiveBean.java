package com.pxf.fftv.plus.bean;

import java.util.List;

public class TvLiveBean {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * title : 央视频道
         * sub : [{"title":"CCTV-1综合HD","url":"http://39.135.34.151:18890/000000001000/1000000001000021973/1.m3u8?channel-id=ystenlive&Contentid=1000000001000021973&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=4ef1f6fdd53988bdf19472c73151206f21vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000021973&owchid=ystenlive&owsid=1106497909461769539&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRLaQJeR5usCQMKdpIDCZAoYPt4bOuuiUwGxs8%2fKxpb7Wa3xqB26AcGEvjhx3JJlw6"},{"title":"CCTV-2财经","url":"http://39.135.34.150:18890/000000001000/1000000001000012442/1.m3u8?channel-id=ystenlive&Contentid=1000000001000012442&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=4ef1f6fdd53988bdf19472c73151206f21vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000012442&owchid=ystenlive&owsid=1106497909461775063&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRknGE%2fO0VC9Co37x%2f7gMjaWYjatUfTQlAa15Ksg%2fXe8sQo%2fi5btdpzeV%2b1v4UwuHf"},{"title":"CCTV-3综艺","url":"http://39.135.34.151:18890/000000001000/1000000001000011218/1.m3u8?channel-id=ystenlive&Contentid=1000000001000011218&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=4ef1f6fdd53988bdf19472c73151206f21vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000011218&owchid=ystenlive&owsid=1106497909461779572&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRrAyJU4rR1Dadw9ISEYm5oBbA9lSzNfT0W7kMLAWHUBTbBAjpiIN0Pdi%2fTRm3zPoh"},{"title":"CCTV-4国际","url":"http://39.135.34.150:18890/000000001000/1000000002000031664/1.m3u8?channel-id=ystenlive&Contentid=1000000002000031664&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000002000031664&owchid=ystenlive&owsid=1106497909461056460&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE5oj7lZFbEKIj3xJcvQPkjhSaNRr4LzI3YsXQPQcGzS6VSIZXSs858JtCAdvl%2fg%2f2u5lawXOBSX%2fqESSB5FmTXS"},{"title":"CCTV-5体育","url":"http://39.135.34.146:18890/000000001000/1000000001000004794/1.m3u8?channel-id=ystenlive&contentid=1000000001000004794&livemode=1&stbid=005203ff000360100001001a34c0cd33&usertoken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000004794&owchid=ystenlive&owsid=1106497909461092492&authinfo=yolxjswzzffv3fvb8mhhuelkgjklbu5h0jb3qahfse7aoraovdzdwbfnj0sxjearfiatofcd93c7ach3puyar1v%2bioi39cfseenlxp5u9ufadu6bcdl7v7ctgkugzw1d"},{"title":"CCTV-6高清","url":"http://39.135.34.140:18890/000000001000/1000000001000016466/1.m3u8?channel-id=ystenlive&Contentid=1000000001000016466&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000016466&owchid=ystenlive&owsid=1106497909461115566&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRPL4aTeGJNlZwIkZROfGAKEC3iG4uOUkYNIdPRliCqBi8AjSQtKTBHB8b3LPXJcIA"},{"title":"CCTV-7军事农业","url":"http://39.135.34.152:18890/000000001000/7050628689018054317/1.m3u8?channel-id=ystenlive&Contentid=7050628689018054317&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=7050628689018054317&owchid=ystenlive&owsid=1106497909461123525&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE5v8WYfJSLSoSDQSshIAsx77pE15UEXEdMCmKIrL5%2f3xV22iBl6KMm%2bLcZrmbA5H1KmAD8EY%2bWAeRUs2LJ9Vxxk"},{"title":"CCTV-8高清","url":"http://39.135.34.154:18890/000000001000/1000000001000003736/1.m3u8?channel-id=ystenlive&Contentid=1000000001000003736&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000003736&owchid=ystenlive&owsid=1106497909461132718&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRKhIjYL3aRNrIf2qoTcK90Bd0oe1gvWxxvd9Qs9pkD5eKCeYPeHs2GppHtnd1klhd"},{"title":"CCTV-9纪录","url":"http://39.135.34.145:18890/000000001000/1000000001000014583/1.m3u8?channel-id=ystenlive&Contentid=1000000001000014583&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000014583&owchid=ystenlive&owsid=1106497909461138654&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRh3XYVWmjehcJ6eeMnvhxAUHggQtCK8Xdm86HMC53JU75N56tG8kTzA%2fMocL0w%2bEi"},{"title":"CCTV-10科教","url":"http://39.135.34.151:18890/000000001000/7019587760656900133/1.m3u8?channel-id=ystenlive&Contentid=7019587760656900133&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=7019587760656900133&owchid=ystenlive&owsid=1106497909461152716&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE5DtJGdM%2bq9fDiivHNjKl%2bK%2b9%2fvTG5xUEzd4ADPtXjiSOOAPl07bvHVEc1KInbp3p%2fXrmwsjTHZ%2fLcoxEWqnCxI"},{"title":"CCTV-11戏曲","url":"http://39.135.34.157:18890/000000001000/1000000002000019789/1.m3u8?channel-id=ystenlive&Contentid=1000000002000019789&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000002000019789&owchid=ystenlive&owsid=1106497909461158507&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE5oj7lZFbEKIj3xJcvQPkjhUELVtb8TDQzMSm5TScAJJF9JU4YcW5z4YHYDU6y7yn5EMhEqmI%2fAHiE6j4Y95QQ6"},{"title":"CCTV-12社会与法","url":"http://39.135.34.153:18890/000000001000/5325631075193490169/1.m3u8?channel-id=ystenlive&Contentid=5325631075193490169&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=5325631075193490169&owchid=ystenlive&owsid=1106497909461165978&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7Q36zc4fu7NwQ%2fn1v2mTHqagOL8gLhzmY66HLCxgy24bGH5Xn1D6yEQdwViWmtBz15WRKuKxWsf0XH20JE0dby"},{"title":"CCTV-13新闻","url":"http://39.135.34.142:18890/000000001000/1000000002000021303/1.m3u8?channel-id=ystenlive&Contentid=1000000002000021303&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000002000021303&owchid=ystenlive&owsid=1106497909461172796&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE5oj7lZFbEKIj3xJcvQPkjht3HVAbnHsmZNUh9hfpXto6CyOW1rkA%2biXZhxwyg0QWyFIN7oKbswdBdf7iWy8vlE"},{"title":"CCTV-14少儿","url":"http://39.135.34.147:18890/000000001000/8203666801302077036/1.m3u8?channel-id=ystenlive&Contentid=8203666801302077036&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=8203666801302077036&owchid=ystenlive&owsid=1106497909461179285&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE5OdsPl7aB2c%2fh9IG5fEu5Y1yWuhr6PsL8ovQK5JmdmvjbSpXjguwCa8emDxg5lQPKN6B6go0yKDtys3NmnOM6U"},{"title":"CCTV-15音乐","url":"http://39.135.34.158:18890/000000001000/1000000002000008163/1.m3u8?channel-id=ystenlive&Contentid=1000000002000008163&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000002000008163&owchid=ystenlive&owsid=1106497909461186580&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE5oj7lZFbEKIj3xJcvQPkjhY20qRt7%2f617UUdZqFKeX8uE3lV%2bUYqhdkQW8TTtgMeMYnBWivLOAtJKqm6UM8Epy"},{"title":"CCTV-5+体育","url":"http://39.135.34.152:18890/000000001000/1000000001000020505/1.m3u8?channel-id=ystenlive&Contentid=1000000001000020505&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000020505&owchid=ystenlive&owsid=1106497909461103025&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRkrZ1YVus93dDbxNB4SF1DDcF4ZpIL8iGHt%2bMdt2ZAOlknlq9lyy%2fQZJVqiFUxfle"},{"title":"CCTV-17农村农业","url":"http://39.135.34.140:18890/000000001000/1000000005000056836/1.m3u8?channel-id=ystenlive&Contentid=1000000005000056836&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000005000056836&owchid=ystenlive&owsid=1106497909461196027&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE6skxOPttMtBQzbEMb71jMrM9bNA5sjuldmGASVEj1FB1TOzqCEWAK6w%2FJbtYz8kEYu99sUSfTSd48Av%2FzOzjC1"}]
         */

        private String title;
        private List<SubBean> sub;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<SubBean> getSub() {
            return sub;
        }

        public void setSub(List<SubBean> sub) {
            this.sub = sub;
        }

        public static class SubBean {
            /**
             * title : CCTV-1综合HD
             * url : http://39.135.34.151:18890/000000001000/1000000001000021973/1.m3u8?channel-id=ystenlive&Contentid=1000000001000021973&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=4ef1f6fdd53988bdf19472c73151206f21vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000021973&owchid=ystenlive&owsid=1106497909461769539&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRLaQJeR5usCQMKdpIDCZAoYPt4bOuuiUwGxs8%2fKxpb7Wa3xqB26AcGEvjhx3JJlw6
             */

            private String title;
            private String url;

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
    }
}
