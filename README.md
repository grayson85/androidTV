# 此应用根据FFTVPlus来修改

# 名称
全局搜索“乐看视频”的位置都更改

# 图标（注意尺寸尽量一致）
app/src/res-launcher/drawable/ic_launcher.png

# 启动图（注意尺寸尽量一致）
app/src/res-pic/drawable/pic_launcher.png

# 视频列表和筛选的域名配置
app/src/main/java/com/pxf/fftv/plus/VideoConfig.java

# 登录注册等后台域名
app/src/main/java/com/pxf/fftv/plus/Const.java 11行

# 电视直播列表数据来源
app/src/main/java/com/pxf/fftv/plus/contract/live/IjkTVLiveActivity.java 120行

# 线路及播放器名称
app/src/main/res/layout/activity_setting.xml

# 注意
此app需要配合更改过的MACCMS api才能完美运行。需要的朋友可以私信我。

# 修复内容
i) https源无法显示图片

ii) 影片数据加载错误

iii) 移除release版需要登入才能播放影片

iv) 添加公告

# 未来修改
i) 影片多源显示

ii) 影片集数分类

![image](https://user-images.githubusercontent.com/37401242/189471881-e38ac0ac-e2ee-4f3e-8262-9da58fa970bc.png)
![image](https://user-images.githubusercontent.com/37401242/189471893-81a78f13-772f-493a-ae55-f886fdbec0a1.png)
