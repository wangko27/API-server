package io.nuls.api.server.resource;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Transaction;
import io.nuls.api.utils.JSONUtils;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.RpcTransferUtil;
import io.nuls.api.utils.log.Log;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (necessary) need a running server, otherwise pls skip it.
 */
public class ReportResourceTest {

    private static RestFulUtils restFulUtils;

    @BeforeClass
    public static void init() {
        String serverUri = "http://127.0.0.1:8001";
        restFulUtils = RestFulUtils.getInstance();
        restFulUtils.init(serverUri);
    }

    @Test
    public void blockTest() {
        long height = 0;
        RpcClientResult result = restFulUtils.get("/block/header/height/" + height, null);
        try {
            BlockHeader blockHeader = RpcTransferUtil.toBlockHeader((Map<String, Object>) result.getData());
            System.out.println(blockHeader.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void txTest() {
        String hash = "0020272a4790d6284e71d4f708e1ea5e15bce6b74635fdb160c9561908fff0c6c54d";
        RpcClientResult result = RestFulUtils.getInstance().get("/tx/hash/" + hash, null);
        Log.debug(result.toString());
        Assert.assertEquals("SYS000", result.getCode());
        try {
            Transaction tx = RpcTransferUtil.toTransaction((Map<String, Object>) result.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMapToJson() throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("aaa","aaa");
        map.put("bbb", 1);

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        map.put("list",list);
        System.out.println(JSONUtils.obj2json(map));
    }

}

