/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.api.i18n;

import com.alibaba.druid.util.JdbcUtils;
import io.nuls.api.constant.ErrorCode;
import io.nuls.api.constant.Constant;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Niels
 * @date 2017/9/27
 *
 */
public class I18nUtils {

    private static final Map<String, Properties> ALL_MAPPING = new HashMap<>();
    private static Properties nowMapping = null;
    /**
     * default language is English
     */

    private static String key = "en";
    /**
     * default properties file folder
     */
    private static final String[] FOLDER = {"languages/en.properties", "languages/zh-cn.properties"};

    static {
        //load all language properties
        InputStream input = null;
        InputStreamReader reader = null;
        try {
            for (String file : FOLDER) {
                input = I18nUtils.class.getClassLoader().getResourceAsStream(file);
                Properties prop = new Properties();
                reader = new InputStreamReader(input, Constant.DEFAULT_ENCODING);
                prop.load(reader);
                JdbcUtils.close(reader);
                JdbcUtils.close(input);
                String key = file.replace(".properties", "").replace("languages/", "");
                ALL_MAPPING.put(key, prop);
            }
        } catch (IOException e) {
            Log.error(e);
        } finally {
            JdbcUtils.close(reader);
            JdbcUtils.close(input);
        }
    }

    public static void setLanguage(String lang) throws Exception {
        if (StringUtils.isBlank(lang)) {
            throw new Exception(ErrorCode.LANGUAGE_CANNOT_SET_NULL.getCode());
        }
        key = lang;
        nowMapping = ALL_MAPPING.get(lang);
    }

    public static String get(int id) {
        if (nowMapping == null) {
            nowMapping = ALL_MAPPING.get(key);
        }
        return nowMapping.getProperty(id + "");
    }
}
