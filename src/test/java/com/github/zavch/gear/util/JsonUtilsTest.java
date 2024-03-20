package com.github.zavch.gear.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonUtilsTest {

    @Test
    public void format() {
        String jsonStr = "{\"name\": \"test\", \"array\": [{\"字符串\": \"abc\", \"数字\": 1}]}";
        String result = """
                {
                    "name": "test",
                    "array": [
                        {
                            "字符串": "abc",
                            "数字": 1
                        }
                    ]
                }
                """.replace("\n", System.lineSeparator()).trim();
        Assertions.assertEquals(result, JsonUtils.format(jsonStr).trim());
    }
}
