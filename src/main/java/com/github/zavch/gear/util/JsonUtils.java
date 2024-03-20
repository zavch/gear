package com.github.zavch.gear.util;

import java.util.Arrays;
import java.util.List;

public class JsonUtils {
    private static final List<Character> WRAP_CHARS = Arrays.asList('{', '}', '[', ']', ',');
    public static final String SPACE = " ";

    /**
     * 格式化JSON字符串
     *
     * @param jsonStr
     * @return
     */
    public static String format(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return jsonStr;
        }
        StringBuilder builder = new StringBuilder();
        boolean isContent = false;
        int spaceNumber = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (c == '"') {
                isContent = !isContent;
            } else if (Character.isSpaceChar(c) && !isContent) {
                // 去除不需要的空格
                continue;
            }

            if (WRAP_CHARS.contains(c)) {
                switch (c) {
                    case '{':
                    case '[':
                        builder.append(c);
                        builder.append(System.lineSeparator());
                        builder.append(SPACE.repeat(spaceNumber += 4));
                        break;
                    case '}':
                    case ']':
                        builder.append(System.lineSeparator());
                        builder.append(SPACE.repeat(spaceNumber -= 4));
                        builder.append(c);
                        break;
                    case ',':
                        builder.append(c);
                        builder.append(System.lineSeparator());
                        builder.append(SPACE.repeat(spaceNumber));
                }
            } else {
                builder.append(c);
                if (c == ':') {
                    builder.append(SPACE);
                }
            }
        }
        return builder.toString();
    }
}
