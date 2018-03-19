package io.nuls.api.utils;

import io.nuls.api.utils.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
    private static Properties properties=new Properties();

    static{
        InputStream in = null;
        try {
            in = PropertiesUtils.class.getClassLoader().getResourceAsStream("sys.properties");
            properties.load(in);
        } catch (Exception e) {
            Log.error(e);
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //skip
                }
            }

        }
    }

    public static String readProperty(String property, String defaultValue){
        return properties.getProperty(property, defaultValue);
    }

    public static String readProperty(String property){
        return properties.getProperty(property);
    }
}
