# SMTVPlus - Android TV 视频应用

基于 FFTVPlus 修改的 Android TV 视频播放应用，支持 MacCMS V10 API 和 StreamCore API。

[English](#english) | [中文](#中文)

---

## 中文

### 📱 功能特性

- 🎬 **MacCMS V10 兼容** - 支持标准 MacCMS 采集 API
- 📺 **IPTV 直播** - 支持 M3U 格式直播源
- 🔍 **智能搜索** - 支持首字母拼音搜索
- 🎮 **D-Pad 导航** - 完美支持遥控器操作
- 📱 **双端适配** - 同时支持 TV 和手机
- 🎨 **现代 UI** - 全新设计的界面风格

### 🛠️ 配置指南

#### 1. 应用名称
全局搜索 "乐看视频" 并替换为您的应用名称

#### 2. 应用图标
`app/src/res-launcher/drawable/ic_launcher.png`

#### 3. 启动图
`app/src/res-pic/drawable/pic_launcher.png`

#### 4. CMS 域名配置
`app/src/main/java/com/pxf/fftv/plus/Const.java` 第 12 行

```java
public static String BASE_URL = "https://你的域名/";
```

#### 5. 视频列表配置
`app/src/main/java/com/pxf/fftv/plus/VideoConfig.java`

#### 6. 电视直播数据源
`app/src/main/java/com/pxf/fftv/plus/contract/live/IjkTVLiveActivity.java` 第 120 行

#### 7. IPTV 直播源
设置 → IPTV → 编辑源地址

### 📡 API 要求

此应用需要配合修改过的 MacCMS API 或 StreamCore API 才能完美运行。

#### MacCMS API 扩展（可选）

参考：[maccmsAPI/Provide.php](maccmsAPI/Provide.php)

```
# 主分类查询
GET /api.php/provide/vod/?ac=detail&t1=1

# 推荐影片查询
GET /api.php/provide/vod/?ac=detail&lvl=2

# 首字母搜索
GET /api.php/provide/vod/?ac=detail&word=HZW
```

#### StreamCore API（推荐）

完全兼容 MacCMS V10 标准，无需额外修改：
- GitHub: [grayson85/streamCore](https://github.com/grayson85/streamCore)

### ✅ 修复内容

| 版本 | 更新内容 |
|------|---------|
| 2024-12 | 🆕 IPTV M3U 直播支持 |
| 2024-12 | 🆕 现代化对话框 UI |
| 2024-12 | 🆕 搜索结果焦点优化 |
| 2024-12 | 🆕 Top Bar 时间日期显示 |
| 2024-12 | 🆕 多播放源支持优化 |
| 2022-09 | 新增倒序功能 |
| 2022-09 | 新增 ijkPlayer 手机支持 |
| 2022-09 | 修正搜索后滑动失焦问题 |
| 2022-09 | 新增视频多源选择 |
| - | 修复 HTTPS 图片无法显示 |
| - | 修复影片数据加载错误 |
| - | 移除 Release 版登录限制 |
| - | 添加公告功能 |

### 🏗️ 编译

```bash
# Debug 版本
./gradlew assembleDebug

# Release 版本
./gradlew assembleRelease
```

APK 输出路径：`app/build/outputs/apk/`

### 📦 技术栈

- **ExoPlayer** - 主播放器
- **IJKPlayer** - 备用播放器
- **Glide** - 图片加载
- **EventBus** - 事件总线
- **ViewBinding** - 视图绑定

---

## English

### 📱 Features

- 🎬 **MacCMS V10 Compatible** - Standard MacCMS collection API support
- 📺 **IPTV Live** - M3U format live streaming support
- 🔍 **Smart Search** - Pinyin initial search support
- 🎮 **D-Pad Navigation** - Perfect remote control support
- 📱 **Dual Platform** - TV and Phone support
- 🎨 **Modern UI** - Redesigned interface

### 🛠️ Configuration

1. **App Name**: Search and replace "乐看视频"
2. **App Icon**: `app/src/res-launcher/drawable/ic_launcher.png`
3. **Splash Image**: `app/src/res-pic/drawable/pic_launcher.png`
4. **CMS URL**: `app/src/main/java/com/pxf/fftv/plus/Const.java` line 12

### 📡 API Requirements

Works with:
- Modified MacCMS API (see `maccmsAPI/Provide.php`)
- StreamCore API (recommended): [grayson85/streamCore](https://github.com/grayson85/streamCore)

### 🏗️ Build

```bash
./gradlew assembleRelease
```

### 📄 License

MIT License
