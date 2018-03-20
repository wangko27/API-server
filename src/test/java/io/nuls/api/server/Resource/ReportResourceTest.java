package io.nuls.api.server.Resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.log.Log;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * (necessary) need a running server, otherwise pls skip it.
 */
public class ReportResourceTest {

    @BeforeClass
    public static void init() {
        String serverUri = "http://127.0.0.1:8765/nuls";
        RestFulUtils.getInstance().init(serverUri);
    }

    @Test
    public void balanceTest() {
        Map<String, String> map = new HashMap<>();
        map.put("pageNumber", "1");
        map.put("pageSize", "101");
        RpcClientResult result = RestFulUtils.getInstance().get("/address/balancelist", map);
        Log.debug(result.toString());
        Assert.assertEquals("SYS000", result.getCode());
    }

    @Test
    public void minedTest() {
        Map<String, String> map = new HashMap<>();
        map.put("pageNumber", "2");
        map.put("pageSize", "30");
        RpcClientResult result = RestFulUtils.getInstance().get("/address/minedlist", map);
        Log.debug(result.toString());
        Assert.assertEquals("SYS000", result.getCode());
    }

    @Test
    public void txHistoryTest() {
        RpcClientResult result = RestFulUtils.getInstance().get("/txhistory", null);
        Log.debug(result.toString());
        Assert.assertEquals("SYS000", result.getCode());
    }
}

