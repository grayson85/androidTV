# 此应用根据FFTVPlus来修改

# 名称
全局搜索“乐看视频”的位置都更改

# 图标（注意尺寸尽量一致）
app/src/res-launcher/drawable/ic_launcher.png

# 启动图（注意尺寸尽量一致）
app/src/res-pic/drawable/pic_launcher.png

# 视频列表和筛选的域名配置
app/src/main/java/com/pxf/fftv/plus/VideoConfig.java
![image](https://user-images.githubusercontent.com/37401242/192079441-06fad969-c99e-471f-8c45-4f5afcc644bf.png)

app/src/main/java/com/pxf/fftv/plus/model/video/cms/CMSVideoEngine.java
![image](https://user-images.githubusercontent.com/37401242/192079487-9ebfab5f-4da1-417b-8e2f-b904817a2f47.png)


# CMS域名
app/src/main/java/com/pxf/fftv/plus/Const.java 12行

# 电视直播列表数据来源
app/src/main/java/com/pxf/fftv/plus/contract/live/IjkTVLiveActivity.java 120行

# 线路及播放器名称
app/src/main/res/layout/activity_setting.xml

# 注意
此app需要配合更改过的MACCMS api才能完美运行。需要的朋友可以私信我。
Maccms API新增如下:
i) MagicBlack版本API只能查询子分类。修改后API能支持主分类查询

https://【你的域名】/api.php/provide/vod/?ac=detail&t1=1

ii) 修改后API能支持推荐影片查询

https://【你的域名】/api.php/provide/vod/?ac=detail&lvl=2

iii) 新增首字母查询

https://【你的域名】/api.php/provide/vod/?ac=detail&word=HZW


# 修复内容
i) https源无法显示图片

ii) 影片数据加载错误

iii) 移除release版需要登入才能播放影片

iv) 添加公告

v) 20220910 - 新增倒序功能 (Added new feature sorting)

vi) 20220910 - 新增ijkPlayer支持移动设备 (Added new feature ijkPlayer for Phone) - 还在持续更新中

vii) 20200919 - 修正搜索后滑动失焦问题 (Fixed scroll down lose focus)

viii) 20220923 - 新增视频多源选择 Added new feature indicate multiple source

# 未来修改
i) 影片集数分类

![image](https://user-images.githubusercontent.com/37401242/189471881-e38ac0ac-e2ee-4f3e-8262-9da58fa970bc.png)
![image](https://user-images.githubusercontent.com/37401242/189471893-81a78f13-772f-493a-ae55-f886fdbec0a1.png)
![image](https://user-images.githubusercontent.com/37401242/192080099-8e607eaf-d3d8-4965-8fd3-d19a953238c1.png)
