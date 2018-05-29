package io.nuls.api;

import io.nuls.api.utils.JSONUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CommonTest {

    @Test
    public void testMapToString() {
        Map<String,Object> map = new HashMap<>();
        map.put("aaa",1);
        map.put("bbb", "bbb");
        try {
            System.out.println(JSONUtils.obj2json(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
