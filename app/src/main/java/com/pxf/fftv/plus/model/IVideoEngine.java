package com.pxf.fftv.plus.model;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;

public interface IVideoEngine {

    ArrayList<Video> getVideos(Const.VideoType type, int page);

    ArrayList<Video> getVideos(VideoEngineParam param, int page);
}
