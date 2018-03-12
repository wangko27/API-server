package io.nuls.api.utils;

import java.util.Properties;

public class PropertiesUtils {
    private static Properties properties=new Properties();

    static{
        try {
            properties.load(PropertiesUtils.class.getClassLoader().getResourceAsStream("sys.properties"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String readProperty(String property, String defaultValue){
        return properties.getProperty(property, defaultValue);
    }

    public static String readProperty(String property){
        return properties.getProperty(property);
    }
}
