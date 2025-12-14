package com.pxf.fftv.plus.model;

import android.content.Context;

import com.pxf.fftv.plus.model.account.AccountModel;
import com.pxf.fftv.plus.model.video.cms.CMSSearchEngine;
import com.pxf.fftv.plus.model.video.cms.CMSVideoEngine;
import com.pxf.fftv.plus.model.video.ok.OkVideoEngine;
import com.pxf.fftv.plus.model.video.weiduo.WeiduoSearchEngine;
import com.pxf.fftv.plus.model.video.weiduo.WeiduoVideoEngine;
import com.pxf.fftv.plus.model.video.zd.ZdVideoEngine;

import static com.pxf.fftv.plus.Const.VIDEO_2;
import static com.pxf.fftv.plus.Const.VIDEO_3;
import static com.pxf.fftv.plus.Const.VIDEO_1;
import static com.pxf.fftv.plus.Const.VIDEO_4;

public class Model {

    public static DataModel getData() {
        return DataModel.getInstance();
    }

    public static IVideoEngine getVideoEngine(Context context) {
        switch (getData().getVideoEngine(context)) {
            case VIDEO_1:
                return WeiduoVideoEngine.getInstance();
            case VIDEO_2:
                return CMSVideoEngine.getInstance();
            case VIDEO_3:
                return OkVideoEngine.getInstance();
            case VIDEO_4:
                return ZdVideoEngine.getInstance();
            default:
                return CMSVideoEngine.getInstance();
        }
    }

    public static ISearchEngine getSearchEngine(Context context) {
        switch (getData().getVideoEngine(context)) {
            case VIDEO_1:
                return WeiduoSearchEngine.getInstance();
            case VIDEO_2:
                return CMSSearchEngine.getInstance();
            case VIDEO_3:
            case VIDEO_4:
                return CMSSearchEngine.getInstance();
            default:
                return CMSSearchEngine.getInstance();
        }
    }

    public static IAccountModel getAccountModel() {
        return AccountModel.getInstance();
    }
}
