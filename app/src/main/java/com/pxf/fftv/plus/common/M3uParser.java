package com.pxf.fftv.plus.common;

import com.pxf.fftv.plus.bean.M3uChannel;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * M3U播放列表解析器
 */
public class M3uParser {

    // 提取各种属性
    private static final Pattern TVG_NAME_PATTERN = Pattern.compile("tvg-name=\"([^\"]*)\"");
    private static final Pattern TVG_LOGO_PATTERN = Pattern.compile("tvg-logo=\"([^\"]*)\"");
    private static final Pattern GROUP_TITLE_PATTERN = Pattern.compile("group-title=\"([^\"]*)\"");

    /**
     * 解析M3U内容
     * 
     * @param content M3U文件内容
     * @return 按分组组织的频道列表
     */
    public static Map<String, List<M3uChannel>> parse(String content) {
        Map<String, List<M3uChannel>> groups = new LinkedHashMap<>();

        if (content == null || content.isEmpty()) {
            return groups;
        }

        try {
            BufferedReader reader = new BufferedReader(new StringReader(content));
            String line;
            M3uChannel currentChannel = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // 支持 #EXTINF: 和 # EXTINF:
                if (line.contains("EXTINF:")) {
                    currentChannel = new M3uChannel();

                    // 提取 tvg-name
                    Matcher nameMatcher = TVG_NAME_PATTERN.matcher(line);
                    if (nameMatcher.find()) {
                        currentChannel.setName(nameMatcher.group(1));
                    }

                    // 提取 tvg-logo
                    Matcher logoMatcher = TVG_LOGO_PATTERN.matcher(line);
                    if (logoMatcher.find()) {
                        currentChannel.setLogo(logoMatcher.group(1));
                    }

                    // 提取 group-title
                    Matcher groupMatcher = GROUP_TITLE_PATTERN.matcher(line);
                    if (groupMatcher.find()) {
                        currentChannel.setGroup(groupMatcher.group(1));
                    }

                    // 如果没有 tvg-name，取逗号后的名称
                    if (currentChannel.getName() == null || currentChannel.getName().isEmpty()) {
                        int commaIndex = line.lastIndexOf(",");
                        if (commaIndex > 0) {
                            currentChannel.setName(line.substring(commaIndex + 1).trim());
                        }
                    }

                    // 默认分组
                    if (currentChannel.getGroup() == null || currentChannel.getGroup().isEmpty()) {
                        currentChannel.setGroup("其他");
                    }

                } else if (line.startsWith("http") && currentChannel != null) {
                    currentChannel.setUrl(line);

                    String group = currentChannel.getGroup();
                    if (group == null || group.isEmpty()) {
                        group = "其他";
                    }

                    if (!groups.containsKey(group)) {
                        groups.put(group, new ArrayList<>());
                    }
                    groups.get(group).add(currentChannel);

                    currentChannel = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return groups;
    }

    /**
     * 获取所有频道（扁平列表）
     */
    public static List<M3uChannel> parseFlat(String content) {
        List<M3uChannel> channels = new ArrayList<>();
        Map<String, List<M3uChannel>> groups = parse(content);
        for (List<M3uChannel> list : groups.values()) {
            channels.addAll(list);
        }
        return channels;
    }
}
