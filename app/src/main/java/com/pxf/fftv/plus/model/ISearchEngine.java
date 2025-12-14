package com.pxf.fftv.plus.model;

import com.pxf.fftv.plus.model.video.Video;

import java.util.ArrayList;

public interface ISearchEngine {

    ArrayList<Video> getVideoListFromJson(String words);
}
